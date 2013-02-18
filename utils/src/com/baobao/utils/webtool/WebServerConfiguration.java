/**
 * @Title: WebServerConfiguration.java
 * @Package:com.kmsocial.webservice.config
 * @Description
 * @author luxiangning
 * @date 2012-02-09下午15:05:33
 */
package com.baobao.utils.webtool;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;


public class WebServerConfiguration extends com.baobao.utils.file.BaseFile {

	private static WebServerConfiguration instance      = new WebServerConfiguration();
    private static final String            DEFAULT_PATH   = "web/server.xml";
    
    private String address = "0.0.0.0";
    private int port = 80;
    private String default_servlet = "/*.html";
    public String getDefault_servlet() {
		return default_servlet;
	}

	private Map<String,String> params = new HashMap<String,String>();

    private WebServerConfiguration(){
    	super.path = DEFAULT_PATH;
    	super.root = "server";
    	init();
    }
    
    public static final WebServerConfiguration getInstance () {

        return instance;
    }

    public int getPort () {

        return port;
    }

    public String getAddress () {
        return address;

    }
    
    public Map<String,String> getParams(){
    	return Collections.unmodifiableMap(params);
    }

	@Override
	protected boolean init() {
		try{
			lock.readLock().lock();
			if (path == null) {
				Logger.getLogger(this.getClass()).debug("filePath is null");
				System.exit(1);
			}
			this.params.clear();
			Document doc = getDocument();
			if (doc == null) {
				Logger.getLogger(this.getClass()).error("Something wrong while parsing xml!");
				System.exit(1);
			}
			Element root = doc.getRootElement();
			if (root == null)
				System.exit(1);
			this.address = root.attributeValue("address");
			if(null == this.address || this.address.trim().length() == 0){
				Logger.getLogger(this.getClass()).error("Address cannot be null!");
				System.exit(1);
			}
			String temp_port = root.attributeValue("port");
			if(null != temp_port)
				this.port = Integer.valueOf(temp_port);
			Logger.getLogger(this.getClass()).info("parse server.xml address="+this.address +",port="+this.port);
			List<Element> nodes = root.elements("param");
			Attribute param_name;
			Attribute param_value;
			for(Element node:nodes){
				param_name  = node.attribute("name");
				param_value = node.attribute("value");
				if(null != param_name && param_name.getValue().trim().length() ==0)
					continue;
				if(null != param_value && param_value.getValue().trim().length() ==0)
					continue;
				this.params.put(param_name.getValue().trim(), param_value.getValue().trim());
			}
			Element dfservlet = root.element("default-servlet");
			if(dfservlet.getText()!=null)
				default_servlet = dfservlet.getText();
		}catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e.getMessage());
			System.exit(1);
		} finally {
			lock.readLock().unlock();
		}
		return true;
	}

	@Override
	public boolean storeData() {
		// TODO Auto-generated method stub
		return false;
	}
}
