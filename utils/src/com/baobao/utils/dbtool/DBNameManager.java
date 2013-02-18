package com.baobao.utils.dbtool;

import com.baobao.utils.dbtool.hash.DefaultNodeLocator;

/**
 * 根据key获得其对应记录所在数据库的名字
 * @author 江铁扣注释
 * @since 2010-11-25
 */
public class DBNameManager {

	private static DBCPManager dbcpManager = DBCPManager.getInstance();
	private static DefaultNodeLocator nodeLocator = new DefaultNodeLocator(dbcpManager.getNodeList());
	
	/**
	 * 获取公共库名字
	 */
	public static String getPubDBName(){
		return dbcpManager.getPublicDBName();
	}

	/**
	 * 获取散列库名字
	 * @param key Long型的key值
	 */
	public static synchronized String getHashDBName(Long key){
		return ((DBNode)nodeLocator.getPrimary(String.valueOf(key))).getDBName();
	}
	
	/**
	 * 获取散列库名字
	 * @param key Integer型的key值
	 */
	public static synchronized String getHashDBName(Integer key){
		return ((DBNode)nodeLocator.getPrimary(String.valueOf(key))).getDBName();
	}
	
	/**
	 * 获取散列库名字
	 * @param key String型的key值
	 */
	public static synchronized String getHashDBName(String key){
		return ((DBNode)nodeLocator.getPrimary(key)).getDBName();
	}
}
