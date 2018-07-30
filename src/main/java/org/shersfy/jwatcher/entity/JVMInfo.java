package org.shersfy.jwatcher.entity;

import java.util.ArrayList;
import java.util.List;

import org.shersfy.jwatcher.utils.FileUtil;

public class JVMInfo extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String javaVersion;
	private String javaVendor;
	private String javaVendorUrl;
	private String javaHome;
	private List<String> javaOpts;
	
	// heap
	private long initMemory;
	private long maxMemory;
	private long totalMemory;
	private long usedMemory;
	private long freeMemory;
	
	private int processors;
	
	public JVMInfo(){
		javaOpts = new ArrayList<>();
	}
	
	public String getInit() {
		return FileUtil.getLengthWithUnit(initMemory);
	}
	public String getMax() {
		return FileUtil.getLengthWithUnit(maxMemory);
	}
	public String getTotal() {
		return FileUtil.getLengthWithUnit(totalMemory);
	}
	public String getUsed() {
		return FileUtil.getLengthWithUnit(usedMemory);
	}
	public String getFree() {
		return FileUtil.getLengthWithUnit(freeMemory);
	}
	public String getJavaVersion() {
		return javaVersion;
	}
	public String getJavaVendor() {
		return javaVendor;
	}
	public String getJavaVendorUrl() {
		return javaVendorUrl;
	}
	public String getJavaHome() {
		return javaHome;
	}
	public long getInitMemory() {
		return initMemory;
	}
	public long getMaxMemory() {
		return maxMemory;
	}
	public long getTotalMemory() {
		return totalMemory;
	}
	public long getUsedMemory() {
		return usedMemory;
	}
	public int getProcessors() {
		return processors;
	}
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	public void setJavaVendor(String javaVendor) {
		this.javaVendor = javaVendor;
	}
	public void setJavaVendorUrl(String javaVendorUrl) {
		this.javaVendorUrl = javaVendorUrl;
	}
	public void setJavaHome(String javaHome) {
		this.javaHome = javaHome;
	}
	public void setInitMemory(long initMemory) {
		this.initMemory = initMemory;
	}
	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}
	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}
	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}
	public void setProcessors(int processors) {
		this.processors = processors;
	}
	public long getFreeMemory() {
		return freeMemory;
	}
	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public List<String> getJavaOpts() {
		return javaOpts;
	}

	public void setJavaOpts(List<String> javaOpts) {
		this.javaOpts = javaOpts;
	}
	
	


}
