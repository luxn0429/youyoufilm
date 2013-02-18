package com.baobao.utils;

public class LockObject {

	private int waitCount;
	
	public LockObject() {
		waitCount = 0;
	}

	public int getWaitCount() {
		return waitCount;
	}
	
	public void increase(){
		waitCount += 1;
	}

	public void decrease(){
		waitCount -= 1;
	}
	
}
