/**
 *@Title: IndexBuidTask.java
 *@Package:	com.kmsocial.utils.solr.search
 *@Description:	TODO
 *@author luxiangning luxn0429@gmail.com
 *@Date:2012-7-11 下午4:25:49
 *@version	1.0
 **/
package com.film.solr.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 *  Class Name: IndexBuidTask.java
 *  Function:
 *  Modifications:   
 *  
 */

public class IndexBuildTask implements Runnable {

    private static final boolean DEBUG              = true;
    private static final String  IMPORT_COMMOND     = IndexConfiguration.getInstance ().getSolrDataImportCommond ();
    /**
     * 增量建立索引的间隔时间
     */
    private final int            indexDeltaInterval = IndexConfiguration.getInstance ().getIndexDeltaInterval ();
    /**
     * 索引重建的时刻，0~23点中的时间
     */
    private final int            indexRebuildTime   = IndexConfiguration.getInstance ().getIndexRebuildTime ();

    private String               deltImportUrl      = IndexConfiguration.getInstance ().getSolrUrl ()
                                                            + IMPORT_COMMOND + "?command=delta-import";
    private String               rebuildUrl         = IndexConfiguration.getInstance ().getSolrUrl ()
                                                            + IMPORT_COMMOND + "?command=full-import";

    private IndexBuildTask () {

    }

    private static IndexBuildTask instance = new IndexBuildTask ();

    public static IndexBuildTask getInstance () {

        return instance;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */

    @Override
    public void run () {

        boolean hasBuild = false;
        while (true) {
            Logger.getLogger (this.getClass ()).info ("<Indexing> start index");
           
            ////如果需要重建索引

            if (indexRebuildTime >= 0) {
                int hour = Calendar.getInstance ().get (Calendar.HOUR_OF_DAY);
                ///到了重建时间
                if (indexRebuildTime == hour && !hasBuild) {
                    ///重建索引
                    doCommond (rebuildUrl);
                    hasBuild = true;
                }
                else
                    hasBuild = false;
            }

            doCommond (deltImportUrl);
            try {
            TimeUnit.MINUTES.sleep (indexDeltaInterval);
	        }
	        catch (InterruptedException e) {
	            e.printStackTrace ();
	        }
        }

    }

    private ExecutorService exec = Executors.newFixedThreadPool (20);
    /**
     *  Function:
     *
     *  @author luxiangning  DateTime 2012-7-11 下午4:56:40
     */

    private void doCommond (final String solrURL) {
    	exec.execute(new Runnable(){
    		public void run(){
    			BufferedReader reader = null;
    			try {
    	            URL url = new URL (solrURL);
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection ();

    	            conn.setRequestMethod ("POST");
    	            conn.setRequestProperty ("type", "submit");
    	            conn.setDoOutput (true);

    	            conn.connect ();
    	            int rspCode = conn.getResponseCode ();
    	            if (rspCode != 200)
    	                Logger.getLogger (this.getClass ()).error ("<Indexing> Failed! Response Code = " + rspCode);
    	            else {
    	                reader = new BufferedReader (new InputStreamReader (conn.getInputStream ()));
    	                String responseText = "";
    	                String line;
    	                while ((line = reader.readLine ()) != null)
    	                    responseText += line;
    	                Logger.getLogger (this.getClass ()).info ("<Indexing> Starting indexing");
    	                if (DEBUG)
    	                    System.out.println (responseText);
    	            }
    	        }
    	        catch (Exception e) {
    	            e.printStackTrace ();
    	            Logger.getLogger (this.getClass ()).error ("<Indexing> Failed!"+e.getMessage ());
    	        }finally{
    	        	if(null != reader)
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
    	        }
    		}
    	});
    }
    
    public static void main(String[] args){
    	 ExecutorService exec = Executors.newFixedThreadPool (20);
    	 exec.execute(new IndexBuildTask());
    	 
    }
}
