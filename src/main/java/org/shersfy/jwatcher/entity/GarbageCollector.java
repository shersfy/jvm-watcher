package org.shersfy.jwatcher.entity;

public class GarbageCollector extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**GC总次数**/
	private long collectionCnt;
	/**最近一次GC所用时间**/
	private long collectionTime;
	/**相对于上次是否发生GC**/
	private boolean taking;
	/**GC回收率**/
	private double percent;
	
	public GarbageCollector() {
		super();
	}
	
	public GarbageCollector(String name) {
		super();
		setName(name);
	}

	public GarbageCollector(long collectionCnt, long collectionTime) {
		super();
		this.collectionCnt = collectionCnt;
		this.collectionTime = collectionTime;
	}
	
	public long getCollectionCnt() {
		return collectionCnt;
	}
	public long getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionCnt(long collectionCnt) {
		this.collectionCnt = collectionCnt;
	}
	public void setCollectionTime(long collectionTime) {
		this.collectionTime = collectionTime;
	}
	public boolean isTaking() {
		return taking;
	}
	public double getPercent() {
		return percent;
	}
	public void setTaking(boolean taking) {
		this.taking = taking;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}

}
