package com.baobao.utils.cache;

public class CacheUtil {
	
	/**
	 * PrimaryCache的默认名称
	 */
	public static final String DEFAULT_PRIMARY_CACHE_NAME = "default_primary_cache";
	
	/**
	 * PrimaryCache的默认类型值
	 */
	public static final int DEFAULT_PRIMARY_CACHE_TYPE = 1;
	
	/**
	 * IndexCache的默认名称
	 */
	public static final String DEFAULT_INDEX_CACHE_NAME = "default_index_cache";
	
	/**
	 * IndexCache的默认类型值
	 */
	public static final int DEFAULT_INDEX_CACHE_TYPE = 2;
	
	/**
	 * ConditionCache的默认名称
	 */
	public static final String DEFAULT_CONDITION_CACHE_NAME = "default_condition_cache";
	
	/**
	 * ConditionCache的默认类型值
	 */
	public static final int DEFAULT_CONDITION_CACHE_TYPE = 3;
	
	/**
	 * OrderedCache的默认名称
	 */
	public static final String DEFAULT_ORDERED_CACHE_NAME = "default_ordered_cache";
	
	/**
	 * OrderedCache的默认类型值
	 */
	public static final int DEFAULT_ORDERED_CACHE_TYPE = 4;
	
	/**
	 * TimeoutCache的默认名称
	 */
	public static final String DEFAULT_TIMEOUT_CACHE_NAME = "default_timeout_cache";
	
	/**
	 * TimeoutCache的默认类型值
	 */
	public static final int DEFAULT_TIMEOUT_CACHE_TYPE = 5;
	
	/**
	 * 添加指令
	 */
	public static final String ADD_EVENT = "add";
	
	/**
	 * 更新指令
	 */
	public static final String UPDATE_EVENT = "update";
	
	/**
	 * 删除指令
	 */
	public static final String REMOVE_EVENT = "remove";
	
	/**
	 * 全部删除指令
	 */
	public static final String REMOVEALL_EVENT = "removeAll";
	
	/**
	 * 所有类型，包括分享、日志等等
	 */
	public static final Integer ALL_TYPE = 0;
	
	/**
	 * 通过数据库名找到对应的缓存
	 * @param dbName
	 * @return
	 */
	public static String getCacheFromDBName(String dbName) {
		//to do(映射规则)
		return null;
	}
	
}
