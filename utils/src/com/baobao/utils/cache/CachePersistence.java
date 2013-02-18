package com.baobao.utils.cache;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 将缓存中的数据持久化存储到数据库中去
 * @author 江铁扣
 * @since 2010-05-11
 *
 */
public class CachePersistence {
	
	private static CachePersistence instance = null;
	
	private static Set<PersistenceInterface> timerCaches = new CopyOnWriteArraySet<PersistenceInterface>();
	
	private CachePersistence() {
		//init();
	}
	
	/**
	 * 单例模式的CachePersistence
	 * @return
	 */
	public synchronized static CachePersistence getInstance() {
		if(null == instance)
			instance = new CachePersistence();
		return instance;
	}
	
	/**
	 * 注册延迟更新的缓存
	 * @param timerCache
	 * @return
	 */
	public synchronized boolean register(PersistenceInterface timerCache) {
		if(timerCache == null)
			return false;
		timerCaches.add(timerCache);
		return true;
	}
	
	/**
	 * 解除注册延迟更新的缓存
	 * @param timerCache
	 * @return
	 */
	public synchronized boolean unregister(PersistenceInterface timerCache) {
		if(timerCache == null)
			return false;
		timerCaches.remove(timerCache);
		return true;
	}
	
	/**
	 * 去掉所有延迟更新的缓存
	 * @param timerCache
	 * @return
	 */
	public synchronized void unregisterAll() {
		timerCaches.clear();
	}
	
	/**
	 * 将所有已注册的延迟更新缓存中的数据持久化到数据库
	 */
	public synchronized void doPersistence() {
		Iterator<PersistenceInterface> it = timerCaches.iterator();
		while(it.hasNext()) {
			PersistenceInterface timerCache = it.next();
			if(timerCache != null)
				timerCache.storeData();
		}
	}

}
