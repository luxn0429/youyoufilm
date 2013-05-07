package com.film.searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.film.dao.bean.IPBean;

public class SearchWebPageUtil {

    /**
     * 得到一个连接的内容
     * @param urlStr		连接地址
     * @return
     * @throws WeiboException
     * @throws IOException
     */
    public static String getUrlContent (String urlStr,String gb){
        ////设置登录使用的IP和账号
        long time = System.currentTimeMillis();
        boolean available = false;
        StringBuffer sb = new StringBuffer ();
        int tryTime = 0;
        while (!available && tryTime<5) {
        	BufferedReader br = null;
        	HttpURLConnection conn = null;
        	try{
        		URL url = new URL (urlStr);
	        	conn = (HttpURLConnection) url.openConnection ();
	            conn.setRequestProperty ("User-Agent",
	                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13");
	            conn.setRequestMethod ("GET");
	            conn.connect ();
	            
	            br = new BufferedReader (new InputStreamReader (conn.getInputStream (),gb));
	            String line;
	            while ((line = br.readLine()) != null) {
	                sb.append (line + "\n");
	            }
	            available = true;
        	}catch(Exception e){
        		sb = new StringBuffer();
        		e.printStackTrace();
        		Logger.getLogger (SearchWebPageUtil.class).error("LogNoticeForKmsocialOperation\tsearchGetURLContent\turl:" + urlStr+"\tresult:failed\ttime:"+(System.currentTimeMillis()-time));
        	}finally{
        		if(null != br)
					try {
						br.close ();
					} catch (IOException e) {
						e.printStackTrace();
					}
        		if(null != conn)
        			conn.disconnect();
        	}
        	tryTime++;
        }
        Logger.getLogger (SearchWebPageUtil.class).info ("LogNoticeForKmsocialOperation\tsearchGetURLContent\turl:" + urlStr+"\tresult:success\ttime:"+(System.currentTimeMillis()-time));
        try {
			String res = new String(sb.toString().getBytes("UTF-8"),"UTF-8");
			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    
    public static String getUrlUseProxyContent (String urlStr,String gb){
        ////设置登录使用的IP和账号
        long time = System.currentTimeMillis();
        boolean available = false;
        StringBuffer sb = new StringBuffer ();
        int tryTime = 0;
        while (!available && tryTime<100) {
        	BufferedReader br = null;
        	HttpURLConnection conn = null;
        	IPBean bean = IPCache.getInstance().getIPBean();
        	try{
        		URL url = new URL (urlStr);
        		if(null != bean){
	        		Proxy proxy = new Proxy(Type.HTTP, InetSocketAddress.createUnresolved (bean.getIp(),bean.getPort()));
		        	conn = (HttpURLConnection) url.openConnection (proxy);
        		}else{
        			conn = (HttpURLConnection) url.openConnection ();
        		}
	            conn.setReadTimeout(30*1000);
	        	conn.setRequestProperty ("User-Agent",
	                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13");
	            conn.setRequestMethod ("GET");
	            conn.connect ();
	            
	            br = new BufferedReader (new InputStreamReader (conn.getInputStream (),gb));
	            String line;
	            while ((line = br.readLine()) != null) {
	                sb.append (line + "\n");
	            }
	            available = true;
        	}catch(Exception e){
        		sb = new StringBuffer();
        		e.printStackTrace();
        		Logger.getLogger (SearchWebPageUtil.class).error("LogNoticeForKmsocialOperation\tsearchGetURLContent\turl:" + urlStr+"\tresult:failed\ttime:"+(System.currentTimeMillis()-time));
        	}finally{
        		if(null != br)
					try {
						br.close ();
					} catch (IOException e) {
						e.printStackTrace();
					}
        		if(null != conn)
        			conn.disconnect();
        	}
        	tryTime++;
        }
        Logger.getLogger (SearchWebPageUtil.class).info ("LogNoticeForKmsocialOperation\tsearchGetURLContent\turl:" + urlStr+"\tresult:success\ttime:"+(System.currentTimeMillis()-time));
        try {
			String res = new String(sb.toString().getBytes("UTF-8"),"UTF-8");
			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }

    public static void main (String args[]) {
    	PropertyConfigurator.configure ("log4j.properties");
    	String url = "http://www.xigua110.com/";
    	String html = SearchWebPageUtil.getUrlContent(url,"gb2312");
    	System.out.println(html);
    }
}
