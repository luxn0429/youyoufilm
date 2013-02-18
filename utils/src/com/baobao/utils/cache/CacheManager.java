package com.baobao.utils.cache;

//import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
/**
 * Cache管理类，管理所有Cache的完整生命周期
 * @author 江铁扣
 * @since 2009-11-4
 *
 */
public class CacheManager {

	/**
	 * 单例模式的CacheManager
	 */
	private static CacheManager singleton = null;
	
	/**
	 * CacheManager所管理的全部PrimaryCache
	 */
	private Map<String, PrimaryCache> primaryCaches = new ConcurrentHashMap<String, PrimaryCache>();
	
	/**
	 * CacheManager所管理的全部IndexCache
	 */
	private Map<String, IndexCache> indexCaches = new ConcurrentHashMap<String, IndexCache>();
	
	/**
	 * CacheManager所管理的全部ConditionCache
	 */
	private Map<String,ConditionCache> conditionCaches = new ConcurrentHashMap<String, ConditionCache>();
	
	/**
	 * CacheManager所管理的全部OrderedCache
	 */
	private Map<String,OrderedCache> orderedCaches = new ConcurrentHashMap<String, OrderedCache>();
	
	/**
	 * 存放所有Cache的名称（每个Cache的名称必须唯一）
	 */
	private List<String> cacheNames = new CopyOnWriteArrayList<String>();
	
	private CacheManager() {
		//init();
	}
	
	/**
	 * 单例模式的CacheManager
	 * @return
	 */
	public synchronized static CacheManager getInstance() {
		if(null == singleton)
			singleton = new CacheManager();
		return singleton;
	}
	
	/**
	 * 初始化CacheManager（初始化时统一一次性生成所有的Cache并注册彼此间的观察者<-->被观察者关系，利用反射生成Cache的实例）
	 */
	public boolean init() {
		CacheConfigParser ccp = CacheConfigParser.getInstance();
		boolean flag = ccp.init();
		if(flag) {
			//生成Cache实例
			Iterator<Map.Entry<String, CacheConfig>> it = ccp.getCacheConfigMap().entrySet().iterator();
			CacheConfig cacheConfig;
			while(it.hasNext()) {
				cacheConfig = it.next().getValue();
				Class[] paramTypes = new Class[3];
				Object[] params = new Object[3];//数组初始化大小设置...
				Class cacheClass, dbInvocationClass;
				java.lang.reflect.Constructor cacheConstructor, dbInvocationConstructor;
				try {
					if(cacheConfig.getType().equals("1") || cacheConfig.getType().equals("2") 
							|| cacheConfig.getType().equals("3")){
						cacheClass = Class.forName(cacheConfig.getClassName());
						paramTypes[0] = int.class;
						paramTypes[1] = String.class;
						paramTypes[2] = DBInvocationInterface.class;
						cacheConstructor = cacheClass.getConstructor(paramTypes);
						if(!cacheConfig.getCapacity().equals("")) {
							params[0] = Integer.valueOf(cacheConfig.getCapacity());
						}
						else {
							params[0] = 0;
						}
						params[1] = cacheConfig.getName();
						if(cacheConfig.getDbInvocationName().equals("")) {
							params[2] = null;
						}
						else {
							dbInvocationClass = Class.forName(cacheConfig.getDbInvocationName());
							dbInvocationConstructor = dbInvocationClass.getConstructor();
							params[2] = dbInvocationConstructor.newInstance();
						}
						Object o = cacheConstructor.newInstance(params);
					}
					else if(cacheConfig.getType().equals("4")) {
						cacheClass = Class.forName(cacheConfig.getClassName());
						paramTypes[0] = int.class;
						paramTypes[1] = String.class;
						paramTypes[2] = long.class;
						cacheConstructor = cacheClass.getConstructor(paramTypes);
						if(!cacheConfig.getCapacity().equals("")) {
							params[0] = Integer.valueOf(cacheConfig.getCapacity());
						}
						else {
							params[0] = 0;
						}
						params[1] = cacheConfig.getName();
						if(!cacheConfig.getMaxLifeTime().equals("")) {
							params[2] = Long.valueOf(cacheConfig.getMaxLifeTime());
						}
						else {
							params[2] = 0;
						}
						Object o = cacheConstructor.newInstance(params);
					}
					else {
						
						Logger.getLogger(this.getClass()).error("cache type is invalid: type="+cacheConfig.getType());
					}
					/*cacheClass = Class.forName(cacheConfig.getClassName());
					if(!cacheConfig.getCapacity().equals("") && !cacheConfig.getDbInvocationName().equals("")) {
						dbInvocationClass = Class.forName(cacheConfig.getDbInvocationName());
						Class[] paramTypes = new Class[]{int.class, String.class, DBInvocationInterface.class};
						cacheConstructor = cacheClass.getConstructor(paramTypes);
						dbInvocationConstructor = dbInvocationClass.getConstructor();
						Object[] params = new Object[]{Integer.valueOf(cacheConfig.getCapacity()), cacheConfig.getName(), dbInvocationConstructor.newInstance()};
						Object o = cacheConstructor.newInstance(params);
					}
					else if(!cacheConfig.getCapacity().equals("")) {
						Class[] paramTypes = new Class[]{int.class, String.class};
						cacheConstructor = cacheClass.getConstructor(paramTypes);
						Object[] params = new Object[]{Integer.valueOf(cacheConfig.getCapacity()), cacheConfig.getName()};
						Object o = cacheConstructor.newInstance(params);
					}
					else {
						Class[] paramTypes = new Class[]{String.class};
						cacheConstructor = cacheClass.getConstructor(paramTypes);
						Object[] params = new Object[]{cacheConfig.getName()};
						Object o = cacheConstructor.newInstance(params);
					}*/
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.out);
					Logger.getLogger(this.getClass()).error("class not found exception: "+e);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//注册观察者与被观察者
			Iterator<Map.Entry<String, CacheConfig>> it2 = ccp.getCacheConfigMap().entrySet().iterator();
			while(it2.hasNext()) {
				cacheConfig = it2.next().getValue();
				String[] observerNames = cacheConfig.getObservers().split(",");
				String[] observableNames = cacheConfig.getObservables().split(",");
				if(primaryCaches.containsKey(cacheConfig.getName())) {
					primaryCaches.get(cacheConfig.getName()).addObserversByName(observerNames);
				}
				else if(indexCaches.containsKey(cacheConfig.getName())) {
					indexCaches.get(cacheConfig.getName()).addObservablesByName(observableNames);
					indexCaches.get(cacheConfig.getName()).addObserversByName(observerNames);
				}
				else if(conditionCaches.containsKey(cacheConfig.getName())) {
					conditionCaches.get(cacheConfig.getName()).addObservablesByName(observableNames);
				}
				else {
					// orderedCache or timeoutCache, do nothing
				}
			}
			return true;
		}
		else {
			Logger.getLogger(this.getClass()).error("initialize caches failed!");
			return false;
		}
	}
	
	/**
	 * 生成一个PrimaryCache
	 */
	/*public synchronized PrimaryCache creatPrimaryCache(int capacity, String cacheName, DBInvocationInterface dbInvocation, List<Observer> observers) 
			throws CacheException {
		if(cacheNames.contains(cacheName))
			throw new CacheException("Failure! PrimaryCache " + cacheName + " already exists!");
		return new PrimaryCache(capacity, cacheName, dbInvocation, observers);
	}*/
	
	/**
	 * 生成一个IndexCache
	 */
	/*public synchronized <T extends IndexCache> T creatIndexCache(int capacity, String cacheName, DBInvocationInterface dbInvocation, List<Observer> observers) 
			throws CacheException {
		if(cacheNames.contains(cacheName))
			throw new CacheException("Failure! IndexCache " + cacheName + " already exists!");
		//return new T(capacity, cacheName, dbInvocation, observers);
		//to do 
		return null;
	}*/

	/**
	 * 生成一个ConditionCache
	 */
	/*public synchronized <T extends PrimaryCache> T  creatConditionCache(int capacity, String cacheName, DBInvocationInterface dbInvocation) 
			throws CacheException {
		if(cacheNames.contains(cacheName))
			throw new CacheException("Failure! ConditionCache " + cacheName + " already exists!");
		//return new ConditionCache(capacity, cacheName, dbInvocation);
		//to do
		return null;
	}*/
	
	/**
	 * 获得PrimaryCache
	 * @param cacheName
	 * @return
	 */
	public synchronized PrimaryCache getPrimaryCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name is null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		return primaryCaches.get(cacheName);
	}
	
	/**
	 * 获得IndexCache
	 * @param cacheName
	 * @return
	 */
	public synchronized IndexCache getIndexCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name is null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		return indexCaches.get(cacheName);
	}
	
	/**
	 * 获得ConditionCache
	 * @param cacheName
	 * @return
	 */
	public synchronized ConditionCache getConditionCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name is null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		return conditionCaches.get(cacheName);
	}
	
	/**
	 * 获得OrderedCache
	 * @param cacheName
	 * @return
	 */
	public synchronized OrderedCache getOrderedCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name is null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		return orderedCaches.get(cacheName);
	}
	
	/**
	 * 添加PrimaryCache
	 * @param cache
	 * @throws CacheException
	 */
	public synchronized void addPrimaryCache(PrimaryCache cache) throws CacheException {
		if(cache.getName() == null || cache.getName().length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(cacheNames.contains(cache.getName())) {
			Logger.getLogger(this.getClass()).error("primary cache "+ cache.getName() +" already exists");
			throw new CacheException("primary cache "+ cache.getName() +" already exists");
		}
		primaryCaches.put(cache.getName(), cache);
		cacheNames.add(cache.getName());
	}
	
	/**
	 * 添加IndexCache
	 * @param cache
	 * @throws CacheException
	 */
	public synchronized void addIndexCache(IndexCache cache) throws CacheException {
		if(cache.getName() == null || cache.getName().length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(cacheNames.contains(cache.getName())) {
			Logger.getLogger(this.getClass()).error("index cache "+ cache.getName() +" already exists");
			throw new CacheException("index cache "+ cache.getName() +" already exists");
		}
		indexCaches.put(cache.getName(), cache);
		cacheNames.add(cache.getName());
	}
	
	/**
	 * 添加ConditionCache
	 * @param cache
	 * @throws CacheException
	 */
	public synchronized void addConditionCache(ConditionCache cache) throws CacheException {
		if(cache.getName() == null || cache.getName().length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(cacheNames.contains(cache.getName())) {
			Logger.getLogger(this.getClass()).error("condition cache "+ cache.getName() +" already exists");
			throw new CacheException("condition cache "+ cache.getName() +" already exists");
		}
		conditionCaches.put(cache.getName(), cache);
		cacheNames.add(cache.getName());
	}
	
	/**
	 * 添加OrderedCache
	 * @param cache
	 * @throws CacheException
	 */
	public synchronized void addOrderedCache(OrderedCache cache) throws CacheException {
		if(cache.getName() == null || cache.getName().length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(cacheNames.contains(cache.getName())) {
			Logger.getLogger(this.getClass()).error("condition cache "+ cache.getName() +" already exists");
			throw new CacheException("condition cache "+ cache.getName() +" already exists");
		}
		orderedCaches.put(cache.getName(), cache);
		cacheNames.add(cache.getName());
	}
	
	/**
	 * 删除PrimaryCache
	 * @param cacheName
	 * @throws CacheException
	 */
	public synchronized void removePrimaryCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(!primaryCaches.containsKey(cacheName)) {
			Logger.getLogger(this.getClass()).error("primary cache " + cacheName + " does not exist");
			throw new CacheException("primary cache " + cacheName + " does not exist");
		}
		Set<String> observerNames = primaryCaches.get(cacheName).getObserverNames();
		for(String observerName:observerNames) {
			if(indexCaches.get(observerName) != null)
				indexCaches.get(observerName).deleteObservableByName(cacheName);
			if(conditionCaches.get(observerName) != null)
				conditionCaches.get(observerName).deleteObservableByName(cacheName);
		}
		primaryCaches.remove(cacheName);
		cacheNames.remove(cacheName);
	}
	
	/**
	 * 删除IndexCache
	 * @param cacheName
	 * @throws CacheException
	 */
	public synchronized void removeIndexCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(!indexCaches.containsKey(cacheName)) {
			Logger.getLogger(this.getClass()).error("index cache " + cacheName + " does not exist");
			throw new CacheException("index cache " + cacheName + " does not exist");
		}
		Set<String> observerNames = indexCaches.get(cacheName).getObserverNames();
		for(String observerName:observerNames) {
			if(indexCaches.get(observerName) != null)
				indexCaches.get(observerName).deleteObservableByName(cacheName);
			if(conditionCaches.get(observerName) != null)
				conditionCaches.get(observerName).deleteObservableByName(cacheName);
		}
		Set<String> observableNames = indexCaches.get(cacheName).getObservableNames();
		for(String observableName:observableNames) {
			if(primaryCaches.get(observableName) != null)
				primaryCaches.get(observableName).deleteObserverByName(cacheName);
			else if(indexCaches.get(observableName) != null)
				indexCaches.get(observableName).deleteObserverByName(cacheName);
		}
		indexCaches.remove(cacheName);
		cacheNames.remove(cacheName);
	}
	
	/**
	 * 删除ConditionCache
	 * @param cacheName
	 * @throws CacheException
	 */
	public synchronized void removeConditionCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(!conditionCaches.containsKey(cacheName)) {
			Logger.getLogger(this.getClass()).error("condition cache " + cacheName + " does not exist");
			throw new CacheException("condition cache " + cacheName + " does not exist");
		}
		Set<String> observableNames = conditionCaches.get(cacheName).getObservableNames();
		for(String observableName:observableNames) {
			if(primaryCaches.get(observableName) != null)
				primaryCaches.get(observableName).deleteObserverByName(cacheName);
			else if(indexCaches.get(observableName) != null)
				indexCaches.get(observableName).deleteObserverByName(cacheName);
		}
		conditionCaches.remove(cacheName);
		cacheNames.remove(cacheName);
	}
	
	/**
	 * 删除OrderedCache
	 * @param cacheName
	 * @throws CacheException
	 */
	public synchronized void removeOrderedCache(String cacheName) throws CacheException {
		if(cacheName == null || cacheName.length() == 0) {
			Logger.getLogger(this.getClass()).error("cache name can not be null or empty");
			throw new CacheException("cache name can not be null or empty");
		}
		if(!orderedCaches.containsKey(cacheName)) {
			Logger.getLogger(this.getClass()).error("ordered cache " + cacheName + " does not exist");
			throw new CacheException("ordered cache " + cacheName + " does not exist");
		}
		orderedCaches.remove(cacheName);
		cacheNames.remove(cacheName);
	}
	
	/**
	 * 删除所有Cache
	 */
	public synchronized void removeAll() {
		primaryCaches.clear();
		indexCaches.clear();
		conditionCaches.clear();
		orderedCaches.clear();
		cacheNames.clear();
	}
	
	/**
	 * 清空所有Cache
	 */
	public synchronized void clearAll() {
		for(PrimaryCache primaryCache:primaryCaches.values()) {
			primaryCache.removeAllQuiet();
		}
		for(IndexCache indexCache:indexCaches.values()) {
			indexCache.removeAllQuiet();
		}
		for(ConditionCache conditionCache:conditionCaches.values()) {
			conditionCache.removeAllQuiet();
		}
		for(OrderedCache orderedCache:orderedCaches.values()) {
			orderedCache.clear();
		}
	}
	
	public List<String> getCacheNames() {
		return cacheNames;
	}
	
}
