package com.baobao.utils.memcache.impl;


public class MemcachedConfig
{
	private String name;
	/**
	 * 是否需要压缩
	 */
	private boolean compressEnable;
	/**
	 * 默认编码方式UTF-8
	 */
	private String defaultEncoding;
	/**
	 * 客户端对应的SocketIOPool
	 */
	private String socketPool;
	/**
	 * observable:被观察缓存名，如果observable不为空，则表明缓存为观察者，被观察者一定要在观察者之前被初始化，在配置文件中，
	 			被观察者一定要写在观察者之前，多个被观察者之间使用逗号","间隔；如果observalbe为空则表明缓存是个被观察者;
	 */
	private String[] observable;
	
	private String className;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompressEnable() {
		return compressEnable;
	}

	public void setCompressEnable(boolean compressEnable) {
		this.compressEnable = compressEnable;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public String getSocketPool() {
		return socketPool;
	}

	public void setSocketPool(String socketPool) {
		this.socketPool = socketPool;
	}

	public String[] getObservable() {
		return observable;
	}

	public void setObservable(String[] observable) {
		this.observable = observable;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
