package org.shersfy.jwatcher.entity;

public class JVMProcess extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**进程ID**/
	private long pid;
	/**可执行程序路径**/
	private String exePath;
	/**是否已监控**/
	private boolean watch;
	
	public long getPid() {
		return pid;
	}
	public String getExePath() {
		return exePath;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public void setExePath(String exePath) {
		this.exePath = exePath;
	}
	public boolean isWatch() {
		return watch;
	}
	public void setWatch(boolean watch) {
		this.watch = watch;
	}
	
	

}
