package com.baobao.utils.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.baobao.utils.LinkedList;
import com.baobao.utils.LinkedListNode;

/**
 * 提供顺序存取的Cache
 * @author 江铁扣
 * @since 2009-11-19
 *
 */

public class OrderedCache<K, V> {

    /**
	 * 缓存名称
	 */
	private String cacheName;
	
	/**
	 * 默认缓存名称
	 */
	private final static String DEFAULT_CACHE_NAME = "default_ordered_cache";
	
	/**
	 * 默认缓存类型
	 */
	private static final int type = CacheUtil.DEFAULT_ORDERED_CACHE_TYPE;

	/**
     * Cache的默认最大容量
     */
    private final static int DEFAULT_CAPACITY = 100000;
    
    /**
     * Cache的最长有效期，默认一直有效（小于等于0）
     */
    private long maxLifeTime = 0;
    
	/**
     * Cache的最大容量，值小于等于0代表没有最大容量限制
     */
    private int capacity;
    
    /**
     * 保存缓存对象的键和值
     */
    private Map<K, OrderedCache.CacheObject<V>> cache;
    
    /**
     * 存放缓存对象放入缓存先后次序的链表，最后放入的位于表头，先放入的位于表尾
     */
    private LinkedList orderedList;
	
    /**
	 * 放入Cache的次数
	 */
	private long cachePuts;
    
    /**
	 * 从Cache取出对象的次数
	 */
	private long cacheGets;
	
	/**
	 * 从Cache中删除对象的次数
	 */
	private long cacheRemoves;
	
	/**
	 * Cache命中的次数
	 */
	private long cacheHits;
	
	/**
	 * Cache未命中的次数
	 */
	private long cacheMisses;
	
	/**
	 * CacheManager
	 */
	private CacheManager cacheManager;
	
	public OrderedCache() {
		this.capacity = DEFAULT_CAPACITY;
		this.cacheName = DEFAULT_CACHE_NAME;
		cache = new HashMap<K, OrderedCache.CacheObject<V>>();
		orderedList = new LinkedList();
		initialize();
	}
	
	public OrderedCache(String cacheName) {
		this.capacity = DEFAULT_CAPACITY;
		if(cacheName != null && cacheName.length() != 0)
			this.cacheName = cacheName;
		else
			this.cacheName = DEFAULT_CACHE_NAME;
		cache = new HashMap<K, OrderedCache.CacheObject<V>>();
		orderedList = new LinkedList();
		initialize();
	}
	
	public OrderedCache(int capacity, String cacheName) {
		if(capacity>0)
			this.capacity = capacity;
		else
			this.capacity = DEFAULT_CAPACITY;
		if(cacheName != null && cacheName.length() != 0)
			this.cacheName = cacheName;
		else
			this.cacheName = DEFAULT_CACHE_NAME;
		cache = new HashMap<K, OrderedCache.CacheObject<V>>();
		orderedList = new LinkedList();
		initialize();
	}
	
	public OrderedCache(int capacity, String cacheName, long maxLifeTime) {
		if(capacity>0)
			this.capacity = capacity;
		else
			this.capacity = DEFAULT_CAPACITY;
		if(cacheName != null && cacheName.length() != 0)
			this.cacheName = cacheName;
		else
			this.cacheName = DEFAULT_CACHE_NAME;
		this.maxLifeTime = maxLifeTime;
		cache = new HashMap<K, OrderedCache.CacheObject<V>>();
		orderedList = new LinkedList();
		initialize();
	}
	
	public synchronized V put(K key, V value) {
		cachePuts++;
		V answer = remove(key); 
		OrderedCache.CacheObject<V> cacheObject = new OrderedCache.CacheObject<V>(value);
		cache.put(key, cacheObject);
		LinkedListNode orderedListNode = orderedList.addFirst(key);
		orderedListNode.timestamp = System.currentTimeMillis();
		cacheObject.orderedListNode = orderedListNode;
		cullCache();
		return answer;
	}
	
	public synchronized V get(K key) {
		cacheGets++;
		OrderedCache.CacheObject<V> cacheObject = cache.get(key);
		if (cacheObject == null) {
            cacheMisses++;
            return null;
        }
		 cacheHits++;
		 return cacheObject.object;
	}
	
	public synchronized V get(int index) {
		cacheGets++;
		deleteExpiredEntries();
		LinkedListNode orderedListNode = orderedList.get(index);
		OrderedCache.CacheObject<V> cacheObject = cache.get((K)orderedListNode.object);
		if (cacheObject == null) {
            cacheMisses++;
            return null;
        }
		 cacheHits++;
		 return cacheObject.object;
	}
	
	public synchronized V remove(K key) {
		OrderedCache.CacheObject<V> cacheObject = cache.get(key);
		if (cacheObject == null) {
            return null;
        }
		cache.remove(key);
		orderedList.remove(cacheObject.orderedListNode);
		cacheRemoves++;
		return cacheObject.object;
	}
	
	public synchronized V remove(int index) {
		LinkedListNode orderedListNode = orderedList.get(index);
		return remove((K)orderedListNode.object);
	}
	
	public synchronized void clear() {
		 Object[] keys = cache.keySet().toArray();
	        for (int i = 0; i < keys.length; i++) {
	            remove((K)keys[i]);
	        }

	        // 充值所有的容器
	        cache.clear();
	        orderedList.clear();
	        orderedList = new LinkedList();
	        
	        cachePuts = 0;
	        cacheGets = 0;
	        cacheRemoves = 0;
	        cacheHits = 0;
	        cacheMisses = 0;
	}
	
	public LinkedList getOrderedList() {
		return orderedList;
	}
	
	public String getName() {
		return cacheName;
	}
	
	public void setName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public long getMaxLifeTime() {
		return maxLifeTime;
	}
	
	public void setMaxLifeTime(long maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}
	
	public int getType() {
		return type;
	}
	
	public long getCachePuts() {
		return cachePuts;
	}
	
	public long getCacheGets() {
		return cacheGets;
	}
	
	public long getCacheRemoves() {
		return cacheRemoves;
	}
	
	public long getCacheHits() {
		return cacheHits;
	}
	
	public long getCacheMisses() {
		return cacheMisses;
	}
	
	/**
     * 返回Cache当前的实际容量
     *
     * @return 
     */
    public long getSize() {
    	deleteExpiredEntries();
        return cache.size();
    }
	
	/**
     * 返回Cache的最大容量
     *
     * @return 
     */
    public long getCapacity() {
        return capacity;
    }
    
    /**
     * 重新设置Cache的最大容量
     * @param newSize 重置后的容量
     * @return 
     */
    public synchronized void reSize(int newSize) {
        if(newSize <= 0 || newSize >= capacity) {
        	capacity = newSize;
        }
        else {
        	while(cache.size() > newSize) {
        		remove((K)orderedList.getLast().object);
        	}
        }
    }
    
    public boolean isEmpty() {
    	deleteExpiredEntries();
    	return cache.isEmpty();
    }
    
    /**
     * 删除过期的缓存对象
     */
    public void deleteExpiredEntries() {
    	if(maxLifeTime <= 0) {
    		return;
    	}
    	LinkedListNode node = orderedList.getLast();
    	if(node == null) {
    		return;
    	}
    	long expireTime = System.currentTimeMillis() - maxLifeTime;
    	while(expireTime > node.timestamp) {
    		remove((K)node.object);
    		node = orderedList.getLast();
    		if(node == null) {
    			return;
    		}
    	}
    } 
    
    /**
     * 当Cache中缓存对象超过最大容量限制时，删除最早放入的那些对象
     */
    public void cullCache() {
    	if(this.capacity <= 0) {
    		return;
    	}
    	else {
    		while(cache.size() > capacity) {
        		remove((K)orderedList.getLast().object);
        	}
    	}
    }
	
	public String toString() {
		StringBuffer dump = new StringBuffer();
		dump.append("[ ")
			.append(" name = ").append(cacheName)
			.append(" cachePuts = ").append(cachePuts)
			.append(" cacheGets = ").append(cacheGets)
			.append(" cacheRemoves = ").append(cacheRemoves)
			.append(" cacheHits = ").append(cacheHits)
			.append(" cacheMisses = ").append(cacheMisses)
			.append(" size = ").append(cache.size())
			.append(" ]");
		return dump.toString();
	}
	
	public void initialize() {
		cachePuts = 0;
		cacheGets = 0;
		cacheRemoves = 0;
		cacheHits = 0;
		cacheMisses = 0;
		this.cacheManager = CacheManager.getInstance();
		try {
			this.cacheManager.addOrderedCache(this);
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e.getMessage());
		}
	}
	
	
	/**
     * 对所有放入Cache中的对象进行包装，从而保持对所有Cache对象的放入Cache先后顺序及其放入时间的引用
     */
    private static class CacheObject<V> {
    	
    	/**
         * CacheObject所包装的放入Cache的对象
         */
        public V object;
        
        /**
         * 保存当前Cache对象节点在顺序链表中位置的引用
         */
        public LinkedListNode orderedListNode;
        
        /**
         * 创建Cache对象的包装对象
         *
         * @param object 被包装的Cache对象
         */
        public CacheObject(V object) {
            this.object = object;
        }
        
    }
	
}
