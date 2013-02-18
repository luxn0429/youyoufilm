package com.baobao.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * 数据层用到的url配置
 * @author luxiangning
 *
 */
public class UrlUtil {
	private static Properties urlProp;
	
    private static Properties getUrlProp() {
    	if(urlProp != null){
    		return urlProp;
    	}
    	urlProp = new Properties();
		try {
			urlProp.load(new FileInputStream(ConfigUtil.defaultTomcatCommonConfigPath() + "url.properties"));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
			urlProp = null;
		} catch (IOException e) {
			e.printStackTrace();
			urlProp = null;
		}
		return urlProp;
	}
    /**
     * 得到某个URL
     * @param name			配置名
     * @return
     */
    public static String getUrl(String name){
    	Properties prpo = getUrlProp();
    	if(null != prpo)
    		return prpo.getProperty(name);
    	return null;
    }
}
