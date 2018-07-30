package org.shersfy.jwatcher.conf;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import javax.management.remote.JMXConnector;

import org.hyperic.sigar.Sigar;
import org.shersfy.jwatcher.connector.JMXLocalConnector;
import org.shersfy.jwatcher.connector.JVMConnector;
import org.shersfy.jwatcher.entity.BaseEntity;

public class Config extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Sigar sigar;
	private JMXConnector localDefault;
	private Map<String, JVMConnector> cache;
	private ExecutorService jvmWatcherThreadsPool;
	
	public Config(){
		super();
		localDefault = new JMXLocalConnector();
		cache = new ConcurrentHashMap<>();
	}

	public Sigar getSigar() {
		return sigar;
	}


	public JMXConnector getLocalDefault() {
		return localDefault;
	}

	public void setSigar(Sigar sigar) {
		this.sigar = sigar;
	}

	public void setLocalDefault(JMXConnector localDefault) {
		this.localDefault = localDefault;
	}

	public Map<String, JVMConnector> getCache() {
		return cache;
	}

	public void setCache(Map<String, JVMConnector> cache) {
		this.cache = cache;
	}

	public ExecutorService getJvmWatcherThreadsPool() {
		return jvmWatcherThreadsPool;
	}

	public void setJvmWatcherThreadsPool(ExecutorService jvmWatcherThreadsPool) {
		this.jvmWatcherThreadsPool = jvmWatcherThreadsPool;
	}

}
