package com.baobao.utils.cache;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LRUMap<K, V> extends LinkedHashMap<K, V> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7028158315828299085L;

	private static final int defaultSize = 100000;
	
	private int capacity;
	
	public LRUMap() {
		super(defaultSize);
		this.capacity = defaultSize;
	}
	
	public LRUMap(int capacity) {
		super(capacity);
		this.capacity = capacity;
	}
	public LRUMap(int initialCapacity, int maximumCacheSize, float loadFactor, boolean accessOrder){
		super(initialCapacity,loadFactor,accessOrder);
		this.capacity = maximumCacheSize;
	}
	public boolean removeEldestEntry(Entry<K, V> eldest) {
		return (size() > this.capacity);
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
}
