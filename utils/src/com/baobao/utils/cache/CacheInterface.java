package com.baobao.utils.cache;

import java.util.Set;

/**
 * Cache接口
 * Cache接口是缓存系统的核心接口，Cache中存储缓存的对象Element，并由CacheManager进行管理
 * Cache只做逻辑上的操作，实际的实现交由com.wanmei.sns.cache.store.Store来做
 * @author 江铁扣
 * @since 2009-11-3
 *
 */
public interface CacheInterface<K, V> {

	/**
	 * 将一个对象放入缓存，并通知观察者做相应处理
	 * @param key 缓存对象的key
	 * @param value 缓存对象的value
	 * @param event 传给观察者的参数
	 * @throws CacheException
	 */
	void put(K key, V value, Event event);
	
	/**
	 * 将一个对象放入缓存，但不通知观察者做相应处理
	 * @param key 缓存对象的key
	 * @param value 缓存对象的value
	 * @throws CacheException
	 */
	void putQuiet(K key, V value);
	
	/**
	 * 从缓存中取出对象，如果缓存中没有该对象则从数据库中查询
	 * @param key 缓存对象的key
	 * @return
	 * @throws CacheException
	 */
	V get(K key) throws CacheException;
	
	/**
	 * 从缓存中取出Element，如果缓存中没有该对象，不再从数据库中查询
	 * @param key an Object's key
	 * @return
	 * @throws CacheException
	 */
	V getQuiet(K key) throws CacheException;
	
	/**
	 * 从缓存中删除一个对象，并通知观察者做相应处理
	 * @param key 缓存对象的key
	 * @param event 传给观察者的参数
	 * @return
	 * @throws CacheException
	 */
	void remove(K key, Event event);
	
	/**
	 * 从缓存中删除一个对象，但不通知观察者做相应处理
	 * @param key 缓存对象的key
	 * @return
	 * @throws CacheException
	 */
	void removeQuiet(K key);
	
	/**
	 * 删除缓存中所有对象， 并通知观察者做相应处理
	 * @param event 传给观察者的参数
	 */
	void removeAll(Event event);
	
	/**
	 * 删除缓存中所有对象， 但不通知观察者做相应处理
	 */
	void removeAllQuiet();
	
	/**
	 * 如果缓存中不存在某个对象，则从数据库中查询，并将返回的结果重新放入缓存中
	 * @param key an Object's key
	 * @return
	 * @throws CacheException
	 */
	V getFromDB(K key);
	
	/**
	 * 返回缓存中所有key的列表
	 * @return
	 * @throws CacheException
	 */
	Set<K> getKeys() ;
	
	/**
	 * 得到缓存中Element的数量
	 * @return
	 */
	int getSize();
	
	/**
	 * 返回Cache的名字
	 * @return
	 */
	String getName();
	
	/**
	 * 设置Cache的名字
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * 返回字符串形式表示的Cache
	 * @return
	 */
	String toString();
	
	/**
	 * 检查key是否存在于Cache中
	 * @param key
	 * @return
	 */
	boolean isKeyInCache(K key);
	
	/**
	 * 检查value是否存在于Cache中
	 * @param value
	 * @return
	 */
	boolean isValueInCache(V value);
	
	/**
	 * 检查Cache是否为空
	 * @param value
	 * @return
	 */
	boolean isEmpty();
	
	/**
	 * 获得Cache的类型
	 * @param value
	 * @return
	 */
	int getType();
	
	/**
	 * 初始化Cache的相关参数
	 */
	void initialize();
	
}
