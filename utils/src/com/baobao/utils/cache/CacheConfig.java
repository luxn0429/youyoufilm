package com.baobao.utils.cache;

/**
 * 通过配置文件方式生成缓存时的相应配置
 * @author 江铁扣
 *
 */
public class CacheConfig {

	/**
	 * 缓存的名字
	 */
	private String name;
	
	/**
	 * 缓存的类型，1：PrimaryCache 2：IndexCache 3：ConditionCache 4：OrderedCache
	 */
	private String type;
	
	/**
	 * 缓存的最大容量
	 */
	private String capacity;
	
	/**
	 * 缓存对象的最长有效期（只针对OrderedCache）
	 */
	private String maxLifeTime;
	
	/**
	 * 缓存实现类的名字
	 */
	private String className;
	
	/**
	 * 缓存对应数据库回调函数的实现类的名字
	 */
	private String dbInvocationName;
	
	/**
	 * 观察者的名字，多个观察者之间以","隔开
	 */
	private String observers;
	
	/**
	 * 被观察者的名字，多个被观察者之间以","隔开
	 */
	private String observables;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDbInvocationName() {
		return dbInvocationName;
	}

	public void setDbInvocationName(String dbInvocationName) {
		this.dbInvocationName = dbInvocationName;
	}

	public String getObservers() {
		return observers;
	}

	public void setObservers(String observers) {
		this.observers = observers;
	}

	public String getObservables() {
		return observables;
	}

	public void setObservables(String observables) {
		this.observables = observables;
	}

	public String getMaxLifeTime() {
		return maxLifeTime;
	}

	public void setMaxLifeTime(String maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}
	
}
