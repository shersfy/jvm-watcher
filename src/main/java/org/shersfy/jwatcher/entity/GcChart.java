package org.shersfy.jwatcher.entity;

import java.util.ArrayList;
import java.util.List;

public class GcChart extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> subjects;
	private List<String> xdata;
	private List<List<GarbageCollector>> ydata;
	
	public GcChart() {
		super();
		subjects = new ArrayList<>();
		xdata = new ArrayList<>();
		ydata = new ArrayList<>();
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public List<String> getXdata() {
		return xdata;
	}
	public List<List<GarbageCollector>> getYdata() {
		return ydata;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public void setXdata(List<String> xdata) {
		this.xdata = xdata;
	}
	public void setYdata(List<List<GarbageCollector>> ydata) {
		this.ydata = ydata;
	}
	
	

}
