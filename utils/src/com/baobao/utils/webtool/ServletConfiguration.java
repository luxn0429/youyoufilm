/**
 * @Title: ServletConfiguration.java
 * @Package:com.kmsocial.webservice.config
 * @Description
 * @author luxiangning
 * @date 2012-02-09下午15:05:33
 */
package com.baobao.utils.webtool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;


public class ServletConfiguration extends com.baobao.utils.file.BaseFile {
	
	private static final String DEFAULT_PATH = "web/web.xml";
	private Map<String,String> servletMap = new HashMap<String,String>();
	
	private static final ServletConfiguration instance = new ServletConfiguration();
	
	public static ServletConfiguration getInstance(){
		return instance;
	}
	
	public Map<String, String> getServletMap() {
		return servletMap;
	}

	private ServletConfiguration(){
		super.path = DEFAULT_PATH;
		super.root = "web";
		this.init();
	}
	
	@Override
	protected boolean init() {
		try{
			lock.readLock().lock();
			if (path == null) {
				Logger.getLogger(this.getClass()).debug("filePath is null");
				System.exit(1);
			}
			Document doc = getDocument();
			if (doc == null) {
				Logger.getLogger(this.getClass()).error("Something wrong while parsing xml!");
				System.exit(1);
			}
			Element root = doc.getRootElement();
			if (root == null)
				System.exit(1);
			List<Element> servlets = root.elements("servlet");
			for(Element servlet:servlets){
				Element class_path = servlet.element("class");
				String classText = class_path.getText();
				try{
					Class c = Class.forName(classText);
					c.newInstance();
					Element mapping = servlet.element("mapping");
					String map_text = mapping.getText();
					if(servletMap.containsKey(map_text)){
						Logger.getLogger(this.getClass()).error("The same mapping exist:"+map_text);
						System.exit(1);
					}
					Logger.getLogger(this.getClass()).info("add servlet "+c.getName());
					System.out.println(c.getName());
					this.servletMap.put(map_text,c.getName());
				}catch( InstantiationException e){
					e.printStackTrace();
					Logger.getLogger(this.getClass()).error("class not found:"+classText);
					System.exit(1);
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

	@Override
	public boolean storeData() {
		return false;
	}

}
