package com.baobao.utils.memcache.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class CacheUtil{
	private static final Log Logger = LogFactory.getLog(CacheUtil.class);
	
	////MemcachedClient配置文件解析
	private static List<MemcachedConfig> memcachedConfigs = new ArrayList<MemcachedConfig>();
	private static List<MemcachedSocketPoolConfig> memcachedSocketPoolConfigs = new ArrayList<MemcachedSocketPoolConfig>();


	public static List<MemcachedConfig> getMemcachedConfigs() {
		return memcachedConfigs;
	}


	public static List<MemcachedSocketPoolConfig> getMemcachedSocketPoolConfigs() {
		return memcachedSocketPoolConfigs;
	}
	
	public static void loadMemcachedConfig(String configfile){
		MemcachedConfig node = null;
		MemcachedSocketPoolConfig socketnode = null;
		try {
			SAXReader reader = new SAXReader();
			reader.setEncoding("UTF-8");
			Document doc = reader.read(configfile);
			Element root = doc.getRootElement();
			List<Element> list = root.elements("cache");
			for(Element element:list){
				node = new MemcachedConfig();
				node.setName(element.attributeValue("name"));
				
				node.setSocketPool(element.attributeValue("socketpool"));
				node.setCompressEnable(Boolean.parseBoolean(element.attributeValue("compressEnable")));
				node.setDefaultEncoding(element.attributeValue("defaultEncoding"));
				node.setClassName(element.elementText("classname"));
				String observable = element.elementText("observable");
				if(null != observable)
					node.setObservable(observable.split(","));
				memcachedConfigs.add(node);
			}
			list = root.elements("socketpool");
			for(Element element:list){
				socketnode = new MemcachedSocketPoolConfig();
				
				if(element.attribute("name")!=null)
					socketnode.setName(element.attributeValue("name"));
				
				if(element.attribute("failover")!=null)
					socketnode.setFailover(Boolean.parseBoolean(element.attributeValue("failover")));
				
				if(element.attribute("initConn")!=null)
					socketnode.setInitConn(Integer.parseInt(element.attributeValue("initConn")));
				
				if(element.attribute("minConn")!=null)
					socketnode.setMinConn(Integer.parseInt(element.attributeValue("minConn")));
				
				if(element.attribute("maxConn")!=null)
					socketnode.setMaxConn(Integer.parseInt(element.attributeValue("maxConn")));
				
				if(element.attribute("maintSleep")!=null)
					socketnode.setMaintSleep(Integer.parseInt(element.attributeValue("maintSleep")));
				
				if(element.attribute("nagle")!=null)
					socketnode.setNagle(Boolean.parseBoolean(element.attributeValue("nagle")));
				
				if(element.attribute("socketTO")!=null)
					socketnode.setSocketTo(Integer.parseInt(element.attributeValue("socketTO")));
				
				if(element.attribute("maxIdle")!=null)
					socketnode.setMaxIdle(Integer.parseInt(element.attributeValue("maxIdle")));
				
				if(element.attribute("aliveCheck")!=null)
					socketnode.setAliveCheck(Boolean.parseBoolean(element.attributeValue("aliveCheck")));
				
				if(element.attribute("hashAlg") != null)
					socketnode.setHashAlgorithm(Integer.valueOf(element.attributeValue("hashAlg")));
				
				if(element.attribute("failover")!=null)
					socketnode.setServers(element.elementText("servers"));
				
				
				
				memcachedSocketPoolConfigs.add(socketnode);
			}
			Logger.info("parser memcached XML finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		loadMemcachedConfig("memcached.xml");
	}
}
