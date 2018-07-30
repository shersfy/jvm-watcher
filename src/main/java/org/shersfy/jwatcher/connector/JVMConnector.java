package org.shersfy.jwatcher.connector;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.shersfy.jwatcher.entity.GarbageCollector;
import org.shersfy.jwatcher.entity.MemoSegment;
import org.shersfy.jwatcher.entity.MemoUsage;
import org.shersfy.jwatcher.service.SystemInfoService;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class JVMConnector {

	/**jmx连接串**/
	private String url;
	/**启用监听**/
	private AtomicBoolean enable;
	/**监听最大Segments缓存数**/
	private AtomicInteger maxSegSize;
	/**监听时间间隔(毫秒)**/
	private AtomicLong interval;
	/**更新时间 连接超时判定用**/
	private AtomicLong updatetime;
	/**jmx连接**/
	private JMXConnector jmxConnector;
	/**监听jvm不同时刻的内存数据**/
	private ConcurrentLinkedQueue<MemoSegment> memoSegments;
	
	private JVMConnector(){
		if(SystemInfoService.conf!=null){
			this.jmxConnector = SystemInfoService.conf.getLocalDefault();
		}
	}
	
	/**
	 * 获取JMX连接器
	 * @param url 连接串
	 * @return JVMConnector
	 * @throws IOException 
	 */
	public synchronized static JVMConnector getConnector(String url) throws IOException{
		JVMConnector obj = null;
		if(StringUtils.isNotBlank(url) && SystemInfoService.conf!=null){
			obj = SystemInfoService.conf.getCache().get(url);
		}
		if(obj!=null){
			return obj;
		}
		
		if(StringUtils.isBlank(url)){
			throw new IOException("url error: "+url);
		}
		
		obj = new JVMConnector();
		obj.url = url;
		if(isLocal(url)){
			try {
				long pid = Long.parseLong(url.split("/")[1].trim());
				obj.jmxConnector = getLocalConnector(pid);
			} catch (IOException e) {
				throw e;
			} catch (NumberFormatException e) {
				throw new IOException("url error: "+url);
			} catch (Exception e) {
				throw new IOException(e);
			}
		} else {
			JMXServiceURL serviceURL = new JMXServiceURL(url);
			obj.jmxConnector = JMXConnectorFactory.connect(serviceURL);
		}
		
		obj.jmxConnector.connect();
		obj.updatetime();
		if(SystemInfoService.conf!=null){
			SystemInfoService.conf.getCache().put(url, obj);
		}
		
		obj.setEnable(false);
		obj.setInterval(new AtomicLong(5000));
		obj.setMaxSegSize(new AtomicInteger(1));
		obj.memoSegments = new ConcurrentLinkedQueue<>();
		
		return obj;
	}
	
	
	public static JMXConnector getLocalConnector(long pid) throws AttachNotSupportedException, IOException{
		VirtualMachine jvm = VirtualMachine.attach(String.valueOf(pid));
		String key  = "com.sun.management.jmxremote.localConnectorAddress";
		String addr = jvm.getAgentProperties().getProperty(key);
		if(StringUtils.isBlank(addr)){
			jvm.startLocalManagementAgent();
			addr = jvm.getAgentProperties().getProperty(key);
        }
		JMXServiceURL url = new JMXServiceURL(addr);
		JMXConnector connector = JMXConnectorFactory.connect(url);
		return connector;
	}
	
	public MemoryMXBean getMemoryMXBean() throws IOException {
		MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
		MemoryMXBean memoBean = ManagementFactory.getPlatformMXBean(conn, MemoryMXBean.class);
		return memoBean;
	}
	
	public List<MemoryPoolMXBean> getMemoryPoolMXBeans() throws IOException{
		MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
		List<MemoryPoolMXBean> poolBeans = ManagementFactory.getPlatformMXBeans(conn, MemoryPoolMXBean.class);
		return poolBeans;
	}
	
	public OperatingSystemMXBean getOperatingSystemMXBean() throws IOException{
		MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(conn, OperatingSystemMXBean.class);
		return osBean;
	}
	
	public RuntimeMXBean getRuntimeMXBean() throws IOException{
		MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
		RuntimeMXBean bean = ManagementFactory.getPlatformMXBean(conn, RuntimeMXBean.class);
		return bean;
	}
	
	public ThreadMXBean getThreadMXBean() throws IOException{
		MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
		ThreadMXBean threadBean = ManagementFactory.getPlatformMXBean(conn, ThreadMXBean.class);
		return threadBean;
	}
	
	public List<GarbageCollectorMXBean> getGCList() throws IOException{
		MBeanServerConnection conn = jmxConnector.getMBeanServerConnection();
		List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getPlatformMXBeans(conn, GarbageCollectorMXBean.class);
		return gcBeans;
	}
	
	public synchronized void startWatcher(int maxSegSize, WatcherCallback callback) throws IOException{
		if(this.enable.get()){
			return;
		}
		
		if(SystemInfoService.conf==null 
				|| SystemInfoService.conf.getJvmWatcherThreadsPool()==null){
			throw new IOException("configuration instance's JvmWatcherThreadsPool is null");
		}
		
		if(maxSegSize<1){
			throw new IOException("maxSegSize must be more than 1");
		}
		getMaxSegSize().set(maxSegSize);
		
		ExecutorService pool = SystemInfoService.conf.getJvmWatcherThreadsPool();
		pool.execute(new Runnable() {
			
			@Override
			public void run() {
				while(isEnable()){
					MemoSegment segment = new MemoSegment();
					segment.setCreateTime(System.currentTimeMillis());
					try {
						MemoryMXBean memoBean = getMemoryMXBean();
						MemoryUsage heap = memoBean.getHeapMemoryUsage();
						MemoryUsage non  = memoBean.getNonHeapMemoryUsage();
						
						segment.getHeapPools().add(new MemoUsage("heap", heap.getInit(), heap.getUsed(), heap.getCommitted(), heap.getMax()));
						segment.getNonHeapPools().add(new MemoUsage("non-heap", non.getInit(), non.getUsed(), non.getCommitted(), non.getMax()));
						
						List<MemoryPoolMXBean> poolBeans = getMemoryPoolMXBeans();
						poolBeans.forEach(bean->{
							if(isHeap(bean.getName())){
								segment.getHeapPools().add(new MemoUsage(bean.getName(), 
										bean.getUsage().getInit(), bean.getUsage().getUsed(), bean.getUsage().getCommitted(), bean.getUsage().getMax()));
							} else {
								segment.getNonHeapPools().add(new MemoUsage(bean.getName(), 
										bean.getUsage().getInit(), bean.getUsage().getUsed(), bean.getUsage().getCommitted(), bean.getUsage().getMax()));
							}
						});
						
						// 保留最大元素个数
						if(getMemoSegments().size()>=getMaxSegSize().get()){
							getMemoSegments().poll();
						}
						
						// GC
						MemoSegment preNode = segment;
						MemoSegment current = segment;
						if(!getMemoSegments().isEmpty()){
							MemoSegment[] arr = new MemoSegment [getMemoSegments().size()];
							preNode = getMemoSegments().toArray(arr)[arr.length-1];
						}
						double percent = 0;
						if(!preNode.getHeapPools().isEmpty() && !current.getHeapPools().isEmpty()){
							long pre = preNode.getHeapPools().get(0).getUsed();
							long cur = current.getHeapPools().get(0).getUsed();
							percent = (double)(pre-cur)/pre;
						}
						percent = percent<0?0:percent;
						List<GarbageCollectorMXBean> gcList = getGCList();
						for(GarbageCollectorMXBean gc: gcList){

							GarbageCollector curGC = new GarbageCollector(gc.getCollectionCount(), gc.getCollectionTime());
							curGC.setName(gc.getName());
							segment.getGcs().put(gc.getName(), curGC);
							
							GarbageCollector preGC = preNode.getGcs().get(gc.getName());
							if(preGC==null){
								preGC = curGC;
							}
							// 是否发生GC
							if(curGC.getCollectionCnt()>preGC.getCollectionCnt()){
								curGC.setTaking(true);
							} else {
								curGC.setTaking(false);
								curGC.setPercent(0);
							}
							// 发生GC
							if(curGC.isTaking() || preGC.isTaking()){
								curGC.setPercent(percent);
							}
							
						}
						// 缓存segment
						getMemoSegments().add(segment);
						// 睡眠间隔时间
						Thread.sleep(getInterval().get());
					} catch (Throwable e) {
						JVMConnector.this.stopWatcher();
						callback.setException(e);
					} finally {
						callback.callbackWatchMemo(url, segment);
					}
				}
			}
			
		});
		setEnable(true);
	}
	
	public synchronized void stopWatcher(){
		this.close();
	}
	
	
	public void close(){
		this.enable.set(false);
		if(SystemInfoService.conf!=null && SystemInfoService.conf.getCache().containsKey(url)){
			SystemInfoService.conf.getCache().remove(url);
		}
		IOUtils.closeQuietly(jmxConnector);
	}
	
	public boolean isHeap(String name){
		if(StringUtils.containsIgnoreCase(name, "Eden")){
			return true;
		}
		if(StringUtils.containsIgnoreCase(name, "Survivor")){
			return true;
		}
		if(StringUtils.containsIgnoreCase(name, "Old Gen")
				|| StringUtils.containsIgnoreCase(name, "Tenured Gen")){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否是本地连接
	 * @return
	 */
	public static boolean isLocal(String url){
		if(!StringUtils.startsWith(url, "localhost/")){
			return false;
		}
		return true;
	}

	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}

	public long getUpdatetime() {
		return updatetime.get();
	}

	public long updatetime() {
		this.updatetime = updatetime==null?new AtomicLong(0):updatetime;
		this.updatetime.set(System.currentTimeMillis());
		return this.updatetime.get();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ConcurrentLinkedQueue<MemoSegment> getMemoSegments() {
		return memoSegments;
	}

	public void setMemoSegments(ConcurrentLinkedQueue<MemoSegment> memoSegments) {
		this.memoSegments = memoSegments;
	}

	public boolean isEnable() {
		return enable.get();
	}

	public void setEnable(boolean enable) {
		if(this.enable==null){
			this.enable = new AtomicBoolean(enable);
		}
		this.enable.set(enable);;
	}

	public AtomicLong getInterval() {
		return interval;
	}

	public void setInterval(AtomicLong interval) {
		this.interval = interval;
	}

	public AtomicInteger getMaxSegSize() {
		return maxSegSize;
	}

	public void setMaxSegSize(AtomicInteger maxSegSize) {
		this.maxSegSize = maxSegSize;
	}


}
