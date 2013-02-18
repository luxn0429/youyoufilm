package com.film.dao.bean;

import java.io.Serializable;

public class Hot<K> implements Comparable<Hot<K>>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3854855437910633578L;
	private K key;		//热点Key
	private int number;		//微博转发数
	private int time;
	
	@Override
	public int compareTo(Hot<K> o) {
		int result = Integer.valueOf(o.number).compareTo(this.number);
		return result == 0 ? Integer.valueOf(o.time).compareTo(this.time) : result;
	}
	
	public K getKey() {
		return key;
	}
	
	public void setKey(K key) {
		this.key = key;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		this.time = time;
	}
	
	
}
