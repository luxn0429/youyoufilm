package com.baobao.utils.cache;

import java.io.Serializable;

import com.baobao.utils.memcache.IMemcachedCache;
import com.baobao.utils.memcache.impl.MemcachedCacheManager;

public class PrimaryTimerCache<K, V> extends PrimaryCache implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7837489288651L;
	private IMemcachedCache supcache=null;
	public PrimaryTimerCache(int capacity, String cacheName){
		super(0, cacheName);
		supcache = MemcachedCacheManager.getInstance().getCache(cacheName);
	}
	public void putTimeQuiet(String key, V value, long timeout){
		
		TimerObject tobj=new TimerObject();
		tobj.obj=value;
		tobj.timerstmp=System.currentTimeMillis()+timeout;
		supcache.putQuiet(key, tobj, (int)(timeout/1000));
		
	}
	public V getTimeQuiet(String key) throws CacheException{
		TimerObject tobj=(TimerObject)supcache.get(key);
		if(tobj==null){
			return null;
		}else{
			long now=System.currentTimeMillis();
			if(tobj.timerstmp>=now){
				return (V) tobj.obj;
			}else{
				return null;
			}
		}
		
	}

}

