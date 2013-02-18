package com.baobao.utils;

import java.net.URLDecoder;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
/**
 * 一个自动找到config文件目录的工具类
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class ConfigUtil {

    private ConfigUtil() {

    }
    
    public static final String DEFAULT_CONFIG_PATH = "config/";
    /**
     * 路径分隔符
     */
    public static final String fileSeparator = System.getProperty("file.separator");

    /**
     * 根据本类所在的位置，获得Config文件的目录的路径。因为类缺省放在/WEB-INF/classes或/WEB-INF/lib下
     * 而config文件则缺省是放在/WEB-INF/下
     * @return Config文件的路径。如果没有找到，return null;
     */
    
    public static String defaultConfigPath() {
    	return defaultConfigPath(ConfigUtil.class);
    }
    

    /**
     * 获得缺省的安装目录
     * @return 缺省的安装目录
     */
    
    public static String defaultHomePath() {
    	String configPath = defaultConfigPath();
        if (configPath == null) {
            return null;
        }
        int n = configPath.lastIndexOf(fileSeparator);
        if (n > 0) {
            return configPath.substring(0, n);
        }
        return configPath;
    }
    

    /**
     * 获得指定文件名的绝对路径（包含指定文件名）。defaultConfPath() + 分隔符 + confFileName
     * @param confFileName 指定Config文件的文件名
     * @return 配置文件的绝对路径,如果没有找到配置文件目录，return null;
     * @see defaultConfigPath()
     */
    
    public static String getDefaultConfFile(String confFileName) {
        String path = defaultConfigPath();
        if (path == null) {
            return null;
        }
        String s = path + fileSeparator + confFileName;
        return s;

    }

    /**
     * 根据指定类所在的位置，获得Config文件的目录路径。因为类缺省放在/WEB-INF/classes或/WEB-INF/lib下
     * 而config文件则缺省是放在/WEB-INF/下
     * @param c 指定类
     * @return 根据指定类所在的位置,Config文件的路径。如果没有找到，return null;
     */
    
    public static String defaultConfigPath(Class<ConfigUtil> c) {
        String startMark = "file:";
        String endMark = "/WEB-INF";
        String s = c.getName().replace('.', '/') + ".class";
        //当资源class放在/WEB-INF/classs中时，
        //url=file:/e:/test/project/defaultroot/WEB-INF/classes/com.world2.util.ConfigUtil.class
        //当资源class打成jar包放在/WEB-INF/lib中时，
        //url=jar:file:/e:/test/project/defaultroot/WEB-INF/lib/res.jar!/com.world2.util.ConfigUtil.class
        java.net.URL url = c.getClassLoader().getResource(s);
        String upath = url.toString();

        int n1 = upath.indexOf(startMark);
        n1 = n1 < 0 ? 0 : n1 + startMark.length();
        int n2 = upath.lastIndexOf(endMark + "/classes");
        if (n2 < 0) {
            n2 = upath.lastIndexOf(endMark + "/lib");
            if (n2 < 0) {
                return null;
            }
        }
        n2 += endMark.length();

        String path = upath.substring(n1, n2);
        if (path.startsWith("/") && path.indexOf(":") == 2) {
            path = path.substring(1);
        }

        return path.replace('/', fileSeparator.charAt(0));
    }
    
    private static String cpath = null;
    /**
     * 得到${tomcat_rootPath}
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String getTomcatRootPath() throws UnsupportedEncodingException{
    	if(null != cpath)
    		return cpath;
    	cpath = ConfigUtil.class.getResource("/").getPath();
		cpath = URLDecoder.decode(cpath, "UTF-8");
		int posi = cpath.indexOf("webapps");
		if (posi > 0) {
			cpath = cpath.substring(0, posi);
		}
    	return cpath;
    }
    
    private static String defaultConfig = null;
    /**
     * 得到默认配置文件路径
     */
    public static String getDefaultConfigDir(){
    	if(defaultConfig != null)
    		return defaultConfig;
    	String homePath = defaultHomePath()+"/WEB-INF";
    	Properties dirPro = new Properties();
    	FileInputStream input = null;
		try {
			input = new FileInputStream(homePath+fileSeparator+"config.properties");
			dirPro.load(input);
			defaultConfig = dirPro.getProperty("config_dir");
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
			dirPro = null;
		} catch (IOException e) {
			e.printStackTrace();
			dirPro = null;
		}finally{
			if(null != input)
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return defaultConfig;
    }
    
    /**
     * 获取${tomcat_rootPath}/common/sns_config/路径
     * @param c Class
     * @return String
     */
    private static String modleConfigPath = null;
    public static String defaultTomcatCommonConfigPath() {
    	
    	if(null != modleConfigPath)
    		return modleConfigPath;
    	
    	try {
			modleConfigPath = getTomcatRootPath()+"common/"+getDefaultConfigDir()+"/";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return modleConfigPath; 

    }
    
    public static String defaultTomcatLogsPath() {
    	try {
			return getTomcatRootPath()+"logs/";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
    }
    
    public static String defaultTomcatCacheFilePath() {

    	try {
			return getTomcatRootPath()+"cache/";
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return "";
    }
    
    public static void main(String[] args) {
        //java.net.URL url = (ConfigUtil.class).getClassLoader().getResource("com/world2/util/ConfigUtil.class");

        //System.out.println(url.toString());
        //System.out.println(url.getPath());
        //String path = ConfigUtil.defaultHomePath();
        //String filename = path + ConfigUtil.fileSeparator + "web.xml";
        //System.out.println(filename);
        //java.io.File file = new java.io.File(filename);
        //System.out.println(file.exists());
    	//String path = ConfigUtil.getTomcatRootPath();
    	//System.out.println("tomcat root path: "+path);
    }
    
}
