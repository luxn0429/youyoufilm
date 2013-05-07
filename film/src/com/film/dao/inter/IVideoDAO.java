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
	 * 得到最近影片
	 * @param type
	 * @param number
	 * @return
	 */
	List<VideoBean> getLatestVideo(int type,int number);
	/**
	 * 得到数据量
	 * @param filter   查询过滤
	 * @return
	 */
	int getVideoNumberByType(VideoFilter filter);
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
	/**
	 * 通过影片名字得到影片
	 * @param name		影片名字
	 * @return
	 */
	long getVideoID(String name);
}
