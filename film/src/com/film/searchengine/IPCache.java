/**
 * 
 */
package com.film.searchengine;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.film.dao.bean.IPBean;
import com.film.dao.factory.DaoFactory;

/**
 * @author xiangning
 *
 */
public class IPCache {
	
	private Queue<IPBean> ipQueue = new LinkedList<IPBean>();
	/**
	 * 
	 */
	private IPCache() {
		loadIP();
	}
	
	private static final IPCache instance = new IPCache();
	public static final IPCache getInstance(){return instance;}
	public void loadIP(){
		List<IPBean> list = DaoFactory.getInstance().getIPDao().getValidIP();
		ipQueue.clear();
		ipQueue.addAll(list);
	}
	
	public synchronized IPBean getIPBean(){
		IPBean bean = ipQueue.poll();
		ipQueue.add(bean);
		return bean;
	}

}
