package com.film.dao.inter.impl;

import com.film.dao.bean.Hot;
import java.util.*;

public class WeekHotListDAOImpl extends AbstractHotListDAOImpl<List<Hot<Long>>>
{
	public WeekHotListDAOImpl(String tableName)
	{
		super(tableName);
		insert = "insert into " + TABLE_NAME + "(week,type,content) values(?,?,?) on duplicate key update content=?";
		select = "select content from " + TABLE_NAME+ " where week=? and type=?";
	}
}
