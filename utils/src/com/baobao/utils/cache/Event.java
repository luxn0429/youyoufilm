package com.baobao.utils.cache;

import java.io.Serializable;


/**
 * 被观察者传给观察者的参数
 * @author 江铁扣
 * @since 2009-11-6
 *
 */
public class Event implements Serializable {

	/**
	 * 被观察者传给观察者的参数
	 */
	private Object param;
	
	/**
	 * 被观察者告知观察者自己做了何种操作（增、删、改、查）
	 */
	private String operation;
	
	public Event(Object param, String operation) {
		this.param = param;
		this.operation = operation;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
