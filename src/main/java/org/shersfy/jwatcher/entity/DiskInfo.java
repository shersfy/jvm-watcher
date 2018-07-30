package org.shersfy.jwatcher.entity;

import org.shersfy.jwatcher.utils.FileUtil;

public class DiskInfo extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;
	private String fileSystem;
	private long total;
	private long used;
	private String usedPercent;
	
	public String getTotalStr() {
		return FileUtil.getLengthWithUnit(total);
	}
	public String getUsedStr() {
		return FileUtil.getLengthWithUnit(used);
	}
	
	public String getType() {
		return type;
	}
	public String getFileSystem() {
		return fileSystem;
	}
	public long getTotal() {
		return total;
	}
	public long getUsed() {
		return used;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setFileSystem(String fileSystem) {
		this.fileSystem = fileSystem;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public void setUsed(long used) {
		this.used = used;
	}
	public String getUsedPercent() {
		return usedPercent;
	}
	public void setUsedPercent(String usedPercent) {
		this.usedPercent = usedPercent;
	}

}
