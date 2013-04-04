package com.film.solr.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 */
public class IndexConfiguration {

    private static IndexConfiguration instance      = null;
    private static Object              singletonLock = new Object ();

    private Properties                 properties    = new Properties ();

    private static final String        CONFIG_FILE   = "config/config.properties";

    public static IndexConfiguration getInstance () {

        if (instance == null) {
            synchronized (singletonLock) {
                if (instance == null) {
                    instance = new IndexConfiguration ();
                }
            }
        }
        return instance;
    }

    private IndexConfiguration () {

        try {
            System.out.println (new File (CONFIG_FILE).getAbsolutePath ());
            BufferedReader br = new BufferedReader (new InputStreamReader (
                    new FileInputStream (new File (CONFIG_FILE)), "UTF-8"));
            String line;
            while ((line = br.readLine ()) != null) {
                if (line.startsWith ("#"))
                    continue;
                final int index = line.indexOf ("=");
                if (index == -1)
                    continue;
                properties.put (line.substring (0, index), line.substring (index + 1));
            }
            br.close ();
        }
        catch (IOException e) {
            Logger.getLogger (this.getClass ()).error ("Ioexception.", e);
        }
    }


    /**
     * 
     * Function:舆情搜索链接
     * 
     * @author luxiangning DateTime 2012-7-11 下午4:28:50
     * @return
     */
    public String getSolrUrl () {

        return properties.getProperty ("solrurl");
    }

    /**
     * 
     * Function: 增量索引建立时间，最小单位分钟
     * 
     * @author luxiangning DateTime 2012-7-11 下午4:29:20
     * @return 如果配置文件中配置了此参数，返回配置的值，否则返回默认值
     */
    public int getIndexDeltaInterval () {

        String temp = properties.getProperty ("indexdelta_interval");
        try {
            return Integer.valueOf (temp);
        }
        catch (Exception e) {

        }
        return 5;
    }

    /**
     * 
     * Function:重新建立索引的时间，为当日24小时中某一个小时
     * 
     * @author luxiangning DateTime 2012-7-11 下午4:32:30
     * @return 如果配置文件中配置了此参数，否则返回-1,-1表示不用自动重建
     */
    public int getIndexRebuildTime () {

        String temp = properties.getProperty ("indexrebuild_time");
        try {
            if (null == temp)
                return -1;
            return Integer.valueOf (temp);
        }
        catch (Exception e) {

        }
        return -1;
    }

    /**
     * 
     * Function:数据导入命令路径
     * 
     * @author luxiangning DateTime 2012-7-11 下午4:50:25
     * @return
     */
    public String getSolrDataImportCommond () {

        return properties.getProperty ("solrimportcommond");
    }
}
