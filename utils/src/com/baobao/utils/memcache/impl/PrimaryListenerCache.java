package com.baobao.utils.memcache.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baobao.utils.memcache.ICacheListener;
import com.baobao.utils.memcache.IMemcachedCache;

public class PrimaryListenerCache<K, V>{
	
	private IMemcachedCache cache = null;
	
	public PrimaryListenerCache(String cacheName,ICacheListener<V> lisImpl, long loopTimes) {
		cache = MemcachedCacheManager.getInstance().getCache(cacheName);
		updateSet = new HashSet<K>();
		thread = new ListenerThread();
		thread.looptimes = loopTimes;
		thread.lis = lisImpl;
		thread.start();
	}
	
	public void putForUpdate(K key, V value){
		synchronized(updateSet){
			updateSet.add(key);
		}
	}
	
	public void putQuietForUpdate(K key, V value){
		cache.putQuiet(String.valueOf(key), value);
		synchronized(updateSet){
			updateSet.add(key);
		}
	}
	
	private Set<K> updateSet = null;
	private ListenerThread thread = null;

	private class ListenerThread extends java.lang.Thread {
		long looptimes = 60000;
		ICacheListener<V> lis = null;
		List<V> vlist = new ArrayList<V>();
		public void run() {
			while(true){
				try {
					sleep(looptimes);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (updateSet != null && !updateSet.isEmpty()) {
					Set<K> temp = null;
					synchronized(updateSet){
						temp = updateSet;
						updateSet = new HashSet<K>();
					}
					for(K k:temp){
						V v = (V)cache.get(String.valueOf(k));
						if(null != v)
							vlist.add(v);
					}
					lis.runUpdate(vlist);
					vlist.clear();
				}
			}
		}
	}

	public IMemcachedCache getCache() {
		return cache;
	}
}
