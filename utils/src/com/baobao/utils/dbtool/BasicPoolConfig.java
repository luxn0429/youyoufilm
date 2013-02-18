package com.baobao.utils.dbtool;

/**
 * 基本数据库连接池配置
 * @author 江铁扣
 *
 */
public class BasicPoolConfig implements Cloneable{

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
	 * 主机在群组内的id，id="1"代表为master，否则为slave
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
	 * 连接池创建的连接的默认的auto-commit状态
	 * 默认值：true
	 */
	private String defaultAutoCommit;
	
	/**
	 * 连接池创建的连接的默认的read-only状态. 如果没有设置则setReadOnly方法将不会被调用
	 * 默认值：driver default
	 */
	private String defaultReadOnly;
	
	/**
	 * 连接池创建的连接的默认的TransactionIsolation状态:
	 * 		NONE,READ_COMMITTED,READ_UNCOMMITTED,REPEATABLE_READ,SERIALIZABLE
	 * 默认值：driver default
	 */
	private String defaultTransactionIsolation;
	
	/**
	 * 最大活动连接:连接池在同一时间能够分配的最大活动连接的数量, 如果设置为非正数则表示不限制
	 * 默认值：8
	 */
	private String maxActive;
	
	/**
	 * 最大空闲连接:连接池中容许保持空闲状态的最大连接数量,超过的空闲连接将被释放,如果设置为负数表示不限制
	 * 默认值：8
	 */
	private String maxIdle;
	
	/**
	 * 最大等待时间:当没有可用连接时,连接池等待连接被归还的最大时间(以毫秒计数),
	 * 超过时间则抛出异常,如果设置为-1表示无限等待
	 * 默认值:无限
	 */
	private String maxWait;
	
	/**
	 * 指明是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
	 * 默认值：true
	 */
	private String testOnBorrow;
	
	/**
	 * 指明是否在归还到池中前进行检验
	 * 默认值：false
	 */
	private String testOnReturn;
	
	/**
	 * 在空闲连接回收器线程运行期间休眠的时间值,以毫秒为单位. 如果设置为非正数,则不运行空闲连接回收器线程
	 * 默认值：-1
	 */
	private String timeBetweenEvictionRunsMillis;
	
	/**
	 * 在每次空闲连接回收器线程(如果有)运行时检查的连接数量
	 * 默认值：3
	 */
	private String numTestsPerEvictionRun;
	
	/**
	 * 连接在池中保持空闲而不被空闲连接回收器线程(如果有)回收的最小时间值，单位毫秒
	 * 默认值：默认值：1000*60*30
	 */
	private String minEvictableIdleTimeMillis;
	
	/**
	 * 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除
	 * 默认值：false
	 */
	private String testWhileIdle;
	
	/**
	 * 用来验证从连接池取出的连接,在将连接返回给调用者之前.如果指定,则查询必须是一个SQL SELECT并且必须返回至少一行记录
	 * 默认值：SQL查询，例如"select 1"
	 */
	private String validationQuery;
	
	/**
	 * 开启池的prepared statement 池功能
	 * 默认值：false
	 */
	private String poolPreparedStatements;
	
	/**
	 * statement池能够同时分配的打开的statements的最大数量, 如果设置为0表示不限制
	 * 默认值：不限制
	 */
	private String maxOpenPreparedStatements;
	
	/**
	 * 初始化连接:连接池启动时创建的初始化连接数量
	 * 默认值：0
	 */
	private String initialSize;
	
	/**
	 * 最小空闲连接:连接池中容许保持空闲状态的最小连接数量,低于这个数量将创建新的连接,如果设置为0则不创建
	 * 默认值：0
	 */
	private String minIdle;
	
	/**
	 * 控制PoolGuard是否容许获取底层连接
	 * 默认值：false
	 */
	private String accessToUnderlyingConnectionAllowed;
	
	/**
	 * 标记是否删除泄露的连接,如果他们超过了removeAbandonedTimout的限制.
	 * 如果设置为true, 连接被认为是被泄露并且可以被删除,如果空闲时间超过removeAbandonedTimeout. 
	 * 设置为true可以为写法糟糕的没有关闭连接的程序修复数据库连接.
	 * 默认值：false 
	 */
	private String removeAbandoned;
	
	/**
	 * 泄露的连接可以被删除的超时值, 单位秒
	 * 默认值：300
	 */
	private String removeAbandonedTimeout;
	
	/**
	 * 标记当Statement或连接被泄露时是否打印程序的stack traces日志。
	 * 被泄露的Statements和连接的日志添加在每个连接打开或者生成新的Statement,因为需要生成stack trace。
	 * 默认值：false
	 */
	private String logAbandoned;
	
	/**
	 * 连接池创建的连接的默认的catalog
	 */
	private String defaultCatalog;
	
	/**
	 * the value of loginTimeout.
	 */
	private String loginTimeout;
	
	/**
	 * Whether a rollback will be issued after executing the SQL query that will be 
	 * used to validate connections from this pool before returning them to the caller. 
	 * Default behavior is NOT to issue a rollback. The setting will only have an effect 
	 * if a validation query is set
	 */
	private String rollbackAfterValidation;

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

	public String getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(String defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public String getDefaultReadOnly() {
		return defaultReadOnly;
	}

	public void setDefaultReadOnly(String defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}

	public String getDefaultTransactionIsolation() {
		return defaultTransactionIsolation;
	}

	public void setDefaultTransactionIsolation(String defaultTransactionIsolation) {
		this.defaultTransactionIsolation = defaultTransactionIsolation;
	}

	public String getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(String maxActive) {
		this.maxActive = maxActive;
	}

	public String getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(String maxIdle) {
		this.maxIdle = maxIdle;
	}

	public String getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(String maxWait) {
		this.maxWait = maxWait;
	}

	public String getTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(String testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public String getTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(String testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public String getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(
			String timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public String getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(String numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public String getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(String minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public String getTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(String testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public String getPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(String poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public String getMaxOpenPreparedStatements() {
		return maxOpenPreparedStatements;
	}

	public void setMaxOpenPreparedStatements(String maxOpenPreparedStatements) {
		this.maxOpenPreparedStatements = maxOpenPreparedStatements;
	}

	public String getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(String initialSize) {
		this.initialSize = initialSize;
	}

	public String getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(String minIdle) {
		this.minIdle = minIdle;
	}

	public String getAccessToUnderlyingConnectionAllowed() {
		return accessToUnderlyingConnectionAllowed;
	}

	public void setAccessToUnderlyingConnectionAllowed(
			String accessToUnderlyingConnectionAllowed) {
		this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
	}

	public String getRemoveAbandoned() {
		return removeAbandoned;
	}

	public void setRemoveAbandoned(String removeAbandoned) {
		this.removeAbandoned = removeAbandoned;
	}

	public String getRemoveAbandonedTimeout() {
		return removeAbandonedTimeout;
	}

	public void setRemoveAbandonedTimeout(String removeAbandonedTimeout) {
		this.removeAbandonedTimeout = removeAbandonedTimeout;
	}

	public String getLogAbandoned() {
		return logAbandoned;
	}

	public void setLogAbandoned(String logAbandoned) {
		this.logAbandoned = logAbandoned;
	}

	public String getDefaultCatalog() {
		return defaultCatalog;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}

	public String getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(String loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	public String getRollbackAfterValidation() {
		return rollbackAfterValidation;
	}

	public void setRollbackAfterValidation(String rollbackAfterValidation) {
		this.rollbackAfterValidation = rollbackAfterValidation;
	}

	@Override
	public BasicPoolConfig clone(){
		BasicPoolConfig obj = null;
		try {
			obj = (BasicPoolConfig)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	public static void main(String[] args){
		BasicPoolConfig config = new BasicPoolConfig();
		config.setUsername("root");
		BasicPoolConfig clone = config.clone();
		System.out.println(clone.getUsername());
	}
}
