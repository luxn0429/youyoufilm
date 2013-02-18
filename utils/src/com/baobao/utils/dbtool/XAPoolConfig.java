package com.baobao.utils.dbtool;

/**
 * 分布式数据库连接池配置
 * @author luxiangning
 *
 */
public class XAPoolConfig implements Cloneable {

	/**
	 * 使用的JDBC驱动的完整有效的java 类名
	 */
	private String driverClassName;
	
	/**
	 * 传递给JDBC驱动的用于建立连接的用户名
	 */
	private String username;
	
	/**
	 * 传递给JDBC驱动的用于建立连接的密码
	 */
	private String password;
	
	/**
	 * 数据库所在主机的ip地址
	 */
	private String ip;
	
	/**
	 * 数据库端口号
	 */
	private String port;
	
	/**
	 * 数据库名称
	 */
	private String dbname;
	
	/**
	 * 主机所属群组的id
	 */
	private String groupId;
	
	/**
	 * 主机所属群组的类型
	 */
	private String groupType;
	
	/**
	 * 主机在群组内的id，id=1代表为master，否则为slave
	 */
	private String hostId;
	
	/**
	 * 建立新连接时被发送给JDBC驱动的连接参数，格式必须是 [propertyName=property;]*
	 */
	private String connectionProperties;
	
	/**
	 * 数据库连接池的描述信息
	 */
	private String description;
	
	/**
	 * PoolKeeper检测时间间隔
	 */
	private String sleepTime;
	
	/**
	 * 连接生命周期（上次访问时间-当前时间）
	 */
	private String lifeTime;
	
	/**
	 * the value of loginTimeout
	 */
	private String loginTimeout;
	
	/**
	 * 超过最大连接之后的调用getConnection的等待时间
	 */
	private String deacLockMaxWait;
	
	/**
	 * 超过最大连接之后的调用getConnection等待，在等待中重试的时间间隔
	 */
	private String deacLockRetryWait;
	
	/**
	 * 连接池的最大容量
	 */
	private String maxSize;
	
	/**
	 * 连接池的最小容量
	 */
	private String minSize;
	
	/**
	 * the String to test the jdbc connection before using it
	 */
	private String jdbcTestStmt;
	
	/**
	 * 
	 */
	private String transactionIsolation;
	
	/**
	 * preparedStmtCacheSize?
	 */
	private String preparedStmtCacheSize;
	
	/**
	 * the garbage collection option
	 */
	private String gc;
	
	/**
	 * the debug flag
	 */
	private String debug;
	
	/**
	 * the verbose value?
	 */
	private String verbose;

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(String connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(String sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(String lifeTime) {
		this.lifeTime = lifeTime;
	}

	public String getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(String loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	public String getDeacLockMaxWait() {
		return deacLockMaxWait;
	}

	public void setDeacLockMaxWait(String deacLockMaxWait) {
		this.deacLockMaxWait = deacLockMaxWait;
	}

	public String getDeacLockRetryWait() {
		return deacLockRetryWait;
	}

	public void setDeacLockRetryWait(String deacLockRetryWait) {
		this.deacLockRetryWait = deacLockRetryWait;
	}

	public String getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}

	public String getMinSize() {
		return minSize;
	}

	public void setMinSize(String minSize) {
		this.minSize = minSize;
	}

	public String getJdbcTestStmt() {
		return jdbcTestStmt;
	}

	public void setJdbcTestStmt(String jdbcTestStmt) {
		this.jdbcTestStmt = jdbcTestStmt;
	}

	public String getTransactionIsolation() {
		return transactionIsolation;
	}

	public void setTransactionIsolation(String transactionIsolation) {
		this.transactionIsolation = transactionIsolation;
	}

	public String getPreparedStmtCacheSize() {
		return preparedStmtCacheSize;
	}

	public void setPreparedStmtCacheSize(String preparedStmtCacheSize) {
		this.preparedStmtCacheSize = preparedStmtCacheSize;
	}

	public String getGc() {
		return gc;
	}

	public void setGc(String gc) {
		this.gc = gc;
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public String getVerbose() {
		return verbose;
	}

	public void setVerbose(String verbose) {
		this.verbose = verbose;
	}
	@Override
	public XAPoolConfig clone(){
		XAPoolConfig obj = null;
		try {
			obj = (XAPoolConfig)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
