package org.shersfy.jwatcher.service;

import java.io.File;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.shersfy.jwatcher.conf.Config;
import org.shersfy.jwatcher.connector.JMXLocalConnector;
import org.shersfy.jwatcher.connector.JVMConnector;
import org.shersfy.jwatcher.connector.WatcherCallback;
import org.shersfy.jwatcher.entity.CPUInfo;
import org.shersfy.jwatcher.entity.DiskInfo;
import org.shersfy.jwatcher.entity.GarbageCollector;
import org.shersfy.jwatcher.entity.GcChart;
import org.shersfy.jwatcher.entity.JVMInfo;
import org.shersfy.jwatcher.entity.MemoSegment;
import org.shersfy.jwatcher.entity.JVMProcess;
import org.shersfy.jwatcher.entity.JVMThreads;
import org.shersfy.jwatcher.entity.Memory;
import org.shersfy.jwatcher.entity.SystemInfo;
import org.shersfy.jwatcher.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

@Component
public class SystemInfoService extends BaseService{

	public static Config conf;
	
	@Value("${jmx.watcher.threads.pool.size}")
	private int jvmWatcherThreadsPoolSize;
	
	@Value("${jmx.watcher.interval}")
	private long interval;
	
	@Value("${jmx.watcher.segment.max}")
	private int maxSegSize;
	
	@PostConstruct
	private void init(){
		LOGGER.info("=========init starting===========");
		conf = new Config();
		conf.setSigar(new Sigar());
		conf.setJvmWatcherThreadsPool(Executors.newFixedThreadPool(jvmWatcherThreadsPoolSize));
		LOGGER.info("=========init finished===========");
	}

	public SystemInfo getSystemInfo(){

		SystemInfo info = new SystemInfo();
		Map<String, String> env = System.getenv();
		Properties props = System.getProperties();
		OperatingSystem osBean = OperatingSystem.getInstance();
		Sigar sigar = conf.getSigar();

		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LOGGER.error("", e);
		}

		// Address
		info.setName(env.getOrDefault("COMPUTERNAME", ""));
		info.setDomain(env.getOrDefault("USERDOMAIN", ""));

		if(addr!=null){
			info.setHost(addr.getHostName());
			info.setIp(addr.getHostAddress());
		}

		// OS
		String os = String.format("%s %s %s %s", 
				props.getProperty("os.name"),
				osBean.getArch(),
				osBean.getDataModel(),
				osBean.getVersion());
		
		info.setOs(os);

		// CPU
		CpuInfo[] cpus = null;
		try {
			cpus = sigar.getCpuInfoList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}

		if(cpus!=null&&cpus.length!=0){
			info.setCpu(String.format("%s %s", cpus[0].getVendor(), cpus[0].getModel()));
		}

		// RAM
		Mem memo = null;
		try {
			memo = sigar.getMem();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}
		long ram = memo==null?0:memo.getRam() * 1024 * 1024;
		info.setRam(FileUtil.getLengthWithUnit(ram));

		// disk
		FileSystem fslist[] = null;
		File.listRoots();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		try {
			fslist = sigar.getFileSystemList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}

		if(fslist!=null){
			info.getDisks().clear();
			for(FileSystem fs :fslist){
				File root = new File(fs.getDirName());
				FileSystemUsage usage = null;
				try {
					if(fs.getType()!=FileSystem.TYPE_CDROM){
						usage = sigar.getFileSystemUsage(fs.getDirName());
					}
				} catch (SigarException e) {
					LOGGER.error("", e);
				}
				DiskInfo disk = new DiskInfo();
				String name = fsv.getSystemDisplayName(root);
				disk.setName(StringUtils.isBlank(name)?root.getPath():name);
				disk.setType(fsv.getSystemTypeDescription(root));
				disk.setFileSystem(fs.getSysTypeName());
				disk.setTotal(usage==null?0:usage.getTotal()*1024);
				disk.setUsed(usage==null?0:usage.getUsed()*1024);
				disk.setUsedPercent(usage==null?"0%":String.format("%.0f%%", usage.getUsePercent()*100));
				
				info.getDisks().add(disk);
			}
		}
		
//		FileSystemView fs = FileSystemView.getFileSystemView();
//		File[] roots = File.listRoots();
//		for(File root :roots){
//			DiskInfo disk = new DiskInfo();
//			disk.setName(fs.getSystemDisplayName(root));
//			disk.setType(fs.getSystemTypeDescription(root));
//			disk.setFileSystem(fileSystem);
//			disk.setTotal(root.getTotalSpace());
//			disk.setUsed(root.getUsableSpace());
//
//			info.getDisks().add(disk);
//		}

		return info;
	}
	
	public JVMInfo getJvmInfo(){
		
		Properties props = System.getProperties();
		MemoryMXBean mx  = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = mx.getHeapMemoryUsage();
		MemoryUsage nonheap = mx.getNonHeapMemoryUsage();
		
		JVMInfo jvm = new JVMInfo();
		
		jvm.setProcessors(Runtime.getRuntime().availableProcessors());
		
		jvm.setJavaVersion(String.format("%s Build %s", 
				props.getProperty("java.version"),
				props.getProperty("java.vm.version")));
		jvm.setJavaVendor(props.getProperty("java.vendor"));
		jvm.setJavaVendorUrl(props.getProperty("java.vendor.url"));
		jvm.setJavaHome(props.getProperty("java.home"));
		jvm.setJavaHome(props.getProperty("java.home"));
		
		jvm.setInitMemory(heap.getInit()+nonheap.getInit());
		jvm.setMaxMemory(heap.getMax()+nonheap.getCommitted());
		jvm.setUsedMemory(heap.getUsed()+nonheap.getUsed());
		
		jvm.setTotalMemory(jvm.getMaxMemory());
		jvm.setFreeMemory(jvm.getTotalMemory()- jvm.getUsedMemory());
		if(jvm.getMaxMemory()<jvm.getInitMemory()){
			jvm.setMaxMemory(jvm.getInitMemory());
		}
		
		return jvm;
	}
	
	public List<JVMProcess> getLocalJvmProcesses(){
		
		Sigar sigar = conf.getSigar();
		List<JVMProcess> list = new ArrayList<>();
		list.clear();
		try {
			MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
			// 取得所有在活动的虚拟机集合
			Set<Integer> pids = local.activeVms();
			// 遍历PID和进程名
			for(Integer pid : pids){
				MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + pid));
				String mainClass = MonitoredVmUtil.mainClass(vm, true);
				JVMProcess p = new JVMProcess();
				p.setPid(pid);
				p.setName(mainClass);
				p.setExePath(sigar.getProcExe(pid).getName());
				JVMConnector conn = conf.getCache().get(JMXLocalConnector.getLocalUrl(pid));
				if(conn!=null){
					p.setWatch(conn.isEnable());
				}
				list.add(p);
			}

		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return list;
	}
	
	public JVMConnector getConnector(String url) throws IOException{
		JVMConnector connector = JVMConnector.getConnector(url);
		return connector;
	}
	
	public List<JVMConnector> getRemoteConnectors(){
		List<JVMConnector> list = new ArrayList<>();
		conf.getCache().forEach((url, connector)->{
			if(!JVMConnector.isLocal(url)){
				list.add(connector);
			}
		});
		return list;
	}
	
	public MemoSegment[] getData(String url) throws IOException{
		JVMConnector connector = conf.getCache().get(url);
		if(connector==null || !connector.isEnable()){
			throw new IOException("JVMConnector is not created or not started: "+url);
		}
		
		MemoSegment[] data = connector.getMemoSegments().toArray(new MemoSegment[connector.getMemoSegments().size()]);
		connector.updatetime();
		return data;
	}
	
	public GcChart getGcChart(String url) throws IOException{
		JVMConnector connector = conf.getCache().get(url);
		if(connector==null || !connector.isEnable()){
			throw new IOException("JVMConnector is not created or not started: "+url);
		}
		
		MemoSegment[] data = connector.getMemoSegments().toArray(new MemoSegment[connector.getMemoSegments().size()]);
		connector.updatetime();
		
		
		GcChart chart = new GcChart();
		chart.setName("GC Chart");
		if(data.length>0){
			Map<String, GarbageCollector> gcs = data[data.length-1].getGcs();
			gcs.keySet().forEach(key->{
				chart.getSubjects().add(key);
				chart.getYdata().add(new ArrayList<>());
			});
		}
		
		for(MemoSegment seg :data){
			chart.getXdata().add(DateFormatUtils.format(seg.getCreateTime(), "mm:ss"));
			for(int i=0; i<chart.getSubjects().size(); i++){
				GarbageCollector gc = seg.getGcs().get(chart.getSubjects().get(i));
				chart.getYdata().get(i).add(gc);
			}
		}
		
		return chart;
	}
	
	public String getServerOS(JVMConnector connector){
		String info = "";
		if(connector == null){
			return info;
		}
		try {
			OperatingSystemMXBean os = connector.getOperatingSystemMXBean();
			info = String.format("%s %s %s %s cores", 
					os.getName(),
					os.getVersion(),
					os.getArch(),
					os.getAvailableProcessors());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return info;
	}
	
	public JVMInfo getServerJVM(JVMConnector connector){
		JVMInfo info = new JVMInfo();
		if(connector == null){
			return info;
		}
		try {
			RuntimeMXBean bean = connector.getRuntimeMXBean();
			String javaVersion = String.format("%s JDK%s Build %s", 
					bean.getVmName(),
					bean.getSpecVersion(),
					bean.getVmVersion());
			info.setName(bean.getName());
			info.setJavaVersion(javaVersion);
			info.setJavaVendor(bean.getVmVendor());
			info.setJavaOpts(bean.getInputArguments());
			info.setJavaHome(bean.getSystemProperties().get("java.home"));
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		
		return info;
	}
	
	public List<String> getServerGCNames(JVMConnector connector){
		List<String> list = new ArrayList<>();
		if(connector == null){
			return list;
		}
		try {
			List<GarbageCollectorMXBean> beans = connector.getGCList();
			beans.forEach(bean->{
				list.add(String.format("%s: %s", 
						bean.getName(),
						StringUtils.join(bean.getMemoryPoolNames(), ", ")));
			});
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return list;
	}
	
	public JVMThreads getServerThreads(JVMConnector connector, boolean all){
		JVMThreads info = new JVMThreads();
		if(connector == null){
			return info;
		}
		try {
			ThreadMXBean bean = connector.getThreadMXBean();
			info.setName(bean.getObjectName().getCanonicalName());
			info.setDaemonThreadCount(bean.getDaemonThreadCount());
			info.setPeakThreadCount(bean.getPeakThreadCount());
			info.setTotalStartedThreadCount(bean.getTotalStartedThreadCount());
			info.setThreadCount(bean.getThreadCount());
			if(all){
				info.setAllThreads(Arrays.asList(bean.dumpAllThreads(true, true)));
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return info;
	}
	
	/**
	 * 启动监控jvm进程
	 * 
	 * @param connector jvm连接
	 */
	public void startWatcher(JVMConnector connector){
		if(connector.isEnable()){
			return;
		}
		maxSegSize = maxSegSize<1?1:maxSegSize;
		interval   = interval<1000?1000:interval;
		connector.getInterval().set(interval);
		if(connector!=null){
			WatcherCallback callback = new WatcherCallback() {
				
				@Override
				public void callbackWatchMemo(String url, MemoSegment segment) {
					if(this.getException()!=null){
						LOGGER.error("", this.getException());
						return;
					}
					
					LOGGER.debug("jvm process '{}', watch begin ...", url);
					segment.getHeapPools().forEach(heap->{
						LOGGER.debug("jvm process '{}', heap {}, init {}, max {}, committed {}, used {}", url,
								heap.getName(), 
								FileUtil.getLengthWithUnit(heap.getInit()),
								FileUtil.getLengthWithUnit(heap.getMax()),
								FileUtil.getLengthWithUnit(heap.getCommitted()),
								FileUtil.getLengthWithUnit(heap.getUsed()));
					});
					
					segment.getNonHeapPools().forEach(non->{
						LOGGER.debug("jvm process '{}', non-heap {}, init {}, max {}, committed {}, used {}", url,
								non.getName(), 
								FileUtil.getLengthWithUnit(non.getInit()),
								FileUtil.getLengthWithUnit(non.getMax()),
								FileUtil.getLengthWithUnit(non.getCommitted()),
								FileUtil.getLengthWithUnit(non.getUsed()));
					});
					LOGGER.debug("jvm process '{}', watch finish", url);
				}
			};
			try {
				connector.startWatcher(maxSegSize, callback);
				LOGGER.info("jvm process '{}' started", connector.getUrl());
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
	}
	
	/**
	 * 停止监控jvm进程
	 * 
	 * @param connector jvm连接
	 */
	public void stopWatcher(JVMConnector connector){
		if(connector!=null){
			connector.stopWatcher();
			LOGGER.info("jvm process '{}' stoped", connector.getUrl());
		}
	}
	
	public Memory getMemory(){
		Sigar sigar = conf.getSigar();
		Memory memo = new Memory();
		// RAM
		Mem mem = null;
		try {
			mem = sigar.getMem();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}
		long ram = mem==null?0:mem.getRam() * 1024 * 1024;
		memo.setTotal(ram);
		memo.setUsed(mem.getUsed());
		memo.setUsedPercent((double)mem.getUsed()/mem.getTotal());
		
		return memo;
	}
	
	public CPUInfo getCpuInfo(){
		Sigar sigar = conf.getSigar();
		CPUInfo cpu = new CPUInfo();
		
		CpuInfo[] cpuInfos = null;
		CpuPerc[] cpuPercs = null;
		try {
			cpuInfos = sigar.getCpuInfoList();
			cpuPercs = sigar.getCpuPercList();
		} catch (SigarException e) {
			LOGGER.error("", e);
		}
		if(cpuPercs!=null){
			double user = 0;
			double sys  = 0;
			double wait = 0;
			double nice = 0;
			double used = 0;
			double idle = 0;
			for(CpuPerc perc :cpuPercs){
				user += perc.getUser();
				sys  += perc.getSys();
				wait += perc.getWait();
				nice += perc.getNice();
				used += perc.getCombined();
				idle += perc.getIdle();
			}
			
			int size = cpuPercs.length;
			cpu.setUser(user/size);
			cpu.setSystem(sys/size);
			cpu.setWait(wait/size);
			cpu.setNice(nice/size);
			cpu.setUsed(used/size);
			cpu.setIdle(idle/size);
			
		}
		
		if(cpuInfos!=null&&cpuInfos.length!=0){
			cpu.setName(String.format("%s %s", cpuInfos[0].getVendor(), cpuInfos[0].getModel()));
		}
		
		return cpu;
	}
	
	/**
	 * 创建JMX RMI连接
	 * 
	 * @param jmxRmiUri 连接串
	 * @return 返回 model: code=200, 返回MBeanServerConnection 对象或null(本地时)
	 * @throws IOException 
	 */
	public JMXConnector getJmxConnector(String jmxRmiUri) throws IOException{

		if(StringUtils.isBlank(jmxRmiUri) || "localhost".equalsIgnoreCase(jmxRmiUri)){
			return conf.getLocalDefault();
		}

		JMXServiceURL serviceURL = new JMXServiceURL(jmxRmiUri);
		JMXConnector connector   = JMXConnectorFactory.connect(serviceURL);

		return connector;
	}
	
}
