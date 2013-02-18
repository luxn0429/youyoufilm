package com.baobao.utils.memcache.impl;


/**
 * 
 * SocketIO Pool的配置
 * @author wenchu.cenwc<wenchu.cenwc@alibaba-inc.com>
 *
 */
public class MemcachedSocketPoolConfig
{
	/**
	 * pool名
	 */
	private String name;
	/**
	 * 用于当配置多个memcache服务器时，如果设置为true,当socket连接池连接一个服务器失败时候，连接池尝试连接其他服务器
	 * 如果设置为false，则当获取socket失败时候，直接返回null
	 */
	private boolean failover = true;
	/**
	 * 连接池中每个服务器初始连接数量
	 */
	private int initConn = 10;
	/**
	 * 连接池中每个服务器最小连接数量
	 */
	private int minConn = 5;
	/**
	 * 连接池中每个服务器最大连接数量
	 */
	private int maxConn = 250;
	/**
	 * 这个参数很重要，检查Pool,对于连接池有维护的作用,ms作为单位
	 */
	private int maintSleep = 1000 * 3;
	/**
	 * 当socket创建时候，是否开启nagle算法，true开启,false关闭
	 * nagle算法主要用来解决小包问题，对缓冲区中的一定数量的消息进行自动连接
	 */
	private boolean nagle = false;
	/**
	 * Socket TimeOut配置
	 */
	private int socketTo = 3000;
	/**
	 * socket在处理前是否需要作心跳交验
	 */
	private boolean aliveCheck = true;
	/**
	 * max idle time in ms
	 */
	private int maxIdle = 3 * 1000;
	
	/**
	 * 分布的memcached服务器的列表字段，用逗号分割，服务器地址加端口号
	 */
	private String servers;
	/**
	 * 是否需要设置这些服务器的权重
	 */
	private String weights;
	/**
	 * KEY HASH算法
	 * int NATIVE_HASH = 0; // native String.hashCode();
	 * int OLD_COMPAT_HASH = 1; // original compatibility
	 * hashing algorithm (works with other clients)
	 * int NEW_COMPAT_HASH = 2; // new CRC32 based
	 * compatibility hashing algorithm (works with other clients)
	 * int CONSISTENT_HASH = 3; // MD5 Based -- Stops
	 */
	private int hashAlgorithm = 0;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFailover() {
		return failover;
	}
	public void setFailover(boolean failover) {
		this.failover = failover;
	}
	public int getInitConn() {
		return initConn;
	}
	public void setInitConn(int initConn) {
		this.initConn = initConn;
	}
	public int getMinConn() {
		return minConn;
	}
	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}
	public int getMaxConn() {
		return maxConn;
	}
	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}
	public int getMaintSleep() {
		return maintSleep;
	}
	public void setMaintSleep(int maintSleep) {
		this.maintSleep = maintSleep;
	}
	public boolean isNagle() {
		return nagle;
	}
	public void setNagle(boolean nagle) {
		this.nagle = nagle;
	}
	public int getSocketTo() {
		return socketTo;
	}
	public void setSocketTo(int socketTo) {
		this.socketTo = socketTo;
	}
	public boolean isAliveCheck() {
		return aliveCheck;
	}
	public void setAliveCheck(boolean aliveCheck) {
		this.aliveCheck = aliveCheck;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public String getServers() {
		return servers;
	}
	public void setServers(String servers) {
		this.servers = servers;
	}
	public String getWeights() {
		return weights;
	}
	public void setWeights(String weights) {
		this.weights = weights;
	}
	public int getHashAlgorithm() {
		return hashAlgorithm;
	}
	public void setHashAlgorithm(int hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}
	
}
