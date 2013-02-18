package com.baobao.utils.memcache.impl;

import java.util.Observable;

import com.baobao.utils.cache.Event;
import com.danga.MemCached.MemCachedClient;

public class StatisticCache extends ConditionCache {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1222713815628283604L;

	public StatisticCache(String name, MemCachedClient client,
			Integer statisticsInterval) {
		super(name, client, statisticsInterval);
	}

	@Override
	public void update(Observable o, Object arg) {
		Event event = (Event)arg;
		Long e = (Long)event.getParam();
		removeQuiet(String.valueOf(e));
	}

}
