package com.baobao.utils.memcache;


import java.util.Collection;
import java.util.Set;

import com.baobao.utils.cache.Event;

public interface ICache<K,V>{
	
	
	/**
	 * 带有触发事件的保存数据
	 * @param key
	 * @param value
	 * @param event 事件
	 * @return
	 */
	public V put(K key,V value,Event event);
	/**
	 * 不带触发事件的保存数据
	 * @param key
	 * @param value
	 * @return
	 */
	public V putQuiet(K key,V value);
	
	/**
	 * 带有触发事件保存有有效期的数据
	 * @param key
	 * @param value
	 * @param 数据超时的秒数
	 * @param event 触发事件
	 * @return
	 */
	public V put(K key,V value, int TTL,Event event);
	/**
	 * 带有触发事件保存有有效期的数据
	 * @param key
	 * @param value
	 * @param 数据超时的秒数
	 * @return
	 */
	public V putQuiet(K key,V value,int TTL);
	/**
	 * 获取缓存数据
	 * @param key
	 * @return
	 */
	public V get(K key);
	
	/**
	 * 带有触发事件移出缓存数据
	 * @param key
	 * @param event 触发事件
	 * @return
	 */
	public boolean remove(K key,Event event);	
	/**
	 * 不带触发事件的移除
	 * @param key	要移除对象ID
	 * @return
	 */
	public boolean removeQuiet(K key);
	/**
	 * 带有触发事件删除所有缓存内的数据
	 * @return
	 */
	public boolean removeAll(Event event);
	/**
	 * 删除缓存中所有数据
	 * @return
	 */
	public boolean removeAllQuiet();
	/**
	 * 缓存数据数量
	 * @return
	 */
	public int size();
	
	/**
	 * 缓存所有的key的集合
	 * @return
	 */
	public Set<K> keySet();
	/**
	 * 缓存的所有value的集合
	 * @return
	 */
	public Collection<V> values();
	
	/**
	 * 是否包含了指定key的数据
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key);
	
	/**
	 * 释放Cache占用的资源
	 */
	public void destroy();
}
