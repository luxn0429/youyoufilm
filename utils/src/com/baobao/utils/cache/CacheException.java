package com.baobao.utils.cache;

/**
 * Cache异常信息
 * @author 江铁扣
 * @since 2009-11-3
 *
 */
public class CacheException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6456954067118568516L;

	/**
	 * 默认构造函数
	 */
	public CacheException() {
		super();
	}
	
	/**
	 * 
	 * @param message 异常的详细信息
	 * @param message
	 */
	public CacheException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message 异常的详细信息
	 * @param cause 异常的原因
	 */
	public CacheException(String message, Throwable cause){
		super(message, cause);
	}
	
	/**
	 * 
	 * @param cause 异常的原因
	 */
	public CacheException(Throwable cause) {
		super(cause);
	}
	
}
