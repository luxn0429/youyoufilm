package com.baobao.utils.cache;

import java.util.Observable;

public class SearchCache<K, V> extends ConditionCache<K,V>{
	public SearchCache(){
		super();
	}
	
	public SearchCache(String cacheName){
		super(cacheName);
	}
	
	public SearchCache(int capacity,String cacheName){
		super(capacity,cacheName);
	}
	public SearchCache(int capacity){
		super(capacity);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Event event = (Event)arg;
		K e = (K)event.getParam();
		removeQuiet(e);
	}
}
