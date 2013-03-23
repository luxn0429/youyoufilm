package com.baobao.utils.dbtool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource; 

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.apache.log4j.Logger;

import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;

import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;

import com.baobao.utils.dbtool.hash.ICustomNode;

/**
 * 数据库连接池管理类
 * @author 江铁扣
 * @since 2009.09.27
 *
 */
public class DBCPManager {
	
	//保存公共数据库基本数据源
	private static HashMap<String,DataSource> SystemBasicDataSourceMap = new HashMap<String,DataSource>();
	
	//保存公共数据库分布式数据源
	private static HashMap<String,DataSource> SystemXADataSourceMap = new HashMap<String,DataSource>();
	
	//保存散列数据库基本数据源
	private static HashMap<String,DataSource> HashBasicDataSourceMap = new HashMap<String,DataSource>();
	
	//保存散列数据库分布式数据源
	private static HashMap<String,DataSource> HashXADataSourceMap = new HashMap<String,DataSource>();
	
	//保存数据库名字与所属群组id的对应关系
	private static HashMap<String,String> db2GroupMap = new HashMap<String,String>();
	
	//保存群组id与下属主机id的对应关系
	private static HashMap<String,Set<String>> group2HostsMap = new HashMap<String, Set<String>>();
	
	//公共库名字
	private static String publicDBName;
	
	//一致性Hash节点列表
	private static List<ICustomNode> nodeList = new ArrayList<ICustomNode>();
	
	private static TMService jotm;
	
	static {
        try {
            jotm = new Jotm(true, false);
        } catch (NamingException e) {
            e.printStackTrace();
            Logger.getLogger(DBCPManager.class).error("init JOTM failed: " + e.getMessage());
        }
    }
	
	private DBCPManager() { 
		init();
	} 
	private static final DBCPManager instance = new DBCPManager(); 
	
	public static DBCPManager getInstance() { 
		return instance; 
	}
	
	

	
	/**
	 * 根据配置文件生成数据库连接池，并放入basicDataSourceMap中
	 */
	@SuppressWarnings("unchecked")
	public boolean init(){
		BasicPoolConfig basicPoolConfig = null;
		XAPoolConfig xaPoolConfig = null;
		DBCPConfigParser dbcpConfigParser = DBCPConfigParser.getInstance();
		boolean flag = dbcpConfigParser.init();
		if(flag){
			Iterator it = dbcpConfigParser.getSystemBasicPoolConfigMap().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, BasicPoolConfig> entry = (Map.Entry<String, BasicPoolConfig>)it.next();
				basicPoolConfig = entry.getValue();
				DataSource ds = createBasicDataSource(basicPoolConfig);
				if(ds!=null){
					SystemBasicDataSourceMap.put(entry.getKey(), ds);
					saveRelationShip(basicPoolConfig);
					publicDBName = basicPoolConfig.getDbname();//考虑到目前在配置文件中公共库只有一个节点，所以其对应的数据库名字即为公共库名
				}
				else{
					Logger.getLogger(this.getClass()).error("create basic datasource failed: id="+entry.getKey());
				}
			}
			it = dbcpConfigParser.getSystemXAPoolConfigMap().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, XAPoolConfig> entry = (Map.Entry<String, XAPoolConfig>)it.next();
				xaPoolConfig = entry.getValue();
				DataSource ds = createXADataSource(xaPoolConfig);
				if(ds!=null){
					SystemXADataSourceMap.put(entry.getKey(), ds);
				}
				else{
					Logger.getLogger(this.getClass()).error("create xa datasource failed: id="+entry.getKey());
				}
			}
			it = dbcpConfigParser.getHashBasicPoolConfigMap().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, BasicPoolConfig> entry = (Map.Entry<String, BasicPoolConfig>)it.next();
				basicPoolConfig = entry.getValue();
				DataSource ds = createBasicDataSource(basicPoolConfig);
				if(ds!=null){
					HashBasicDataSourceMap.put(entry.getKey(), ds);
					saveRelationShip(basicPoolConfig);
				}
				else{
					Logger.getLogger(this.getClass()).error("create basic datasource failed: id="+entry.getKey());
				}
			}
			it = dbcpConfigParser.getHashXAPoolConfigMap().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, XAPoolConfig> entry = (Map.Entry<String, XAPoolConfig>)it.next();
				xaPoolConfig = entry.getValue();
				DataSource ds = createXADataSource(xaPoolConfig);
				if(ds!=null){
					HashXADataSourceMap.put(entry.getKey(), ds);
				}
				else{
					Logger.getLogger(this.getClass()).error("create xa datasource failed: id="+entry.getKey());
				}
			}
			for(String dbName:db2GroupMap.keySet()) {
				if(!dbName.equals(publicDBName)) {
					ICustomNode node = new DBNode(dbName);
					nodeList.add(node);
					Logger.getLogger(this.getClass()).info("consistent hash node=:"+node);
				}
			}
			Logger.getLogger(this.getClass()).info("SystemBasicDataSource numbers:"+SystemBasicDataSourceMap.size());
			Logger.getLogger(this.getClass()).info("SystemXADataSource numbers:"+SystemXADataSourceMap.size());
			Logger.getLogger(this.getClass()).info("HashBasicDataSource numbers:"+HashBasicDataSourceMap.size());
			Logger.getLogger(this.getClass()).info("HashXADataSource numbers:"+HashXADataSourceMap.size());
			return true;
		}
		else{
			Logger.getLogger(this.getClass()).error("initialize datasources failed");
			return false;
		}
	}
	
	/**
     * 根据连接池配置,构造基本类型数据库连接池
     */
    public DataSource createBasicDataSource(BasicPoolConfig basicPoolConfig) {
    	SharedPoolDataSource spds = null;
    	try{
    		DriverAdapterCPDS cpds = new DriverAdapterCPDS();   
    		if(!basicPoolConfig.getDriverClassName().trim().equals("")){
    			cpds.setDriver(basicPoolConfig.getDriverClassName());  
    		}
    		else{
    			Logger.getLogger(this.getClass()).error("createBasicDatasource failed: DriverClassName cann't be empty!");
    			return null;
    		}
    		if(basicPoolConfig.getIp().trim().equals("")){
    			Logger.getLogger(this.getClass()).error("createBasicDatasource failed: ip cann't be empty!");
    			return null;
    		}
    		if(basicPoolConfig.getPort().trim().equals("")){
    			Logger.getLogger(this.getClass()).error("createBasicDatasource failed: port cann't be empty!");
    			return null;
    		}
    		if(basicPoolConfig.getDbname().trim().equals("")){
    			Logger.getLogger(this.getClass()).error("createBasicDatasource failed: dbname cann't be empty!");
    			return null;
    		}
			String url;
			if(!basicPoolConfig.getConnectionProperties().trim().equals("")){
				url = "jdbc:mysql://"+basicPoolConfig.getIp()+":"+basicPoolConfig.getPort()+"/"+
						basicPoolConfig.getDbname()+"?"+basicPoolConfig.getConnectionProperties();
			}
			else{
				url = "jdbc:mysql://"+basicPoolConfig.getIp()+":"+basicPoolConfig.getPort()+"/"+
						basicPoolConfig.getDbname();
			}
			cpds.setUrl(url);   
			if(!basicPoolConfig.getUsername().trim().equals("")){
				cpds.setUser(basicPoolConfig.getUsername());
    		}else{
    			Logger.getLogger(this.getClass()).error("createBasicDatasource failed: username cann't be empty!");
    			return null;
    		}
			cpds.setPassword(basicPoolConfig.getPassword());
			if(!basicPoolConfig.getDescription().trim().equals(""))
				cpds.setDescription(basicPoolConfig.getDescription());
			if(!basicPoolConfig.getLoginTimeout().trim().equals(""))
				cpds.setLoginTimeout(Integer.parseInt(basicPoolConfig.getLoginTimeout()));
			if(!basicPoolConfig.getMaxActive().trim().equals(""))
				cpds.setMaxActive(Integer.parseInt(basicPoolConfig.getMaxActive()));
			if(!basicPoolConfig.getMaxIdle().trim().equals(""))
				cpds.setMaxIdle(Integer.parseInt(basicPoolConfig.getMaxIdle()));
			if(!basicPoolConfig.getMaxOpenPreparedStatements().trim().equals(""))
				cpds.setMaxPreparedStatements(Integer.parseInt(basicPoolConfig.getMaxOpenPreparedStatements()));
			if(!basicPoolConfig.getMinEvictableIdleTimeMillis().trim().equals(""))
				cpds.setMinEvictableIdleTimeMillis(Integer.parseInt(basicPoolConfig.getMinEvictableIdleTimeMillis()));
			if(!basicPoolConfig.getNumTestsPerEvictionRun().trim().equals(""))
				cpds.setNumTestsPerEvictionRun(Integer.parseInt(basicPoolConfig.getNumTestsPerEvictionRun()));
			if(!basicPoolConfig.getPoolPreparedStatements().trim().equals(""))
				cpds.setPoolPreparedStatements(basicPoolConfig.getPoolPreparedStatements().equals("true")?true:false);
			if(!basicPoolConfig.getTimeBetweenEvictionRunsMillis().trim().equals(""))
				cpds.setTimeBetweenEvictionRunsMillis(Integer.parseInt(basicPoolConfig.getTimeBetweenEvictionRunsMillis()));
			
			spds = new SharedPoolDataSource();
	        spds.setConnectionPoolDataSource(cpds);   
	        if(!basicPoolConfig.getDefaultAutoCommit().trim().equals(""))
	        	spds.setDefaultAutoCommit(basicPoolConfig.getDefaultAutoCommit().equals("true")?true:false);
	        if(!basicPoolConfig.getDefaultTransactionIsolation().equals(""))
	        	spds.setDefaultTransactionIsolation(Integer.parseInt(basicPoolConfig.getDefaultTransactionIsolation()));
	        if(!basicPoolConfig.getDescription().trim().equals(""))	
	        	spds.setDescription(basicPoolConfig.getDescription());
	        if(!basicPoolConfig.getLoginTimeout().trim().equals(""))
	        	spds.setLoginTimeout(Integer.parseInt(basicPoolConfig.getLoginTimeout()));
	        if(!basicPoolConfig.getMaxActive().trim().equals(""))
	        	spds.setMaxActive(Integer.parseInt(basicPoolConfig.getMaxActive()));
	        if(!basicPoolConfig.getMaxIdle().trim().equals(""))
	        	spds.setMaxIdle(Integer.parseInt(basicPoolConfig.getMaxIdle()));
	        if(!basicPoolConfig.getMaxWait().trim().equals(""))
	        	spds.setMaxWait(Integer.parseInt(basicPoolConfig.getMaxWait()));
	        if(!basicPoolConfig.getMinEvictableIdleTimeMillis().trim().equals(""))
	        	spds.setMinEvictableIdleTimeMillis(Integer.parseInt(basicPoolConfig.getMinEvictableIdleTimeMillis()));
	        if(!basicPoolConfig.getNumTestsPerEvictionRun().trim().equals(""))	
	        	spds.setNumTestsPerEvictionRun(Integer.parseInt(basicPoolConfig.getNumTestsPerEvictionRun()));
	        if(!basicPoolConfig.getRollbackAfterValidation().trim().equals(""))
	        	spds.setRollbackAfterValidation(basicPoolConfig.getRollbackAfterValidation().equals("true")?true:false);
	        if(!basicPoolConfig.getTestOnBorrow().trim().equals(""))
	        	spds.setTestOnBorrow(basicPoolConfig.getTestOnBorrow().equals("true")?true:false);
	        if(!basicPoolConfig.getTestOnReturn().trim().equals(""))
	        	spds.setTestOnReturn(basicPoolConfig.getTestOnReturn().equals("true")?true:false);
	        if(!basicPoolConfig.getTestWhileIdle().trim().equals(""))
	        	spds.setTestWhileIdle(basicPoolConfig.getTestWhileIdle().equals("true")?true:false);
	        if(!basicPoolConfig.getTimeBetweenEvictionRunsMillis().trim().equals(""))
	        	spds.setTimeBetweenEvictionRunsMillis(Integer.parseInt(basicPoolConfig.getTimeBetweenEvictionRunsMillis()));
	        if(!basicPoolConfig.getValidationQuery().trim().equals(""))
	        	spds.setValidationQuery(basicPoolConfig.getValidationQuery());
    	}catch(Exception e){
    		e.printStackTrace();
			Logger.getLogger(this.getClass()).error("createBasicDataSource failed:id="+basicPoolConfig.getDbname()+"_"+
					basicPoolConfig.getGroupId()+"_"+basicPoolConfig.getHostId()+";"+e.getMessage());
    	}
        return spds;   
    }
    
    /**
     * 根据连接池配置,构造分布式数据库连接池(参考org.objectweb.jotm.datasource.DataSourceFactory)
     */
    public DataSource createXADataSource(XAPoolConfig xaPoolConfig) {  
    	StandardXAPoolDataSource xads = null;
        StandardXADataSource ds = null;
        try {
            ds = new StandardXADataSource();
            xads = new StandardXAPoolDataSource(ds);
            if(!xaPoolConfig.getDriverClassName().trim().equals("")){
            	ds.setDriverName(xaPoolConfig.getDriverClassName());  
    		}
    		else{
    			Logger.getLogger(this.getClass()).error("createXADataSource failed: DriverClassName cann't be empty!");
    			return null;
    		}
    		if(xaPoolConfig.getIp().trim().equals("")){
    			Logger.getLogger(this.getClass()).error("createXADataSource failed: ip cann't be empty!");
    			return null;
    		}
    		if(xaPoolConfig.getPort().trim().equals("")){
    			Logger.getLogger(this.getClass()).error("createXADataSource failed: port cann't be empty!");
    			return null;
    		}
    		if(xaPoolConfig.getDbname().trim().equals("")){
    			Logger.getLogger(this.getClass()).error("createXADataSource failed: dbname cann't be empty!");
    			return null;
    		}
            String url;
			if(!xaPoolConfig.getConnectionProperties().trim().equals("")){
				url = "jdbc:mysql://"+xaPoolConfig.getIp()+":"+xaPoolConfig.getPort()+"/"+
						xaPoolConfig.getDbname()+"?"+xaPoolConfig.getConnectionProperties();
			}
			else{
				url = "jdbc:mysql://"+xaPoolConfig.getIp()+":"+xaPoolConfig.getPort()+"/"+
						xaPoolConfig.getDbname();
			}
            ds.setUrl(url);
            if(!xaPoolConfig.getUsername().trim().equals("")){
            	ds.setUser(xaPoolConfig.getUsername());
            	xads.setUser(xaPoolConfig.getUsername());
            }
            else{
            	Logger.getLogger(this.getClass()).error("createXADataSource failed: username cann't be empty!");
    			return null;
            }
            ds.setPassword(xaPoolConfig.getPassword());
            xads.setPassword(xaPoolConfig.getPassword());
            if(!xaPoolConfig.getTransactionIsolation().trim().equals("")){
            	ds.setTransactionIsolation(Integer.parseInt(xaPoolConfig.getTransactionIsolation()));
            }
            if(!xaPoolConfig.getPreparedStmtCacheSize().trim().equals("")){
            	ds.setPreparedStmtCacheSize(Integer.parseInt(xaPoolConfig.getPreparedStmtCacheSize()));
            }
            if(!xaPoolConfig.getDeacLockMaxWait().trim().equals("")){
            	ds.setDeadLockMaxWait(Long.parseLong(xaPoolConfig.getDeacLockMaxWait()));
            	xads.setDeadLockMaxWait(Long.parseLong(xaPoolConfig.getDeacLockMaxWait()));
            }
            if(!xaPoolConfig.getDeacLockRetryWait().trim().equals("")){
            	ds.setDeadLockRetryWait(Long.parseLong(xaPoolConfig.getDeacLockRetryWait()));
            	xads.setDeadLockRetryWait(Long.parseLong(xaPoolConfig.getDeacLockRetryWait()));
            }
            if(!xaPoolConfig.getDebug().trim().equals("")){
            	ds.setDebug(xaPoolConfig.getDebug().equals("true")?true:false);
            	xads.setDebug(xaPoolConfig.getDebug().equals("true")?true:false);
            }
            if(!xaPoolConfig.getDescription().trim().equals("")){
            	ds.setDescription(xaPoolConfig.getDescription());
            	xads.setDescription(xaPoolConfig.getDescription());
            }
            if(!xaPoolConfig.getGc().trim().equals("")){
            	xads.setGC(xaPoolConfig.getGc().equals("true")?true:false);
            }
            if(!xaPoolConfig.getJdbcTestStmt().trim().equals("")){
            	xads.setJdbcTestStmt(xaPoolConfig.getJdbcTestStmt());
            }
            if(!xaPoolConfig.getLifeTime().trim().equals("")){
            	xads.setLifeTime(Long.parseLong(xaPoolConfig.getLifeTime()));
            }
            if(!xaPoolConfig.getLoginTimeout().trim().equals("")){
            	ds.setLoginTimeout(Integer.parseInt(xaPoolConfig.getLoginTimeout()));
            	xads.setLoginTimeout(Integer.parseInt(xaPoolConfig.getLoginTimeout()));
            }
            if(!xaPoolConfig.getMaxSize().trim().equals("")){
            	ds.setMaxCon(Integer.parseInt(xaPoolConfig.getMaxSize()));
            	xads.setMaxSize(Integer.parseInt(xaPoolConfig.getMaxSize()));
            }
            if(!xaPoolConfig.getMinSize().trim().equals("")){
            	ds.setMinCon(Integer.parseInt(xaPoolConfig.getMinSize()));
            	xads.setMinSize(Integer.parseInt(xaPoolConfig.getMinSize()));
            }
            if(!xaPoolConfig.getSleepTime().trim().equals("")){
            	xads.setSleepTime(Long.parseLong(xaPoolConfig.getSleepTime()));
            }
            if(!xaPoolConfig.getVerbose().trim().equals("")){
            	ds.setVerbose(xaPoolConfig.getVerbose().equals("true")?true:false);
            	xads.setVerbose(xaPoolConfig.getVerbose().equals("true")?true:false);
            }
            xads.setTransactionManager(jotm.getTransactionManager());
            xads.setDataSource(ds);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass()).error("createXADataSource failed:id="+xaPoolConfig.getDbname()+"_"+
            		xaPoolConfig.getGroupId()+"_"+xaPoolConfig.getHostId()+";"+e.getMessage());
        }
        return xads;
    }
	
    /**
     * 获得公共数据库基本连接
     * @param key
     * @return
     */
    public synchronized Connection getSystemBasicConnection(String key) throws SQLException {
    	DataSource ds = null;
    	Connection connection = null;
		ds = SystemBasicDataSourceMap.get(key);
		if(ds != null)
			connection = SystemBasicDataSourceMap.get(key).getConnection();
		return connection;
    }
    
    /**
     * 获得公共数据库分布式连接
     * @param key
     * @return
     */
    public synchronized Connection getSystemXAConnection(String key) throws SQLException {
    	DataSource ds = null;
    	Connection connection = null;
		ds = SystemXADataSourceMap.get(key);
		if(ds != null)
			connection = SystemXADataSourceMap.get(key).getConnection();
		return connection;
    }
    
    /**
     * 获得散列数据库基本连接
     * @param key
     * @return
     */
    public synchronized Connection getHashBasicConnection(String key) throws SQLException {
    	DataSource ds = null;
    	Connection connection = null;
		ds = HashBasicDataSourceMap.get(key);
		if(ds!=null){
			connection = HashBasicDataSourceMap.get(key).getConnection();
		}
		return connection;
    }
    
    /**
     * 获得散列数据库分布式连接
     * @param key
     * @return
     */
    public synchronized Connection getHashXAConnection(String key) throws SQLException {
    	DataSource ds = null;
    	Connection connection = null;
		ds = HashXADataSourceMap.get(key);
		if(ds != null)
			connection = HashXADataSourceMap.get(key).getConnection();
		else {
			Logger.getLogger(this.getClass()).error("datasource is null: key=" + key);
		}
		return connection;
    }
    
    /**
     * 获得数据库连接
     * @param id 传入的id
     * @param type 数据库连接的类型
     * @return
     */
    public synchronized Connection getConnection(String dbname, int type) throws SQLException {
    	///Logger.getLogger(this.getClass()).info("try to get a connection:dbname="+dbname+";type="+type);
    	if(type<0||type>7){
    		Logger.getLogger(this.getClass()).error("getConnection("+dbname+","+type+") failed: type is invalid");
    		return null;
    	}
    	Connection connection = null;
    	String key;
    	//假定公共库名为public,假定公共库所属组的id都为"1"
    	//假定只有一主一从两个数据库服务器，主数据库的id=0，从数据库的id=1
    	switch(type){
    		case DBConstants.PUBLIC_QUERY_BASIC:
    			if(getRandom() == 0)
    				key = dbname + "_1_0";
    			else
    				key = dbname + "_1_1";
    			connection = getSystemBasicConnection(key);
    			break;
    		case DBConstants.PUBLIC_QUERY_XA:
    			if(getRandom() == 0)
    				key = dbname + "_1_0";
    			else
    				key = dbname + "_1_1";
    			connection = getSystemXAConnection(key);
    			break;
			case DBConstants.PUBLIC_UPDATE_BASIC:
				key = dbname + "_1_0";
				connection = getSystemBasicConnection(key);	
				break;
			case DBConstants.PUBLIC_UPDATE_XA:
				key = dbname + "_1_0";
				connection = getSystemXAConnection(key);
				break;
			case DBConstants.HASH_QUERY_BASIC:
				if(getRandom() == 0)
					key = dbname + "_" + db2GroupMap.get(dbname) + "_0";
				else
					key = dbname + "_" + db2GroupMap.get(dbname) + "_1";
				connection = getHashBasicConnection(key);
				break;
			case DBConstants.HASH_QUERY_XA:
				if(getRandom() == 0)
					key = dbname + "_" + db2GroupMap.get(dbname) + "_0";
				else
					key = dbname + "_" + db2GroupMap.get(dbname) + "_1";
				connection = getHashXAConnection(key);
				break;
			case DBConstants.HASH_UPDATE_BASIC:
				key = dbname + "_" + db2GroupMap.get(dbname) + "_0";
				connection = getHashBasicConnection(key);
				break;
			case DBConstants.HASH_UPDATE_XA:
				key = dbname + "_" + db2GroupMap.get(dbname) + "_0";
				connection = getHashXAConnection(key);
				break;
			default:
				break;
    	}
    	return connection;
    }
    
    /**
     * 随机生成0和1，各百分之五十
     * @return
     */
    public int getRandom() {
    	return Math.random()>0.5?1:0;
    }
    
    /**
     * 将数据库连接放回到连接池中
     * @param connection
     */
    public void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
        	try{
        		connection.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	connection = null;
        }
    }
    
    /**
     * 将数据库连接放回到连接池中
     * @param connection
     */
    public void closeConnection(Connection connection, PreparedStatement stmt) throws SQLException {
    	if (stmt != null) {
			try{
				stmt.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	stmt = null;
		}
    	if (connection != null) {
        	try{
        		connection.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	connection = null;
        }
    }
    
    public void closeConnection(Connection conn, PreparedStatement stmt,ResultSet rs) throws SQLException {
		if (rs != null) {
			try{
				rs.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	rs = null;
		}
		if (stmt != null) {
			try{
				stmt.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	stmt = null;
		}
		if (conn != null) {
			try{
				conn.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	conn = null;
		}
	}

    public void closePreparedStatement(PreparedStatement stmt) throws SQLException {
		if (stmt != null) {
			try{
				stmt.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	stmt = null;
		}
		stmt = null;
	}

    public void closeStatement(Statement stmt) throws SQLException {
		if (stmt != null) {
			try{
				stmt.close();
        	} catch(SQLException e) {
        		e.printStackTrace(System.out);
        	}
        	stmt = null;
		}
	}

    
    /**
     * 保存数据库名字与所属群组id的对应关系以及群组id与下属主机id的对应关系
     * @param basicPoolConfig
     */
    public void saveRelationShip(BasicPoolConfig basicPoolConfig){
    	if(basicPoolConfig!=null){
	    	db2GroupMap.put(basicPoolConfig.getDbname(), basicPoolConfig.getGroupId());
			if(group2HostsMap.containsKey(basicPoolConfig.getGroupId())){
				group2HostsMap.get(basicPoolConfig.getGroupId()).add(basicPoolConfig.getHostId());
			}
			else{
				Set<String> hostIdSet = new HashSet<String>();
				hostIdSet.add(basicPoolConfig.getHostId());
				group2HostsMap.put(basicPoolConfig.getGroupId(), hostIdSet);
			}
    	}
    }
    
    /**
	 * 获得SystemBasicDataSourceMap
	 */
	public HashMap<String, DataSource> getSystemBasicDataSourceMap(){
		return SystemBasicDataSourceMap;
	}
	
	/**
	 * 获得SystemXADataSourceMap
	 */
	public HashMap<String, DataSource> getSystemXADataSourceMap(){
		return SystemXADataSourceMap;
	}
	
	/**
	 * 获得HashBasicDataSourceMap
	 */
	public HashMap<String, DataSource> getHashBasicDataSourceMap(){
		return HashBasicDataSourceMap;
	}
	
	/**
	 * 获得HashXADataSourceMap
	 */
	public HashMap<String, DataSource> getHashXADataSourceMap(){
		return HashXADataSourceMap;
	}
	
	/**
	 * 获得db2GroupMap
	 */
	public HashMap<String, String> getDB2GroupMap(){
		return db2GroupMap;
	}
	
	/**
	 * 获得group2HostsMap
	 */
	public HashMap<String, Set<String>> getGroup2HostsMap(){
		return group2HostsMap;
	}
	
	/**
	 * 获得publicDBName
	 */
	public String getPublicDBName() {
		return publicDBName;
	}
	
	/**
	 * 获得一致性Hash节点列表
	 */
	public List<ICustomNode> getNodeList() {
		return nodeList;
	}
	
	public void stop() {
		/*try {
			InitialContext ictx = new InitialContext();
			ictx.unbind(USER_TRANSACTION_JNDI_NAME);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
			Logger.getLogger(this.getClass()).error(e);
		}*/
		if(jotm != null)
			jotm.stop();
		jotm = null;
	}
	
}
