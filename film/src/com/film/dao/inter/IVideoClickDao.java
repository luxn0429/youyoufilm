/**
 * 
 */
package com.film.dao.inter;

import java.util.List;

import com.film.dao.bean.VideoClickBean;

/**
 * @author xiangning
 *
 */
public interface IVideoClickDAO {
	
	int insert(VideoClickBean bean);
	/**
	 * 
	 * @param time		1. total 2. month 3 week
	 * @param number	得到的数量
	 * @return
	 */
	List<VideoClickBean> getClickOrder(int time,int number);
	
	int updateVideoClick(long videoId,int number);
	
	VideoClickBean getClickBean(long videId);
	
	/**
	 * 
	 * @param type			电影或电视剧类别
	 * @param classified	电影或电视剧或综艺等分类
	 * @param time			1. 所有，2 月 3周
	 * @param number		数量
	 * @return
	 */
	List<VideoClickBean> getClickOrder(int type,int classified,int time,int number);
}
