package com.baobao.utils.memcache.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baobao.utils.memcache.IMemcachedCache;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedCacheManager {
	private static final Log Logger = LogFactory.getLog(MemcachedCacheManager.class);
	
	/**
	 * 配置文件名称
	 */
	private static final String MEMCACHED_CONFIG_FILE ="memcached.xml";
	/**
	 * cache客户端池
	 */
	private ConcurrentHashMap<String,IMemcachedCache> cachepool;
	/**
	 * cache客户端配置的socketio池
	 */
	private ConcurrentHashMap<String,SockIOPool> socketpool;
	
	/**
	 * Memcache客户端配置
	 */
	private List<MemcachedConfig> memcachedConfigs;
	/**
	 * Memcache SocketPool配置
	 */
	private List<MemcachedSocketPoolConfig> memcachedSocketPoolConfigs;
	
	/**
	 * 是否支持读取所有classpath下的配置
	 */
	private boolean supportMultiConfig = false;
	
	/**
	 * memcached指定的配置文件
	 */
	private String configFile = MEMCACHED_CONFIG_FILE;
	
	/**
	 * 响应统计时间间隔(单位秒,默认为0,0表示不需要做响应统计)
	 */
	private int responseStatInterval = 0;
	
	private MemcachedCacheManager(){start();}
	/**
	 * 此类为单点类，方便管理缓存
	 */
	private static MemcachedCacheManager instance = new MemcachedCacheManager();
	
	
	public static MemcachedCacheManager getInstance(){return instance;}
	/* (non-Javadoc)
	 * @see com.alisoft.xplatform.asf.cache.ICacheManager#start()
	 */
	public void start(){
		cachepool = new ConcurrentHashMap<String,IMemcachedCache>();
		socketpool = new ConcurrentHashMap<String,SockIOPool>();
		
		loadConfig(configFile);
		memcachedConfigs = CacheUtil.getMemcachedConfigs();
		memcachedSocketPoolConfigs = CacheUtil.getMemcachedSocketPoolConfigs();
		
		if(memcachedConfigs != null && memcachedConfigs.size() > 0
				&& memcachedSocketPoolConfigs != null && memcachedSocketPoolConfigs.size() > 0){
			try{
				initMemCacheClientPool();
			}catch(Exception ex){
				Logger.error("MemcachedManager init error ,please check !");
				throw new RuntimeException("MemcachedManager init error ,please check !",ex);
			}
		}else{
			Logger.error("no config info for MemcachedManager,please check !");
			throw new RuntimeException("no config info for MemcachedManager,please check !");
		}
	}
	
	/**
	 * 载入配置信息
	 */
	protected void loadConfig(String configFile) {
		try {
			CacheUtil.loadMemcachedConfig(configFile);
			Logger.info(new StringBuilder().append("load config from :").append(configFile));
		} catch (Exception ex) {
			Logger.error("MemcachedManager loadConfig error !");
			throw new RuntimeException("MemcachedManager loadConfig error !",ex);
		}
	}			
	
	/**
	 * 初始化各个资源池
	 */
	protected void initMemCacheClientPool(){
		//初始化socket pool
		for(MemcachedSocketPoolConfig socketPool : memcachedSocketPoolConfigs){
			if (socketPool != null && 
					socketPool.getServers() != null && !socketPool.getServers().equals("")){
				SockIOPool pool = SockIOPool.getInstance(socketPool.getName());
				
				String[] servers = socketPool.getServers().split(",");
				String[] weights = null;
				
				if (socketPool.getWeights() != null && !socketPool.getWeights().equals("") )
					weights = socketPool.getWeights().split(",");
				
				pool.setServers(servers);
				
				if (weights != null && weights.length > 0 && weights.length == servers.length){
					Integer[] weightsarr = new Integer[weights.length];
					
					for(int i = 0 ; i < weights.length; i++)
						weightsarr[i] =  new Integer(weights[i]);
					
					pool.setWeights( weightsarr );
				}
			
				pool.setInitConn(socketPool.getInitConn());
				pool.setMinConn(socketPool.getMinConn());
				pool.setMaxConn(socketPool.getMaxConn());
				pool.setMaintSleep(socketPool.getMaintSleep());
				pool.setSocketTO(socketPool.getSocketTo() );
				pool.setNagle(socketPool.isNagle() );	
				pool.setFailover(socketPool.isFailover());
				pool.setAliveCheck(socketPool.isAliveCheck() );
				pool.setMaxIdle(socketPool.getMaxIdle());
				////设置HASH算法
				pool.setHashingAlg(socketPool.getHashAlgorithm());
				pool.initialize();
				
				if (socketpool.get(socketPool.getName())!= null)
					Logger.error(new StringBuilder("socketpool define duplicate! socketpool name:").append(socketPool.getName()));
				
				socketpool.put(socketPool.getName(), pool);
				Logger.info(new StringBuilder().append(" add socketpool :").append(socketPool.getName()));
			}else{
				Logger.error("MemcachedClientSocketPool config error !");
				throw new RuntimeException("MemcachedClientSocketPool config error !");
			}
		}
		
		
		for(MemcachedConfig node : memcachedConfigs){
			if(node.getClassName() == null || node.getClassName().trim().length() == 0){
				Logger.error("class name is null cache name:" + node.getName());
				continue;
			}
			MemCachedClient client = new MemCachedClient(node.getSocketPool());
			client.setDefaultEncoding(node.getDefaultEncoding());
			client.setCompressEnable(node.isCompressEnable());
			client.setErrorHandler(new MemcachedErrorHandler());
			
			try {
				Object[] args = new Object[3];
				args[0] = node.getName();
				args[1] = client;
				args[2] = responseStatInterval;
				Class newoneClass = Class.forName(node.getClassName());
				
				Class[] argsClass = new Class[3];
				argsClass[0] = args[0].getClass();
				argsClass[1] = args[1].getClass();
				argsClass[2] = args[2].getClass();
				
				Constructor cons = newoneClass.getConstructor(argsClass);
				
				IMemcachedCache cache = (IMemcachedCache) cons.newInstance(args);
				
				if (cachepool.get(node.getName())!= null)
					Logger.error(new StringBuilder("cache define duplicate! cache name :").append(node.getName()));
				
				cachepool.put(node.getName(), cache);
				if(node.getObservable()!=null){
					for(String observable:node.getObservable()){
						IMemcachedCache temp = cachepool.get(observable);
						if(null == temp)
							Logger.error(new StringBuilder("observable does not exist! cache name :").append(temp));
						if(temp instanceof Observable){
							Observable  observableCache = (Observable)(temp);
							////添加观察者
							observableCache.addObserver((Observer)cache);
						}else {
							Logger.error("target class is not observable cachename:"+observable);
						}
					}
				}	
				Logger.info(new StringBuilder().append(" add memcachedClient :").append(node.getName()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
		}
	}
	/**
	 * 根据缓存名得到缓存
	 * @param name
	 * @return
	 */
	public IMemcachedCache getCache(String name){
		return getCachepool().get(name);
	}
	/**
	 * 删除缓存
	 * @param name 缓存名
	 */
	public void removeCache(String name){
		getCachepool().remove(name);
	}
	
	/* (non-Javadoc)
	 * @see com.alisoft.xplatform.asf.cache.ICacheManager#stop()
	 */
	public void stop(){
		try{
			for(Map.Entry<String,IMemcachedCache> entry : cachepool.entrySet()){
				if(null != entry.getValue())
					entry.getValue().destroy();
			}
			
			if (socketpool != null && socketpool.size() > 0){
				for(Map.Entry<String,SockIOPool> pools : socketpool.entrySet()){
					pools.getValue().shutDown();
				}
				socketpool.clear();
			}
		}catch(Exception ex){
			Logger.error("Cache Manager Stop Error!",ex);
		}finally{
			getCachepool().clear();
			if (memcachedConfigs != null)
				memcachedConfigs.clear();
			
			if (memcachedSocketPoolConfigs != null)
				memcachedSocketPoolConfigs.clear();
		}

	}

	public ConcurrentHashMap<String,IMemcachedCache> getCachepool(){
		if (cachepool == null)
			throw new java.lang.RuntimeException("cachepool is null!");
		
		return cachepool;
	}

	public ConcurrentHashMap<String, SockIOPool> getSocketpool(){
		return socketpool;
	}

	public void setSocketpool(ConcurrentHashMap<String, SockIOPool> socketpool){
		this.socketpool = socketpool;
	}


	public boolean isSupportMultiConfig(){
		return supportMultiConfig;
	}

	public void setSupportMultiConfig(boolean supportMultiConfig){
		this.supportMultiConfig = supportMultiConfig;
	}

	public String getConfigFile(){
		return configFile;
	}

	public void setConfigFile(String configFile){
		this.configFile = configFile;
	}

	//@Override
	public void reload(String configFile){
		if (configFile != null 
				&& !configFile.equals(""))
			this.configFile = configFile;
		
		stop();
		start();
	}

	//@Override
	public void setResponseStatInterval(int seconds) {
		this.responseStatInterval = seconds;
	}
}
