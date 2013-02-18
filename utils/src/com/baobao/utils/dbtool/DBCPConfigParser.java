package com.baobao.utils.dbtool;

//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Attribute;

import com.baobao.utils.ConfigUtil;
import com.baobao.utils.file.BaseFile;


/**
* 解析权限配置文件dbcpconfig.xml的类
* @author luxiangning
* @since 2009-10-19
*
*/

public class DBCPConfigParser extends BaseFile {
	//单例模式
	private static DBCPConfigParser instance = null;
	
	//公共数据库基本连接池配置
	private HashMap<String, BasicPoolConfig> SystemBasicPoolConfigMap;
	
	//公共数据库分布式连接池配置
	private HashMap<String, XAPoolConfig> SystemXAPoolConfigMap;
	
	//散列数据库基本连接池配置
	private HashMap<String, BasicPoolConfig> HashBasicPoolConfigMap;
	
	//散列数据库分布式连接池配置
	private HashMap<String, XAPoolConfig> HashXAPoolConfigMap;
	
	//基本连接池配置的公共属性
	private BasicPoolConfig CommonBasicPoolConfig = null;
	
	//分布式连接池配置的公共属性
	private XAPoolConfig CommonXAPoolConfig = null;
	
	private DBCPConfigParser(){
		this.path = ConfigUtil.DEFAULT_CONFIG_PATH + "dbcpconfig.xml";
		this.SystemBasicPoolConfigMap = new HashMap<String, BasicPoolConfig>();
		this.SystemXAPoolConfigMap = new HashMap<String, XAPoolConfig>();
		this.HashBasicPoolConfigMap = new HashMap<String, BasicPoolConfig>();
		this.HashXAPoolConfigMap = new HashMap<String, XAPoolConfig>();
	}
	
	//创建类的一个实例
	public synchronized static DBCPConfigParser getInstance(){
		if(null == instance)
			instance = new DBCPConfigParser();
		return instance;
	}
	
	/**
	 * 解析权限配置文件dbcpconfig.xml并将管理人员及其权限信息存储在DBCPConfigMap中
	 * @return true or false
	 */
	@SuppressWarnings("unchecked")
	public boolean init(){
		
		try{
			lock.readLock().lock();
			if (path == null) {
				Logger.getLogger(this.getClass()).debug("filePath is null");
				return false;
			}
			this.SystemBasicPoolConfigMap.clear();
			this.SystemXAPoolConfigMap.clear();
			this.HashBasicPoolConfigMap.clear();
			this.HashXAPoolConfigMap.clear();
			Document doc = getDocument();
			if (doc == null) {
				Logger.getLogger(this.getClass()).error("Something wrong while parsing xml!");
				return false;
			}
			Element root = doc.getRootElement();
			if (root == null)
				return false;
			Element commonPropertiesNode = root.element("commonProperties");
			Element commonBasicPoolPropertiesNode = commonPropertiesNode.element("basicPoolProperties");
			this.CommonBasicPoolConfig = getCommonBasicPoolConfig(commonBasicPoolPropertiesNode);
			Element commonXAPoolPropertiesNode = commonPropertiesNode.element("xaPoolProperties");
			this.CommonXAPoolConfig = getCommonXAPoolConfig(commonXAPoolPropertiesNode);
			List<Element> groupNodes = root.elements("Group");
			Attribute attribute;
			String groupId = "";
			String groupType = "";
			for(Element groupNode:groupNodes){
				attribute = groupNode.attribute("id");
				if(attribute!=null&&!attribute.getValue().equals("")){
					groupId = attribute.getValue();
				}
				else{
					Logger.getLogger(this.getClass()).error("Group id not exists or empty!");
					continue;
				}
				attribute = groupNode.attribute("type");
				if(attribute!=null&&!attribute.getValue().equals("")){
					groupType = attribute.getValue();
				}
				else{
					Logger.getLogger(this.getClass()).error("Group type not exists or empty!");
					continue;
				}
				List<Element> hostNodes = groupNode.elements("Host");
				List<Element> dataSourceNodes = groupNode.elements("DataSource");
				String ip = "";
				String port = "";
				String hostId = "";
				for(Element hostNode:hostNodes){
					attribute = hostNode.attribute("ip");
					if(attribute!=null&&!attribute.getValue().equals("")){
						ip = attribute.getValue();
					}
					else{
						Logger.getLogger(this.getClass()).error("Host ip not exists or empty!");
						continue;
					}
					attribute = hostNode.attribute("port");
					if(attribute!=null&&!attribute.getValue().equals("")){
						port = attribute.getValue();
					}
					else{
						Logger.getLogger(this.getClass()).error("Host port not exists or empty!");
						continue;
					}
					attribute = hostNode.attribute("id");
					if(attribute!=null&&!attribute.getValue().equals("")){
						hostId = attribute.getValue();
					}
					else{
						Logger.getLogger(this.getClass()).error("Host id not exists or empty!");
						continue;
					}
					String dbname = "";
					String username = "";
					String password = "";
					//String driverClassName = "";
					for(Element dataSourceNode:dataSourceNodes){
						BasicPoolConfig basicPoolConfig = this.CommonBasicPoolConfig.clone();
						basicPoolConfig.setGroupId(groupId);
						basicPoolConfig.setGroupType(groupType);
						basicPoolConfig.setIp(ip);
						basicPoolConfig.setPort(port);
						basicPoolConfig.setHostId(hostId);
						basicPoolConfig.setDriverClassName(CommonBasicPoolConfig.getDriverClassName());
						
						XAPoolConfig xaPoolConfig = this.CommonXAPoolConfig.clone();
						xaPoolConfig.setGroupId(groupId);
						xaPoolConfig.setGroupType(groupType);
						xaPoolConfig.setIp(ip);
						xaPoolConfig.setPort(port);
						xaPoolConfig.setHostId(hostId);
						attribute = dataSourceNode.attribute("dbname");
						if(attribute!=null&&!attribute.getValue().equals("")){
							dbname = attribute.getValue();
							basicPoolConfig.setDbname(dbname);
							xaPoolConfig.setDbname(dbname);
						}
						else{
							Logger.getLogger(this.getClass()).error("DataSource dbname not exists or empty!");
							continue;
						}
						attribute = dataSourceNode.attribute("username");
						if(attribute!=null&&!attribute.getValue().equals("")){
							username = attribute.getValue();
							basicPoolConfig.setUsername(username);
							xaPoolConfig.setUsername(username);
						}
						else{
							Logger.getLogger(this.getClass()).error("DataSource username not exists or empty!");
							continue;
						}
						attribute = dataSourceNode.attribute("password");
						if(attribute!=null/*&&!attribute.getValue().equals("")*/){//密码可以为空
							password = attribute.getValue();
							basicPoolConfig.setPassword(password);
							xaPoolConfig.setPassword(password);
						}
						else{
							Logger.getLogger(this.getClass()).error("DataSource password not exists!");
							continue;
						}
						String key = dbname + "_" + groupId + "_" + hostId;
						if(groupType.equalsIgnoreCase("system")){
							Logger.getLogger(this.getClass()).info("save system pool config:key="+key);
							this.SystemBasicPoolConfigMap.put(key, basicPoolConfig);
							this.SystemXAPoolConfigMap.put(key, xaPoolConfig);
						}
						else{
							Logger.getLogger(this.getClass()).info("save hash pool config:key="+key);
							this.HashBasicPoolConfigMap.put(key, basicPoolConfig);
							this.HashXAPoolConfigMap.put(key, xaPoolConfig);
						}
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e.getMessage());
			return false;
		} finally {
			lock.readLock().unlock();
		}
		return true;
	}
	
	/**
	 * 获得公共的基本连接池配置
	 * @param basicPoolPropertiesNode
	 * @return
	 */
	public BasicPoolConfig getCommonBasicPoolConfig(Element basicPoolPropertiesNode){
		if(basicPoolPropertiesNode==null)
			return null;
		BasicPoolConfig basicPoolConfig = new BasicPoolConfig();
		Element node = basicPoolPropertiesNode.element("driverClassName");
		if(node!=null)
			basicPoolConfig.setDriverClassName(node.getTextTrim());
		node = basicPoolPropertiesNode.element("username");
		if(node!=null)
			basicPoolConfig.setUsername(node.getTextTrim());
		node = basicPoolPropertiesNode.element("password");
		if(node!=null)
			basicPoolConfig.setPassword(node.getTextTrim());
		node = basicPoolPropertiesNode.element("ip");
		if(node!=null)
			basicPoolConfig.setIp(node.getTextTrim());
		node = basicPoolPropertiesNode.element("port");
		if(node!=null)
			basicPoolConfig.setPort(node.getTextTrim());
		node = basicPoolPropertiesNode.element("dbname");
		if(node!=null)
			basicPoolConfig.setDbname(node.getTextTrim());
		node = basicPoolPropertiesNode.element("connectionProperties");
		if(node!=null)
			basicPoolConfig.setConnectionProperties(node.getTextTrim());
		node = basicPoolPropertiesNode.element("description");
		if(node!=null)
			basicPoolConfig.setDescription(node.getTextTrim());
		node = basicPoolPropertiesNode.element("defaultAutoCommit");
		if(node!=null)
			basicPoolConfig.setDefaultAutoCommit(node.getTextTrim());
		node = basicPoolPropertiesNode.element("defaultReadOnly");
		if(node!=null)
			basicPoolConfig.setDefaultReadOnly(node.getTextTrim());
		node = basicPoolPropertiesNode.element("defaultTransactionIsolation");
		if(node!=null)
			basicPoolConfig.setDefaultTransactionIsolation(node.getTextTrim());
		node = basicPoolPropertiesNode.element("maxActive");
		if(node!=null)
			basicPoolConfig.setMaxActive(node.getTextTrim());
		node = basicPoolPropertiesNode.element("maxIdle");
		if(node!=null)
			basicPoolConfig.setMaxIdle(node.getTextTrim());
		node = basicPoolPropertiesNode.element("maxWait");
		if(node!=null)
			basicPoolConfig.setMaxWait(node.getTextTrim());
		node = basicPoolPropertiesNode.element("testOnBorrow");
		if(node!=null)
			basicPoolConfig.setTestOnBorrow(node.getTextTrim());
		node = basicPoolPropertiesNode.element("testOnReturn");
		if(node!=null)
			basicPoolConfig.setTestOnReturn(node.getTextTrim());
		node = basicPoolPropertiesNode.element("timeBetweenEvictionRunsMillis");
		if(node!=null)
			basicPoolConfig.setTimeBetweenEvictionRunsMillis(node.getTextTrim());
		node = basicPoolPropertiesNode.element("numTestsPerEvictionRun");
		if(node!=null)
			basicPoolConfig.setNumTestsPerEvictionRun(node.getTextTrim());
		node = basicPoolPropertiesNode.element("minEvictableIdleTimeMillis");
		if(node!=null)
			basicPoolConfig.setMinEvictableIdleTimeMillis(node.getTextTrim());
		node = basicPoolPropertiesNode.element("testWhileIdle");
		if(node!=null)
			basicPoolConfig.setTestWhileIdle(node.getTextTrim());
		node = basicPoolPropertiesNode.element("validationQuery");
		if(node!=null)
			basicPoolConfig.setValidationQuery(node.getTextTrim());
		node = basicPoolPropertiesNode.element("poolPreparedStatements");
		if(node!=null)
			basicPoolConfig.setPoolPreparedStatements(node.getTextTrim());
		node = basicPoolPropertiesNode.element("maxOpenPreparedStatements");
		if(node!=null)
			basicPoolConfig.setMaxOpenPreparedStatements(node.getTextTrim());
		node = basicPoolPropertiesNode.element("initialSize");
		if(node!=null)
			basicPoolConfig.setInitialSize(node.getTextTrim());
		node = basicPoolPropertiesNode.element("minIdle");
		if(node!=null)
			basicPoolConfig.setMinIdle(node.getTextTrim());
		node = basicPoolPropertiesNode.element("accessToUnderlyingConnectionAllowed");
		if(node!=null)
			basicPoolConfig.setAccessToUnderlyingConnectionAllowed(node.getTextTrim());
		node = basicPoolPropertiesNode.element("removeAbandoned");
		if(node!=null)
			basicPoolConfig.setRemoveAbandoned(node.getTextTrim());
		node = basicPoolPropertiesNode.element("removeAbandonedTimeout");
		if(node!=null)
			basicPoolConfig.setRemoveAbandonedTimeout(node.getTextTrim());
		node = basicPoolPropertiesNode.element("logAbandoned");
		if(node!=null)
			basicPoolConfig.setLogAbandoned(node.getTextTrim());
		node = basicPoolPropertiesNode.element("defaultCatalog");
		if(node!=null)
			basicPoolConfig.setDefaultCatalog(node.getTextTrim());
		node = basicPoolPropertiesNode.element("loginTimeout");
		if(node!=null)
			basicPoolConfig.setLoginTimeout(node.getTextTrim());
		node = basicPoolPropertiesNode.element("rollbackAfterValidation");
		if(node!=null)
			basicPoolConfig.setRollbackAfterValidation(node.getTextTrim());
		return basicPoolConfig;
	}
	
	/**
	 * 获得公共的分布式连接池配置
	 * @param basicPoolPropertiesNode
	 * @return
	 */
	public XAPoolConfig getCommonXAPoolConfig(Element xaPoolPropertiesNode){
		if(xaPoolPropertiesNode==null)
			return null;
		XAPoolConfig xaPoolConfig = new XAPoolConfig();
		Element node = xaPoolPropertiesNode.element("driverClassName");
		if(node!=null)
			xaPoolConfig.setDriverClassName(node.getTextTrim());
		node = xaPoolPropertiesNode.element("username");
		if(node!=null)
			xaPoolConfig.setUsername(node.getTextTrim());
		node = xaPoolPropertiesNode.element("password");
		if(node!=null)
			xaPoolConfig.setPassword(node.getTextTrim());
		node = xaPoolPropertiesNode.element("ip");
		if(node!=null)
			xaPoolConfig.setIp(node.getTextTrim());
		node = xaPoolPropertiesNode.element("port");
		if(node!=null)
			xaPoolConfig.setPort(node.getTextTrim());
		node = xaPoolPropertiesNode.element("dbname");
		if(node!=null)
			xaPoolConfig.setDbname(node.getTextTrim());
		node = xaPoolPropertiesNode.element("connectionProperties");
		if(node!=null)
			xaPoolConfig.setConnectionProperties(node.getTextTrim());
		node = xaPoolPropertiesNode.element("description");
		if(node!=null)
			xaPoolConfig.setDescription(node.getTextTrim());
		node = xaPoolPropertiesNode.element("sleepTime");
		if(node!=null)
			xaPoolConfig.setSleepTime(node.getTextTrim());
		node = xaPoolPropertiesNode.element("lifeTime");
		if(node!=null)
			xaPoolConfig.setLifeTime(node.getTextTrim());
		node = xaPoolPropertiesNode.element("loginTimeout");
		if(node!=null)
			xaPoolConfig.setLoginTimeout(node.getTextTrim());
		node = xaPoolPropertiesNode.element("deacLockMaxWait");
		if(node!=null)
			xaPoolConfig.setDeacLockMaxWait(node.getTextTrim());
		node = xaPoolPropertiesNode.element("deacLockRetryWait");
		if(node!=null)
			xaPoolConfig.setDeacLockRetryWait(node.getTextTrim());
		node = xaPoolPropertiesNode.element("maxSize");
		if(node!=null)
			xaPoolConfig.setMaxSize(node.getTextTrim());
		node = xaPoolPropertiesNode.element("minSize");
		if(node!=null)
			xaPoolConfig.setMinSize(node.getTextTrim());
		node = xaPoolPropertiesNode.element("jdbcTestStmt");
		if(node!=null)
			xaPoolConfig.setJdbcTestStmt(node.getTextTrim());
		node = xaPoolPropertiesNode.element("transactionIsolation");
		if(node!=null)
			xaPoolConfig.setTransactionIsolation(node.getTextTrim());
		node = xaPoolPropertiesNode.element("preparedStmtCacheSize");
		if(node!=null)
			xaPoolConfig.setPreparedStmtCacheSize(node.getTextTrim());
		node = xaPoolPropertiesNode.element("gc");
		if(node!=null)
			xaPoolConfig.setGc(node.getTextTrim());
		node = xaPoolPropertiesNode.element("debug");
		if(node!=null)
			xaPoolConfig.setDebug(node.getTextTrim());
		node = xaPoolPropertiesNode.element("verbose");
		if(node!=null)
			xaPoolConfig.setVerbose(node.getTextTrim());
		return xaPoolConfig;
	}
	
	/**
	 * 将DBCPConfigMap中存储的管理人员及其权限信息写回到权限配置文件dbcpconfig.xml中
	 * @return true or false
	 */
	public boolean storeData(){
		try {
			lock.writeLock().lock();
			if (!dirty)
				return true;
			Document doc = DocumentHelper.createDocument();
			//to do 
			// 将文件写入
			if (this.writeDocument(doc)) {
				this.dirty = false;
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e.getMessage());
			return false;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * 获得SystemBasicPoolConfigMap
	 */
	public HashMap<String, BasicPoolConfig> getSystemBasicPoolConfigMap(){
		return SystemBasicPoolConfigMap;
	}
	
	/**
	 * 获得SystemXAPoolConfigMap
	 */
	public HashMap<String, XAPoolConfig> getSystemXAPoolConfigMap(){
		return SystemXAPoolConfigMap;
	}
	
	/**
	 * 获得SystemBasicPoolConfigMap
	 */
	public HashMap<String, BasicPoolConfig> getHashBasicPoolConfigMap(){
		return HashBasicPoolConfigMap;
	}
	
	/**
	 * 获得SystemBasicPoolConfigMap
	 */
	public HashMap<String, XAPoolConfig> getHashXAPoolConfigMap(){
		return HashXAPoolConfigMap;
	}
	
	/**
	 * 获得基本连接池配置的公共属性
	 */
	public BasicPoolConfig getCommonBasicPoolConfig(){
		return this.CommonBasicPoolConfig;
	}
	
	/**
	 * 获得分布式连接池配置的公共属性
	 */
	public XAPoolConfig getCommonXAPoolConfig(){
		return this.CommonXAPoolConfig;
	}
	
}
