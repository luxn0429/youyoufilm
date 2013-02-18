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

public class PrimaryCache extends Observable implements IMemcachedCache ,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4254712644030049811L;
	private static final Log Logger = LogFactory.getLog(PrimaryCache.class);
	////客户端
	private MemCachedClient client;
	private ICache<String,Object> localCache;
	private StatisticsTask task;
	private long statisticsInterval = 5 * 60;//单位秒
	
	private String name = null; ///缓存名
	static final String CACHE_STATUS_RESPONSE = "cacheStatusResponse";
	
	private MemcachedCacheManager cacheManager = MemcachedCacheManager.getInstance();
	
	/**
	 * 观察者列表
	 */
	protected Set<String> observerNames = new HashSet<String>();
	
	public PrimaryCache(String name,MemCachedClient client,Integer statisticsInterval){
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
		return client.delete(getKey(key));
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
		if(o instanceof IndexCache){
			IndexCache index = (IndexCache)o;
			observerNames.add(index.getName()); //记录观察者名字
			index.addObservable(this.getName());///注册自己为被观察者
		}else if(o instanceof ConditionCache){
			ConditionCache cache = (ConditionCache)o;
			observerNames.add(cache.getName());//记录观察者名字
			cache.addObservable(this.getName());///注册自己为被观察者
		}
	}
	
	/**
	 * 删除观察者
	 */
	@Override
	public void deleteObserver(Observer o) {
		super.deleteObserver(o);
		if(o instanceof IndexCache){
			IndexCache cache = (IndexCache)o;
			observerNames.remove(cache.getName());
			cache.deleteObservable(this.getName());
		}else if(o instanceof ConditionCache){
			ConditionCache cache = (ConditionCache)o;
			observerNames.remove(cache.getName());
			cache.deleteObservable(this.getName());
		}
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
}
