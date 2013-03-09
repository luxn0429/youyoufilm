/**
 * 
 */
package com.film.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.film.dao.factory.DaoFactory;

/**
 * @author iminer5
 *
 */
public class ClickCache {
	
	private Map<Long,Integer> clickMap = new ConcurrentHashMap<Long,Integer>();
	private ClickCache(){
		
	}
	
	private static ClickCache instance = new ClickCache();
	public static ClickCache getInstance(){return instance;}
	
	public void click(long videoId){
		Integer number = clickMap.get(videoId);
		if(null == number)
			clickMap.put(videoId, number);
		else
			clickMap.put(videoId, number++);
	}
	
	class SaveValue implements Runnable{
        public void run(){
          while(true){
        	  try{
        		  Map<Long,Integer> store = clickMap;
        		  clickMap = new ConcurrentHashMap<Long,Integer>();
        		  for(Map.Entry<Long,Integer> entry:store.entrySet()){
        			  ///更新点击
        			  DaoFactory.getInstance().getVideoClickDAO().updateVideoClick(entry.getKey(),entry.getValue());
        			  ///更新排名
        			  OrderListCache.getInstance().addToClickList(entry.getKey());
        		  }
                 //休眠10分钟
                 Thread.sleep(10*60*1000L);
        	  }catch(Exception ex){
        		  ex.printStackTrace();
              }
          }
        }
	}
}
