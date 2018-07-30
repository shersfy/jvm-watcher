package org.shersfy.jwatcher.entity;

import java.util.ArrayList;
import java.util.List;

public class SystemInfo extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String os;
	private String host;
	private String ip;
	private String domain;
	private String cpu;
	private String ram;
	private List<DiskInfo> disks;
	
	public SystemInfo(){
		disks = new ArrayList<>();
	}
	
	public int getDisksSize(){
		return disks.size();
	}
	
	public String getOs() {
		return os;
	}
	public String getHost() {
		return host;
	}
	public String getIp() {
		return ip;
	}
	public String getDomain() {
		return domain;
	}
	public String getCpu() {
		return cpu;
	}
	public String getRam() {
		return ram;
	}
	public List<DiskInfo> getDisks() {
		return disks;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public void setRam(String ram) {
		this.ram = ram;
	}
	public void setDisks(List<DiskInfo> disks) {
		this.disks = disks;
	}
	
	

}
