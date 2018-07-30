package org.shersfy.jwatcher.connector;

import org.shersfy.jwatcher.entity.MemoSegment;

public abstract class WatcherCallback {

	private Throwable exception;
	public abstract void callbackWatchMemo(String url, MemoSegment segment);

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
}
