package com.baobao.utils.cache;

/**
 * 缓存调用数据库进行操作
 * @author 江铁扣
 * @since 2009-11-10
 *
 */
public interface DBInvocationInterface<K, V> {

	/**
	 * 利用指定的key到数据库中查询，并将查询返回的结果放入指定的缓存中
	 * @param key
	 * @return
	 */
	public V invokeDBQuery (K key);
	
}
