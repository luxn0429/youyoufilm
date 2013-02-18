package com.baobao.utils.cache;

import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

public abstract class IndexCache<K, V> extends Observable implements CacheInterface<K, V>, Observer {

	/**
	 * 缓存名称
	 */
	protected String cacheName;
	
	/**
	 * 缓存类型
	 */
	protected static final int type = CacheUtil.DEFAULT_INDEX_CACHE_TYPE;
	
	/**
	 * 存储缓存对象的LRUMap
	 */
	protected Map<K,V> cache; 
	
	/**
	 * 调用数据库的接口，使用者需要自己实现DBInvocationInterface接口及其方法
	 */
	protected DBInvocationInterface<K,V> dbInvocation;
	
	/**
	 * 放入Cache的次数
	 */
	protected AtomicLong cachePuts = new AtomicLong();
	
	/**
	 * 从Cache取出对象的次数
	 */
	protected AtomicLong cacheGets = new AtomicLong();
	
	/**
	 * 从Cache中删除对象的次数
	 */
	protected AtomicLong cacheRemoves = new AtomicLong();
	
	/**
	 * Cache命中的次数
	 */
	protected AtomicLong cacheHits = new AtomicLong();
	
	/**
	 * Cache未命中的次数
	 */
	protected AtomicLong cacheMisses = new AtomicLong();
	
	/**
	 * 观察者列表
	 */
	protected Set<String> observerNames = new HashSet<String>();
	
	/**
	 * 被观察者列表
	 */
	protected Set<String> observableNames = new HashSet<String>();
	
	/**
	 * CacheManager
	 */
	protected CacheManager cacheManager;
	
	public IndexCache() {
		this.cache = new LRUMap<K, V>();
		this.cacheName = CacheUtil.DEFAULT_INDEX_CACHE_NAME;
		initialize();
	}
	
	public IndexCache(String cacheName) {
		this.cache = new LRUMap<K, V>();
		if(cacheName == null || cacheName.length() == 0)
			this.cacheName = CacheUtil.DEFAULT_PRIMARY_CACHE_NAME;
		else
			this.cacheName = cacheName;
		initialize();
	}
	
	public IndexCache(int capacity) {
		if(capacity <= 0)
			this.cache = new LRUMap<K, V>();
		else
			this.cache = new LRUMap<K, V>(capacity);
		this.cacheName = CacheUtil.DEFAULT_INDEX_CACHE_NAME;
		initialize();
	}
	
	public IndexCache(int capacity, String cacheName) {
		if(capacity <= 0)
			this.cache = new LRUMap<K, V>();
		else
			this.cache = new LRUMap<K, V>(capacity);
		if(cacheName == null || cacheName.length() == 0)
			this.cacheName = CacheUtil.DEFAULT_PRIMARY_CACHE_NAME;
		else
			this.cacheName = cacheName;
		initialize();
	}
	
	public IndexCache(int capacity, String cacheName, DBInvocationInterface<K,V> dbInvocation) {
		if(capacity <= 0)
			this.cache = new LRUMap<K, V>();
		else
			this.cache = new LRUMap<K, V>(capacity);
		if(cacheName == null || cacheName.length() == 0)
			this.cacheName = CacheUtil.DEFAULT_PRIMARY_CACHE_NAME;
		else
			this.cacheName = cacheName;
		this.dbInvocation = dbInvocation;
		initialize();
	}
	
	/*public IndexCache(int capacity, String cacheName, DBInvocationInterface dbInvocation, List<Observer> observers) {
		this.cache = new LRUMap<K, V>(capacity);
		this.cacheName = cacheName;
		this.dbInvocation = dbInvocation;
		for(Observer observer:observers) {
			addObserver(observer);
		}
		initialize();
	}*/
	
	public IndexCache(int capacity, String cacheName, DBInvocationInterface<K,V> dbInvocation, String[] observerNames, String[] observableNames) {
		if(capacity <= 0)
			this.cache = new LRUMap<K, V>();
		else
			this.cache = new LRUMap<K, V>(capacity);
		if(cacheName == null || cacheName.length() == 0)
			this.cacheName = CacheUtil.DEFAULT_PRIMARY_CACHE_NAME;
		else
			this.cacheName = cacheName;
		this.dbInvocation = dbInvocation;
		initialize();
		addObserversByName(observerNames);
		addObservablesByName(observableNames);
	}
	
	public void put(K key, V value, Event event) {
		/*if(key == null || value == null)
			throw new CacheException("a cache element's key or value can not be null");*/
		/*cachePuts.incrementAndGet();
		synchronized(cache) {
			cache.put(key, value);
		}*/
		putQuiet(key, value);
		setChanged();
		notifyObservers(event);
	}
	
	/**
	 * 将对象放入缓存，但不通知观察者
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void putQuiet(K key, V value) {
		/*if(key == null || value == null)
			throw new CacheException("a cache element's key or value can not be null");*/
		cachePuts.incrementAndGet();
		synchronized(cache) {
			cache.put(key, value);
		}
	}
	
	public V get(K key) {
		/*if(key==null) 
			throw new CacheException("a cache element's key can not be null");*/
		cacheGets.incrementAndGet();
		V value = null;
		synchronized(cache) {
			value = (V) cache.get(key);
		}
		if(value != null) {
			cacheHits.incrementAndGet();
		}
		else {
			cacheMisses.incrementAndGet();
			value = getFromDB(key);
			if(value != null)
				cache.put(key, value);
		}
		return value;
	}
	
	/**
	 * 从Cache中获取对象，如果没有，不再从数据库中查询
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	public V getQuiet(K key) {
		/*if(key==null) 
			throw new CacheException("a cache element's key can not be null");*/
		cacheGets.incrementAndGet();
		V value = null;
		synchronized(cache) {
			value = (V) cache.get(key);
		}
		if(value != null) {
			cacheHits.incrementAndGet();
		}
		else {
			cacheMisses.incrementAndGet();
			/*value = getFromDB(key);
			if(value != null)
				cache.put(key, value);*/
		}
		return value;
	}
	
	public void remove(K key, Event event) {
		/*if(key==null) 
			throw new CacheException("a cache element's key can not be null");*/
		/*cacheRemoves.incrementAndGet();
		synchronized(cache) {
			cache.remove(key);
		}*/
		removeQuiet(key);
		setChanged();
		notifyObservers(event);
	}
	
	/**
	 * 删除对象，但不通知观察者
	 * @param key
	 * @throws CacheException
	 */
	public void removeQuiet(K key) {
		/*if(key==null) 
			throw new CacheException("a cache element's key can not be null");*/
		cacheRemoves.incrementAndGet();
		synchronized(cache) {
			cache.remove(key);
		}
	}
	
	public void removeAll(Event event) {
		/*cacheRemoves.addAndGet(getSize());
		synchronized(cache) {
			cache.clear();
		}*/
		removeAllQuiet();
		setChanged();
		notifyObservers(event);
	}
	
	/**
	 * 清空Cache，但不通知观察者
	 */
	public void removeAllQuiet() {
		cacheRemoves.addAndGet(getSize());
		synchronized(cache) {
			cache.clear();
		}
	}

	public V getFromDB(K key) {
		if(dbInvocation == null)
			return null;
		return dbInvocation.invokeDBQuery(key);
	}
	
	public Set<K> getKeys() {
		return (Set<K>) cache.keySet();
	}
	
	public int getSize() {
		return cache.size();
	}
	
	public String getName() {
		return this.cacheName;
	}
	
	public void setName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public boolean isKeyInCache(K key) {
		return cache.containsKey(key);
	}
	
	public boolean isValueInCache(V value) {
		return cache.containsValue(value);
	}
	
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public int getType() {
		return type;
	}
	
	public DBInvocationInterface<K,V> getDbInvocation() {
		return this.dbInvocation;
	}
	
	public void setDbInvocation(DBInvocationInterface<K,V> dbInvocation) {
		this.dbInvocation = dbInvocation;
	}
	
	public long getCachePuts() {
		return cachePuts.longValue();
	}
	
	public long getCacheGets() {
		return cacheGets.longValue();
	}
	
	public long getCacheRemoves() {
		return cacheRemoves.longValue();
	}
	
	public long getCacheHits() {
		return cacheHits.longValue();
	}
	
	public long getCacheMisses() {
		return cacheMisses.longValue();
	}
	
	public void initialize() {
		this.cachePuts.set(0);
		this.cacheGets.set(0);
		this.cacheRemoves.set(0);
		this.cacheHits.set(0);
		this.cacheMisses.set(0);
		this.cacheManager = CacheManager.getInstance();
		try {
			this.cacheManager.addIndexCache(this);
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e.getMessage());
		}
	}
	
	public String toString() {
		StringBuffer dump = new StringBuffer();
		dump.append("[ ")
			.append(" name = ").append(cacheName)
			.append(" cachePuts = ").append(cachePuts.get())
			.append(" cacheGets = ").append(cacheGets.get())
			.append(" cacheRemoves = ").append(cacheRemoves.get())
			.append(" cacheHits = ").append(cacheHits.get())
			.append(" cacheMisses = ").append(cacheMisses.get())
			.append(" size = ").append(cache.size())
			.append(" ]");
		return dump.toString();
	}
	
	public Set<String> getObserverNames() {
		return observerNames;
	}
	
	public Set<String> getObservableNames() {
		return observableNames;
	}
	/*
	public void deleteObservable(String observableName) {
		observableNames.remove(observableName);
	}
	
	public void deleteObservables() {
		observableNames.clear();
	}*/
	
	/**
	 * 注册观察者
	 */
	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
		if(o instanceof IndexCache) {
			observerNames.add(((IndexCache) o).getName());
			((IndexCache) o).getObservableNames().add(cacheName);
		}
		else if(o instanceof ConditionCache) {
			observerNames.add(((ConditionCache) o).getName());
			((ConditionCache) o).getObservableNames().add(cacheName);
		}
	}
	
	/**
	 * 删除观察者
	 */
	@Override
	public synchronized void deleteObserver(Observer o) {
		super.deleteObserver(o);
		if(o instanceof IndexCache) {
			observerNames.remove(((IndexCache) o).getName());
			((IndexCache) o).getObservableNames().remove(cacheName);
		}
		else if(o instanceof ConditionCache) {
			observerNames.remove(((ConditionCache) o).getName());
			((ConditionCache) o).getObservableNames().remove(cacheName);
		}
	}
	
	/**
	 * 删除所有观察者
	 */
	@Override
	public synchronized void deleteObservers() {
		super.deleteObservers();
		for(String observerName:observerNames) {
			try {
				// to be refined
				if(cacheManager.getIndexCache(observerName) != null)
					cacheManager.getIndexCache(observerName).getObservableNames().remove(cacheName);
				if(cacheManager.getConditionCache(observerName) != null)
					cacheManager.getConditionCache(observerName).getObservableNames().remove(cacheName);
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logger.getLogger(this.getClass()).error(e.getMessage());
			}
		}
		observerNames.clear();
	}
	
	/**
	 * 将指定名字的Observer添加到本Cache的Observer列表中
	 * @param observerName
	 */
	public synchronized void addObserverByName(String observerName) {
		if(observerName == null || observerName.length() == 0)
			return;
		Observer observer = null;
		if(cacheManager.getCacheNames().contains(observerName)) {
			try {
				if((observer = (Observer) cacheManager.getIndexCache(observerName)) != null) {
					addObserver(observer);
				}
				else if((observer = (Observer) cacheManager.getConditionCache(observerName)) != null) {
					addObserver(observer);
				}
				else {
					//do nothing, the Cache named observerName is not an observer
				}
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logger.getLogger(this.getClass()).error(e.getMessage());
			}
		}
	}
	
	/**
	 * 将指定名字的一组Observer添加到本Cache的Observer列表中
	 * @param observerNames
	 */
	public synchronized void addObserversByName(String[] observerNames) {
		if(observerNames == null)
			return;
		Observer observer = null;
		for(String observerName:observerNames) {
			if(cacheManager.getCacheNames().contains(observerName)) {
				try {
					if((observer = (Observer) cacheManager.getIndexCache(observerName)) != null) {
						addObserver(observer);
					}
					else if((observer = (Observer) cacheManager.getConditionCache(observerName)) != null) {
						addObserver(observer);
					}
					else {
						//do nothing, the Cache named observerName is not an observer
					}
				} catch (CacheException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Logger.getLogger(this.getClass()).error(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 将指定名字的Observer从本Cache的Observer列表中删除
	 * @param observerName
	 */
	public synchronized void deleteObserverByName(String observerName) {
		if(observerName == null || observerName.length() == 0)
			return;
		Observer observer = null;
		if(cacheManager.getCacheNames().contains(observerName)) {
			try {
				if((observer = (Observer) cacheManager.getIndexCache(observerName)) != null) {
					deleteObserver(observer);
				}
				else if((observer = (Observer) cacheManager.getConditionCache(observerName)) != null) {
					deleteObserver(observer);
				}
				else {
					//do nothing, the Cache named observerName is not an observer
				}
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logger.getLogger(this.getClass()).error(e.getMessage());
			}
		}
	}
	
	/**
	 * 将指定的Observable的名字添加到本Cache所观察的Observable名字列表中，同时将自己加到该Observable的观察者列表中
	 */
	public synchronized void addObservableByName(String observableName) {
		if(observableName == null || observableName.length() == 0)
			return;
		/*if(!observableNames.contains(observableName)) {
			observableNames.add(observableName);
		}*/
		if(cacheManager.getCacheNames().contains(observableName)) {
			try {
				if(cacheManager.getPrimaryCache(observableName) != null) {
					((PrimaryCache)cacheManager.getPrimaryCache(observableName)).addObserverByName(cacheName);
				}
				else if(cacheManager.getIndexCache(observableName) != null) {
					((IndexCache)cacheManager.getIndexCache(observableName)).addObserverByName(cacheName);
				}
				else {
					//do nothing
				}
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Logger.getLogger(this.getClass()).error(e.getMessage());
			}
		}
	}
	
	/**
	 * 将指定的一组Observable的名字添加到本Cache所观察的Observable名字列表中，同时将自己加到所有Observable的观察者列表中
	 * @param observableNames
	 */
	public synchronized void addObservablesByName(String[] observableNames) {
		if(observableNames == null)
			return;
		/*for(String observableName:observableNames) {
			this.observableNames.add(observableName);
		}*/
		for(String observableName:observableNames) {
			addObservableByName(observableName);
		}
	}
	
	/**
	 * 从本Cache所观察的Observable名字列表中删除指定Observable的名字，同时将自己从该Observable的观察者列表中删除
	 * @param observerName
	 */
	public synchronized void deleteObservableByName(String observableName) {
		if(observableName == null || observableName.length() == 0)
			return;
		/*if(observableNames.contains(observableName)) {
			observableNames.remove(observableName);
		}*/
		if(cacheManager.getCacheNames().contains(observableName)) {
			try {
				if(cacheManager.getPrimaryCache(observableName) != null) {
					((PrimaryCache)cacheManager.getPrimaryCache(observableName)).deleteObserverByName(cacheName);
				}
				else if(cacheManager.getIndexCache(observableName) != null) {
					((IndexCache)cacheManager.getIndexCache(observableName)).deleteObserverByName(cacheName);
				}
				else {
					//do nothing
				}
			} catch (CacheException e) {
				e.printStackTrace();
				Logger.getLogger(this.getClass()).error(e.getMessage());
			}
		}
	}
	
	/**
	 * 清空本Cache所观察的Observable名字列表，同时将自己从所有Observable的观察者列表中删除
	 */
	public synchronized void deleteObservablesByName() {
		//observableNames.clear();
		for(String observableName:observableNames) {
			deleteObservableByName(observableName);
		}
	}
	
	
	/*public void update(Observable o, Object arg) {
		Event event = (Event) arg;
		if(event.getOperation().equals(CacheUtil.ADD_EVENT)) {
			try {
				remove(event.getKey());
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event.getOperation().equals(CacheUtil.UPDATE_EVENT)) {
			try {
				remove(event.getKey());
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event.getOperation().equals(CacheUtil.REMOVE_EVENT)) {
			try {
				remove(event.getKey());
			} catch (CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event.getOperation().equals(CacheUtil.REMOVEALL_EVENT)) {
			removeAll();
		}
	}*/
	
	/**
	 * 通知所有观察者
	 * 此方法是抽象方法，使用者必须实现该方法；
	 * （1）构造传递给观察者的参数对象Object arg，此对象是Event类型
	 * （2）调用super.notifyObservers(Object arg)方法，通知所有观察者做出相应操作
	 */
	/*public abstract void notifyObservers();*/
	
	/**
	 * 接受被观察者的通知并做相应处理
	 * 此方法是抽象方法，使用者必须实现该方法，根据被观察者传过来的参数做相应操作
	 */
	@Override
	public abstract void update(Observable o, Object arg);
	
}
