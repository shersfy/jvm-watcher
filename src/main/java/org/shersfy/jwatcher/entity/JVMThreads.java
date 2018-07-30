package org.shersfy.jwatcher.entity;

import java.lang.management.ThreadInfo;
import java.util.List;

public class JVMThreads extends BaseEntity {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int threadCount;
	private int daemonThreadCount;
	private int peakThreadCount;
	private long totalStartedThreadCount;
	private List<ThreadInfo> allThreads;
	
	public JVMThreads(){}
	
	public int getThreadCount() {
		return threadCount;
	}
	public int getDaemonThreadCount() {
		return daemonThreadCount;
	}
	public int getPeakThreadCount() {
		return peakThreadCount;
	}
	public long getTotalStartedThreadCount() {
		return totalStartedThreadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public void setDaemonThreadCount(int daemonThreadCount) {
		this.daemonThreadCount = daemonThreadCount;
	}
	public void setPeakThreadCount(int peakThreadCount) {
		this.peakThreadCount = peakThreadCount;
	}
	public void setTotalStartedThreadCount(long totalStartedThreadCount) {
		this.totalStartedThreadCount = totalStartedThreadCount;
	}
	public List<ThreadInfo> getAllThreads() {
		return allThreads;
	}
	public void setAllThreads(List<ThreadInfo> allThreads) {
		this.allThreads = allThreads;
	}


}
