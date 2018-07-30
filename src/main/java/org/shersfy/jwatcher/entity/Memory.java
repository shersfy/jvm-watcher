package org.shersfy.jwatcher.entity;

import org.shersfy.jwatcher.utils.FileUtil;

public class Memory extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long total;
	private long used;
	private double usedPercent;
	
	public String getTotalStr() {
		return FileUtil.getLengthWithUnit(total);
	}
	public String getUsedStr() {
		return FileUtil.getLengthWithUnit(used);
	}
	
	public long getTotal() {
		return total;
	}
	public long getUsed() {
		return used;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public void setUsed(long used) {
		this.used = used;
	}
	public double getUsedPercent() {
		return usedPercent;
	}
	public void setUsedPercent(double usedPercent) {
		this.usedPercent = usedPercent;
	}

}
