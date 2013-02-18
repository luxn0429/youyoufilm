package com.film.dao.inter;

import java.io.IOException;
import java.sql.*;

public interface IHotTableDAO<K,T>{
	
	
	/**
	 * 插入数据到大表，如果数据存在则更新
	 * @return		插入结果
	 * @throws NullConnectionException
	 * @throws SQLException
	 */
	boolean insertData(K p1,K p2,T data) throws SQLException, IOException;
	/**
	 * 得到数据
	 * @param p1 p2 联合主键
	 * @return		大表中存储的数据
	 * @throws NullConnectionException 
	 * @throws SQLException 
	 */
	T getData(K p1,K p2)throws SQLException, IOException;
}
