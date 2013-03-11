/**
 * 
 */
package com.film.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.film.dao.bean.VideoBean;
import com.film.dao.bean.VideoClickBean;
import com.film.dao.factory.DaoFactory;

/**
 * @author iminer5
 *
 */
public class ClickCache {
	
	private Map<Long,Integer> clickMap = new ConcurrentHashMap<Long,Integer>();
	private ClickCache(){
		Thread saveValue = new Thread(new SaveValue());
		saveValue.start();
	}
	
	private static ClickCache instance = new ClickCache();
	public static ClickCache getInstance(){return instance;}
	
	public void click(long videoId){
		Integer number = clickMap.get(videoId);
		if(null == number)
			clickMap.put(videoId, 1);
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
        			  VideoClickBean click = DaoFactory.getInstance().getVideoClickDAO().getClickBean(entry.getKey());
        			  
        			  if(null == click){
        				  VideoBean video= DaoFactory.getInstance().getVideoDAO().getVideoBean(entry.getKey());
        				  click = new VideoClickBean();
        				  click.setVideoId(video.getId());
        				  click.setClassified(video.getClassified());
        				  click.setType(video.getType());
        				  click.setMonthClick(entry.getValue());
        				  click.setTotalClick(entry.getValue());
        				  click.setWeekClick(entry.getValue());
        				  DaoFactory.getInstance().getVideoClickDAO().insert(click);
        				  OrderListCache.getInstance().addToClickList(entry.getKey());
        				  continue;
        			  }
        			  ///更新点击
        			  DaoFactory.getInstance().getVideoClickDAO().updateVideoClick(entry.getKey(),entry.getValue());
        			  ///更新排名
        			  OrderListCache.getInstance().addToClickList(entry.getKey());
        		  }
        		  Logger.getLogger(this.getClass()).info("save click"+store.size());
                 //休眠10分钟
                 Thread.sleep(10*60*1000L);
        	  }catch(Exception ex){
        		  ex.printStackTrace();
              }
          }
        }
	}
}
