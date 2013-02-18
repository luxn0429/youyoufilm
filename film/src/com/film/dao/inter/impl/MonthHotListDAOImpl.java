package com.film.dao.inter.impl;

import com.film.dao.bean.Hot;
import java.util.*;

public class MonthHotListDAOImpl extends AbstractHotListDAOImpl<List<Hot<Long>>>
{
	public MonthHotListDAOImpl(String tableName)
	{
		super(tableName);
		insert = "insert into " + TABLE_NAME + "(month,type,content) values(?,?,?) on duplicate key update content=?";
		select = "select content from " + TABLE_NAME+ " where month=? and type=?";
	}
}
