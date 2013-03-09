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
	 * @param type		1. total 2. month 3 week
	 * @param number	得到的数量
	 * @return
	 */
	List<VideoClickBean> getClickOrder(int type,int number);
	
	int updateVideoClick(long videoId,int number);
	
	VideoClickBean getClickBean(long videId);
}
