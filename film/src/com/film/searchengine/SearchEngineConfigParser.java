package com.film.searchengine;

//import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.baobao.utils.ConfigUtil;
import com.baobao.utils.file.BaseFile;


/**
* 解析权限配置文件dbcpconfig.xml的类
* @author luxiangning
* @since 2009-10-19
*
*/

public class SearchEngineConfigParser extends BaseFile {
	
	private Map<String,SearchEngine> url2Engine = new HashMap<String,SearchEngine>();
	//单例模式
	private static SearchEngineConfigParser instance = null;
	
	private SearchEngineConfigParser(){
		this.path = ConfigUtil.DEFAULT_CONFIG_PATH + "searchwebsite.xml";
		init();
	}
	
	//创建类的一个实例
	public synchronized static SearchEngineConfigParser getInstance(){
		if(null == instance)
			instance = new SearchEngineConfigParser();
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
			Document doc = getDocument();
			if (doc == null) {
				Logger.getLogger(this.getClass()).error("Something wrong while parsing xml!");
				return false;
			}
			Element root = doc.getRootElement();
			if (root == null)
				return false;
			
			List<Element> webNodes = root.elements("web");
			Attribute attribute;
			String groupId = "";
			String groupType = "";
			for(Element web:webNodes){
				Element urlNode = web.element("url");
				Element parserNode = web.element("parser");
				Element updateNode = web.element("updateUrl");
				String url = urlNode.getText();
				String classPath = parserNode.getText();
				Class<?> newoneClass = Class.forName(classPath);
				Class<?>[] args = new Class[2];
				args[0] = String.class;
				args[1] = String.class;
				
				Constructor cons = newoneClass.getConstructor(args);
				SearchEngine engine = (SearchEngine)cons.newInstance(url,updateNode.getText());
				url2Engine.put(url, engine);
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

	@Override
	public boolean storeData() {
		return false;
	}
	
	public Map<String,SearchEngine> getAllEngine(){
		return url2Engine;
	}
	
	public static void main(String[] args){
		PropertyConfigurator.configure ("log4j.properties");
		Map<String,SearchEngine> url = SearchEngineConfigParser.getInstance().getAllEngine();
		System.out.println(url.size());
		for(Map.Entry<String,SearchEngine> entry:url.entrySet()){
			SearchEngine engine = entry.getValue();
			engine.searchWebSite();
			//engine.searchUpdate();
		}
	}
}
