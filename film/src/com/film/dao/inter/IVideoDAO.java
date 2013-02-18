/**
 * 电影或电视剧库操作类
 */
package com.film.dao.inter;

import java.util.List;

import com.film.dao.bean.VideoBean;
import com.film.dao.bean.VideoFilter;

/**
 * @author luxianginng
 *
 */
public interface IVideoDAO {
	/**
	 * 插入并返回ID
	 * @param bean
	 */
	int insert(VideoBean bean);
	/**
	 * 通过视频类型得到视频
	 * @param type		视频类型：动作、爱情等 见Videotype表
	 * @return
	 *//*
	List<VideoBean> getVideoByType(int type);*/
	/**
	 * 
	 * @param filter	查询过滤
	 * @return
	 */
	List<VideoBean> getVideoByType(VideoFilter filter);
	/**
	 * 
	 * @param videoId	得到一个视频
	 * @return
	 */
	VideoBean getVideoBean(long videoId);
	/**
	 * 删除
	 * @param beanId
	 */
	boolean delete(long beanId);
}
