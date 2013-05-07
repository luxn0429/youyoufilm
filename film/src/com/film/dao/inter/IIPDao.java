package com.film.dao.inter;

import java.util.List;

import com.film.dao.bean.IPBean;

public interface IIPDao {
	void insert(String ip);
	
	List<IPBean> getValidIP();
	
	boolean modifyIPState(int id,int state);
	
	boolean deleteNotValidIP();
	
}
