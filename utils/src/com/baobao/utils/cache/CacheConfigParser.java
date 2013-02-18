package com.baobao.utils.cache;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Attribute;

import com.baobao.utils.file.BaseFile;

public class CacheConfigParser extends BaseFile {

	//单例模式
	private static CacheConfigParser instance = null;
	
	//系统初始化时待生成的缓存配置
	private HashMap<String, CacheConfig> cacheConfigMap;
	
	private CacheConfigParser(){
		String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	    path = path.substring(0,path.indexOf("/WEB-INF/")+8);
//		path = ConfigUtil.defaultTomcatCommonConfigPath();
		this.path = path + "/cacheconfig.xml";
		this.cacheConfigMap = new HashMap<String, CacheConfig>();
	}
	
	//创建类的一个实例
	public synchronized static CacheConfigParser getInstance(){
		if(null == instance)
			instance = new CacheConfigParser();
		return instance;
	}
	
	/**
	 * 解析权限配置文件dbcpconfig.xml并将管理人员及其权限信息存储在DBCPConfigMap中
	 * @return true or false
	 */
	public boolean init(){
		
		try{
			lock.readLock().lock();
			if (path == null) {
				Logger.getLogger(this.getClass()).debug("filePath is null");
				return false;
			}
			Document doc = getDocument();
			if (doc == null) {
				Logger.getLogger(this.getClass()).error("Something wrong while parsing xml!");
				return false;
			}
			Element root = doc.getRootElement();
			if (root == null)
				return false;
			List<Element> cacheNodes = root.elements("cache");
			if(cacheNodes == null || cacheNodes.size() ==0)
				return false;
			Element node;
			Attribute attribute;
			for(Element cacheNode:cacheNodes) {
				CacheConfig cacheConfig = new CacheConfig();
				attribute = cacheNode.attribute("name");
				if(attribute != null && attribute.getValue().trim().length() != 0) {
					cacheConfig.setName(attribute.getValue().trim());
				}
				else {
					Logger.getLogger(this.getClass()).error("cache name can not be null or empty!");
					continue;
				}
				attribute = cacheNode.attribute("type");
				if(attribute != null && attribute.getValue().trim().length() != 0) {
					cacheConfig.setType(attribute.getValue().trim());
				}
				else {
					Logger.getLogger(this.getClass()).error("cache type can not be null or empty!");
					continue;
				}
				attribute = cacheNode.attribute("capacity");
				if(attribute != null && attribute.getValue().trim().length() != 0) {
					cacheConfig.setCapacity(attribute.getValue().trim());
				}
				else {
					cacheConfig.setCapacity("");
				}
				node = cacheNode.element("cache-class");
				if(node != null && !node.getTextTrim().equals("")) {
					cacheConfig.setClassName(node.getTextTrim());
				}
				else {
					Logger.getLogger(this.getClass()).error("cache class name can not be null or empty!");
					continue;
				}
				node = cacheNode.element("dbinvocation-class");
				if(node != null && !node.getTextTrim().equals("")) {
					cacheConfig.setDbInvocationName(node.getTextTrim());
				}
				else {
					cacheConfig.setDbInvocationName(""); 
				}
				node = cacheNode.element("observers");
				if(node != null && !node.getTextTrim().equals("")) {
					cacheConfig.setObservers(node.getTextTrim());
				}
				else {
					cacheConfig.setObservers(""); 
				}
				node = cacheNode.element("observables");
				if(node != null && !node.getTextTrim().equals("")) {
					cacheConfig.setObservables(node.getTextTrim());
				}
				else {
					cacheConfig.setObservables(""); 
				}
				node = cacheNode.element("max-life-time");
				if(node != null && !node.getTextTrim().equals("")) {
					cacheConfig.setMaxLifeTime(node.getTextTrim());
				}
				else {
					cacheConfig.setMaxLifeTime(""); 
				}
				cacheConfigMap.put(cacheConfig.getName(), cacheConfig);
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
	 * 获得初始化缓存时所需的配置
	 * @return
	 */
	public HashMap<String, CacheConfig> getCacheConfigMap() {
		return this.cacheConfigMap;
	}
	
}
