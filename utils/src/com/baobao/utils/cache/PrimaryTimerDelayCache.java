package com.baobao.utils.cache;

import java.io.Serializable;

import com.baobao.utils.memcache.IMemcachedCache;
import com.baobao.utils.memcache.impl.MemcachedCacheManager;

public class PrimaryTimerDelayCache<K, V> extends PrimaryCache implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1235534897561L;
	private IMemcachedCache supcache=null;
	private long MAX_TIMEOUT=1000*60*15;
	public PrimaryTimerDelayCache(int capacity, String cacheName,long maxTime){
		super(0, cacheName);
		supcache = MemcachedCacheManager.getInstance().getCache(cacheName);
		this.MAX_TIMEOUT=maxTime;
	}
	public void putTimeQuiet(String key, V value){
		TimerObject tobj=new TimerObject();
		tobj.obj=value;
		tobj.timerstmp=System.currentTimeMillis()+MAX_TIMEOUT;
		//super.putQuiet(key, tobj);
		supcache.putQuiet(key, tobj, (int)(MAX_TIMEOUT/1000));
		
	}
	public V getTimeQuiet(String key) throws CacheException{
		TimerObject tobj=(TimerObject)supcache.get(key);
		if(tobj==null){
			return null;
		}else{
			long now=System.currentTimeMillis();
			if(tobj.timerstmp>=now){
				tobj.timerstmp=now+MAX_TIMEOUT;
				supcache.putQuiet(key, tobj, (int)(MAX_TIMEOUT/1000));
				return (V) tobj.obj;
			}else{
				return null;
			}
		}
		
	}

	
}

