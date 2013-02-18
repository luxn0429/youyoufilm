package com.baobao.utils.memcache.impl;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baobao.utils.cache.Event;
import com.baobao.utils.memcache.ICache;
import com.baobao.utils.memcache.IMemcachedCache;
import com.danga.MemCached.MemCachedClient;

public abstract class IndexCache extends Observable implements IMemcachedCache, Observer,Serializable {
	private static final Log Logger = LogFactory.getLog(IndexCache.class);
	////客户端
	private MemCachedClient client;
	private ICache<String,Object> localCache;
	private StatisticsTask task;
	private long statisticsInterval = 5 * 60;//单位秒
	
	private String name = null; ///缓存名
	static final String CACHE_STATUS_RESPONSE = "cacheStatusResponse";
	
	private MemcachedCacheManager cacheManager = MemcachedCacheManager.getInstance();
	
	/**
	 * 观察者列表,初始化时候使用
	 */
	protected Set<String> observerNames = new HashSet<String>();
	
	/**
	 * 被观察者列表
	 */
	protected Set<String> observableNames = new HashSet<String>();
	
	public IndexCache(String name,MemCachedClient client,Integer statisticsInterval){
		this.client = client;
		this.name = name;
		
		localCache = new LocalCacheImpl();
		
		if (statisticsInterval > 0){
			this.statisticsInterval = statisticsInterval;
			task = new StatisticsTask();
			task.setDaemon(true);
			task.start();
		}
	}
	
	private String getKey(String key){
		return name+key;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean add(String key, Object value, Event event) {
		if(client.add(getKey(key),value)){
			setChanged();
			notifyObservers(event);
			return true;
		}
		return false;
	}

	@Override
	public boolean add(String key, Object value, Date expiry, Event event) {
		if(client.add(getKey(key),value,expiry)){
			setChanged();
			notifyObservers(event);
			return true;
		}
		return false;
	}

	@Override
	public long addOrDecr(String key, long decr) {
		return client.addOrDecr(getKey(key),decr);
	}

	@Override
	public long addOrIncr(String key, long inc) {
		return client.addOrIncr(getKey(key), inc);
	}

	@Override
	public boolean addQuiet(String key, Object value) {
		return client.add(getKey(key),value);
	}

	@Override
	public boolean addQuiet(String key, Object value, Date expiry) {
		return client.add(getKey(key),value,expiry);
	}

	@Override
	public long decr(String key, long decr) {
		return decr(getKey(key),decr);
	}

	@Override
	public Object get(String key, int localTTL) {
		Object result = null;
		result = localCache.get(key);
		
		if (result == null){
			result = this.get(key);
			if (result != null){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.SECOND, localTTL);
				localCache.putQuiet(key, result, localTTL);
			}
		}
		return result;
	}

	@Override
	public long getCounter(String key) {
		try{
			return client.getCounter(getKey(key));
		}catch(MemcachedException ex){
			ex.printStackTrace();
		}
		return -1;
	}

	@Override
	public Map<String, Object> getMulti(String[] keys) {
		if (keys == null || keys.length <= 0)
			return null;
		String[] newKey = keys.clone();
		for(int i=0;i<keys.length;i++)
			newKey[i] = getKey(newKey[i]);
			
		return client.getMulti(newKey);
	}

	@Override
	public Object[] getMultiArray(String[] keys) {
		if (keys == null || keys.length <= 0)
			return null;
		String[] newKey = keys.clone();
		for(int i=0;i<keys.length;i++)
			newKey[i] = getKey(newKey[i]);
		return client.getMultiArray(newKey);
	}

	@Override
	public long incr(String key, long inc) {
		return client.incr(getKey(key),inc);
	}

	@Override
	public Set<String> keySet(boolean fast) {
		Set<String> keys = new HashSet<String>();
		Map<String,Integer> dumps = new HashMap<String,Integer>();
			 
		Map<String,Map<String,String>> slabs = client.statsItems();
		
		if (slabs != null && slabs.keySet() != null){
			Iterator<String> itemsItr = slabs.keySet().iterator();
			
			while(itemsItr.hasNext()){
				String server = itemsItr.next().toString();
				Map<String,String> itemNames = slabs.get(server);
				Iterator<String> itemNameItr = itemNames.keySet().iterator();
				
				while(itemNameItr.hasNext()){
					String itemName = itemNameItr.next().toString();
			        String[] itemAtt = itemName.split(":");
			        
			        if (itemAtt[2].startsWith("number")) 
			        	dumps.put(itemAtt[1], Integer.parseInt(itemAtt[1]));
				}
			}
			
			if (!dumps.values().isEmpty()){
				Iterator<Integer> dumpIter = dumps.values().iterator();
				
				while(dumpIter.hasNext()){
					int dump = dumpIter.next();
					Map<String,Map<String,String>> cacheDump = client.statsCacheDump(dump,0);
					Iterator<Map<String,String>> entryIter = cacheDump.values().iterator();
					
					while (entryIter.hasNext()){
		            	Map<String,String> items = entryIter.next();
		            	Iterator<String> ks = items.keySet().iterator();
		            	
		            	while(ks.hasNext()){
		            		String k = (String)ks.next();
		            		try{
		            			k = URLDecoder.decode(k,"UTF-8");
		            		}catch(Exception ex){}

		            		if (k != null && !k.trim().equals("")){
		            			if (fast)
		            				keys.add(k);
		            			else
		            				if (containsKey(k))
		            					keys.add(k);
		            		}
		            	}
		            }
				}
			}
		}
		return keys;
	}
	@Override
	public List<Object> getAllValue() {
		Set<String> keys = keySet();
		if(null == keys)
			return null;
		List<Object> ls = new ArrayList<Object>();
		for(String key : keys){
			Object temp = client.get(key);
			if(null != temp)
				ls.add(temp);
		}
		return ls;
	}
	@Override
	public boolean replace(String key, Object value, Event event) {
		if(client.replace(getKey(key),value)){
			setChanged();
			notifyObservers(event);
			return true;
		}
		return false;
	}

	@Override
	public boolean replace(String key, Object value, Date expiry, Event event) {
		if(client.replace(key, value, expiry)){
			setChanged();
			notifyObservers(event);
			return true;
		}
		return false;
	}

	@Override
	public boolean replaceQuiet(String key, Object value) {
		return client.replace(key, value);
	}

	@Override
	public boolean replaceQuiet(String key, Object value, Date expiry) {
		return client.replace(key, value, expiry);
	}

	@Override
	public void setStatisticsInterval(long checkInterval) {
		this.statisticsInterval = checkInterval;
	}

	@Override
	public MemcachedResponse statCacheResponse() {
		if (localCache.get(CACHE_STATUS_RESPONSE)== null){
			MemcachedResponse response = new MemcachedResponse();
			response.setCacheName(this.name);
			localCache.putQuiet(CACHE_STATUS_RESPONSE, response);
		}
		return (MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE);
	}

	@Override
	public MemcacheStats[] stats() {
		MemcacheStats[] result = null;
		Map<String,Map<String,String>> statMap = client.stats();
		
		if (statMap != null && !statMap.isEmpty()){
			result = new MemcacheStats[statMap.size()];
			Iterator<String> iter = statMap.keySet().iterator();
			int i = 0;
			
			while(iter.hasNext()){
				result[i] = new MemcacheStats();
				result[i].setServerHost(iter.next());
				result[i].setStatInfo(statMap.get(result[i].getServerHost()).toString());
				i += 1;
			}
		}
		
		return result;
	}

	@Override
	public Map<String, Map<String, String>> statsItems() {
		return client.statsItems();
	}

	@Override
	public MemcacheStatsSlab[] statsSlabs() {
		MemcacheStatsSlab[] result = null;
		Map<String,Map<String,String>> statMap = client.statsSlabs();
		
		if (statMap != null && !statMap.isEmpty()){
			result = new MemcacheStatsSlab[statMap.size()];
			
			Iterator<String> iter = statMap.keySet().iterator();
			int i = 0;
			while(iter.hasNext()){
				result[i] = new MemcacheStatsSlab();
				result[i].setServerHost(iter.next());
				
				Map<String,String> node = statMap.get(result[i].getServerHost());
				Iterator<String> nodeIter = node.keySet().iterator();
				
				while(nodeIter.hasNext()){
					String key = nodeIter.next();
					result[i].addSlab(key,node.get(key));
				}
				i += 1;
			}
		}	
		return result;
	}

	@Override
	public void storeCounter(String key, long count) {
		boolean result = client.storeCounter(getKey(key),count);
		if (!result)
			throw new java.lang.RuntimeException
				(new StringBuilder().append("storeCounter key :").append(key).append(" error!").toString());
		
	}

	@Override
	public boolean containsKey(String key) {
		try	{
			return client.keyExists(getKey(key));
		}catch(MemcachedException ex){
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public void destroy() {
		try{
			if (localCache != null){
				localCache.destroy();
			}
			
			if (task != null){
				task.stopTask();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	@Override
	public Object get(String key) {
		return client.get(getKey(key));	
	}

	@Override
	public Set<String> keySet() {
		return keySet(false);
	}

	@Override
	public Object put(String key, Object value, Event event) {
		String newKey = getKey(key);
		boolean result = client.set(newKey, value);
		
		//移除本地缓存的内容
		if (result){
			localCache.removeQuiet(newKey);
			this.setChanged();
			this.notifyObservers(event);
		}else
			throw new java.lang.RuntimeException
				(new StringBuilder().append("put key :").append(key).append(" error!").toString());
		return value;
	}
	
	@Override
	public Object put(String key, Object value, int TTL, Event event) {
		String newKey = getKey(key);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, TTL);
		
		boolean result = client.set(newKey,value,calendar.getTime());
		
		//移除本地缓存的内容
		if (result){
			localCache.removeQuiet(newKey);
			this.setChanged();
			this.notifyObservers(event);
		}
		return value;
	}

	@Override
	public Object putQuiet(String key, Object value) {
		String newKey = getKey(key);
		boolean result = client.set(newKey, value);
		
		//移除本地缓存的内容
		if (result){
			localCache.removeQuiet(newKey);
		}else
			throw new java.lang.RuntimeException
				(new StringBuilder().append("put key :").append(key).append(" error!").toString());
		return value;
	}

	@Override
	public Object putQuiet(String key, Object value, int TTL) {
		String newKey = getKey(key);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, TTL);
		
		boolean result = client.set(newKey,value,calendar.getTime());
		
		//移除本地缓存的内容
		if (result){
			localCache.removeQuiet(newKey);
		}
		return value;
	}

	@Override
	public boolean remove(String key, Event event) {
		String newKey = this.getKey(key);
		if(client.delete(newKey)){
			this.setChanged();
			this.notifyObservers(event);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Event event) {
		if(client.flushAll()){
			this.setChanged();
			this.notifyObservers(event);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAllQuiet() {
		return client.flushAll();
	}

	@Override
	public boolean removeQuiet(String key) {
		return client.delete(key);
	}

	@Override
	public int size() {
		throw new java.lang.UnsupportedOperationException("Memcached not support size method!");
	}

	@Override
	public Collection<Object> values() {
		Set<Object> values = new HashSet<Object>();
		Map<String,Integer> dumps = new HashMap<String,Integer>();
			 
		Map<String,Map<String,String>> slabs = client.statsItems();
		
		if (slabs != null ){
			for(Map.Entry<String,Map<String,String>> entry:slabs.entrySet()){
				Map<String,String> temp = entry.getValue();
				if(temp != null){
					for(Map.Entry<String,String> items : temp.entrySet()){
						String itemName = items.getKey();
						String[] itemAtt = itemName.split(":");
						if (itemAtt[2].startsWith("number")) 
				        	dumps.put(itemAtt[1], Integer.parseInt(itemAtt[1]));
					}
				}
			}
			
			for(Map.Entry<String,Integer> entry:dumps.entrySet()){
				int dump = entry.getValue();
				Map<String,Map<String,String>> cacheDump = client.statsCacheDump(dump,50000);
				for(Map.Entry<String,Map<String,String>> temp : cacheDump.entrySet()){
					Map<String,String> items = temp.getValue();
					for(Map.Entry<String,String> item:items.entrySet()){
						String k = item.getKey();
						try	{
	            			k = URLDecoder.decode(k,"UTF-8");
	            		}catch(Exception ex){}

	            		if (k != null && !k.trim().equals("")){
	            			Object value = get(k);
	            			if (value != null)
	            				values.add(value);
	            		}
					}
				}
			}
		}
		
		return values;		
	}
	/**
	 * 注册观察者
	 */
	@Override
	public void addObserver(Observer o) {
		super.addObserver(o);
		IndexCache temp = (IndexCache)o;
		////将观察者名字记录
		observerNames.add(temp.getName());
		//将自己注册为被观察者
		temp.addObservable(this.name);
	}
	///添加被观察者名字
	public void addObservable(String name){
		observableNames.add(name);
	}
	//删除被观察者名字
	public void deleteObservable(String name){
		observableNames.remove(name);
	}
	
	/**
	 * 删除观察者
	 */
	@Override
	public void deleteObserver(Observer o) {
		super.deleteObserver(o);
		IndexCache temp = (IndexCache)o;
		observerNames.remove(temp.getName());
		temp.deleteObservable(this.getName());
	}
	
	/**
	 * 删除所有观察者
	 */
	@Override
	public void deleteObservers() {
		super.deleteObservers();
		for(String observerName:observerNames) {
			deleteObserverByName(observerName);
		}
		observerNames.clear();
	}
	
	/**
	 * 将指定名字的Observer添加到本Cache的Observer列表中
	 * @param observerName
	 */
	
	public void addObserverByName(String observerName) {
		if(observerName == null || observerName.length() == 0)
			return;
		Observer temp = (Observer)cacheManager.getCache(name);
		if(null != temp) {
			addObserver(temp);
		}
	}
	
	/**
	 * 将指定名字的一组Observer添加到本Cache的Observer列表中
	 * @param observerNames
	 */
	public void addObserversByName(String[] observerNames) {
		if(observerNames == null)
			return;
		Observer observer = null;
		for(String observerName:observerNames) {
			observer = (Observer)cacheManager.getCache(observerName);
			if(null != observer)
				addObserver(observer);
		}
	}
	
	/**
	 * 将指定名字的Observer从本Cache的Observer列表中删除
	 * @param observerName
	 */
	public void deleteObserverByName(String observerName) {
		if(observerName == null || observerName.length() == 0)
			return;
		Observer observer = (Observer)cacheManager.getCache(observerName);
		if(null != observer) {
			deleteObserver(observer);
		}
	}
	
	/**
	 * 将指定的Observable的名字添加到本Cache所观察的Observable名字列表中，同时将自己加到该Observable的观察者列表中
	 */
	public void addObservableByName(String observableName) {
		if(observableName == null || observableName.length() == 0)
			return;
		Observable temp = (Observable)cacheManager.getCache(observableName);
		////将自己注册为对象的观察者
		if(null != temp) {
			temp.addObserver(this);
		}
	}
	
	/**
	 * 将指定的一组Observable的名字添加到本Cache所观察的Observable名字列表中，同时将自己加到所有Observable的观察者列表中
	 * @param observableNames
	 */
	public void addObservablesByName(String[] observableNames) {
		if(observableNames == null)
			return;
		for(String observableName:observableNames) {
			addObservableByName(observableName);
		}
	}
	
	/**
	 * 从本Cache所观察的Observable名字列表中删除指定Observable的名字，同时将自己从该Observable的观察者列表中删除
	 * @param observerName
	 */
	public void deleteObservableByName(String observableName) {
		if(observableName == null || observableName.length() == 0)
			return;
		//从被观察者中删除自己
		Observable temp = (Observable)cacheManager.getCache(observableName);
		////在被观察者的操作中会将被观察者从观察者中删除
		temp.deleteObserver(this);
	}
	
	/**
	 * 清空本Cache所观察的Observable名字列表，同时将自己从所有Observable的观察者列表中删除
	 */
	public void deleteObservablesByName() {
		for(String observableName:observableNames) {
			deleteObservableByName(observableName);
		}
	}
	@Override
	public abstract void update(Observable o, Object arg) ;
	/**
	 * 统计响应时间等信息的后台线程
	 * @author wenchu.cenwc
	 *
	 */
	class StatisticsTask extends java.lang.Thread{
		private boolean flag = true;
		
		@Override
		public void run(){
			while(flag){
				long consume = 0;
				
				try{
					Thread.sleep(statisticsInterval * 1000);
					consume = checkResponse();		
				}catch(InterruptedException e){
					e.printStackTrace();
				}catch(Exception ex){
					consume = -1;
				}
				
				if (localCache != null){
					MemcachedResponse response = (MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE);
					if (response != null && response.getResponses() != null)
						response.getResponses().add(consume);
				}
			}
			Logger.info("statistic task stop");
		}
		
		/**
		 * 发送请求
		 * @return
		 */
		private long checkResponse(){
			if (localCache.get(CACHE_STATUS_RESPONSE)== null){
				MemcachedResponse response = new MemcachedResponse();
				response.setCacheName(this.getName());
				localCache.putQuiet(CACHE_STATUS_RESPONSE, response);
			}else if (((MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE))
						.getEndTime().before(new Date())){
				((MemcachedResponse)localCache.get(CACHE_STATUS_RESPONSE)).ini();
			}
			long consume = System.currentTimeMillis();
			putQuiet(CACHE_STATUS_RESPONSE,CACHE_STATUS_RESPONSE);
			get(CACHE_STATUS_RESPONSE);
			consume = System.currentTimeMillis() - consume;
			return consume;
		}
		
		public void stopTask(){
			flag = false;
			interrupt();
		}

		public boolean isFlag(){
			return flag;
		}
		public void setFlag(boolean flag){
			this.flag = flag;
		}
	}
}
