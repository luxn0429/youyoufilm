package com.baobao.utils.dbtool;

/**
 * 管理数据库连接所使用的一些常量
 * @author 江铁扣
 * @since 2009-11-16
 *
 */

public class DBConstants {

	/**
	 * 八种数据库连接的类型（依据是公共数据库还是散列数据库、更新操作（插入、删除、更新）还是查询操作、基本数据源还是分布式数据源）
	 * 散列数据库为1，公共数据库为0；
	 * 数据库插入、更新、删除操作为1，数据库查询操作为0；
	 * 分布式数据源为1，基本数据源为0
	 * 
	 * 数据库连接类型				是否散列数据库			是否更新操作		是否分布式数据源		值
	 * PUBLIC_QUERY_BASIC			0					0				0				0
	 * PUBLIC_QUERY_XA				0					0				1				1
	 * PUBLIC_UPDATE_BASIC			0					1				0				2
	 * PUBLIC_UPDATE_XA				0					1				1				3
	 * HASH_QUERY_BASIC				1					0				0				4
	 * HASH_QUERY_XA				1					0				1				5
	 * HASH_UPDATE_BASIC			1					1				0				6
	 * HASH_UPDATE_XA				1					1				1				7
	 */
	public final static int PUBLIC_QUERY_BASIC = 0;
	public final static int PUBLIC_QUERY_XA = 1;
	public final static int PUBLIC_UPDATE_BASIC = 2;
	public final static int PUBLIC_UPDATE_XA = 3;
	public final static int HASH_QUERY_BASIC = 4;
	public final static int HASH_QUERY_XA = 5;
	public final static int HASH_UPDATE_BASIC = 6;
	public final static int HASH_UPDATE_XA = 7;
	
}
