package org.shersfy.jwatcher.entity;

import java.io.Serializable;
import java.lang.management.MemoryUsage;

import com.alibaba.fastjson.JSON;


public class MemoUsage extends MemoryUsage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public MemoUsage(String name){
		super(0, 0, 0, 0);
		this.name = name;
	}
	
	public MemoUsage(long init, long used, long committed, long max) {
		super(init, used, committed, max);
	}
	
	public MemoUsage(String name, long init, long used, long committed, long max) {
		super(init, used, committed, max);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
