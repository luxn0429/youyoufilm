package com.baobao.utils.cache;

/**
 * 将缓存中的数据写入数据库
 * @author 江铁扣
 * @since 2010-05-11
 *
 */
public interface PersistenceInterface {

	/**
	 * 将缓存中中未持久化的数据存储到数据库中去
	 */
	public void storeData();
	
}
