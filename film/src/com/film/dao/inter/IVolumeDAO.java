package com.film.dao.inter;

import java.util.List;
import java.util.Map;

import com.film.dao.bean.VolumeBean;

public interface IVolumeDAO {
	/**
	 * 插入一个电影或电视剧的集
	 * @param bean
	 */
	boolean insert(VolumeBean bean);
	/**
	 * 插入一系列
	 * @param beans
	 * @return
	 */
	boolean insert(Map<Integer,List<VolumeBean>> beans);
	/**
	 * 得到一集
	 * @param id		集ID
	 * @return
	 */
	VolumeBean getVolume(long id);
	/**
	 * 得到一个列表
	 * @param ids		集ID
	 * @return
	 */
	List<VolumeBean> getVolumes(List<Long> ids);
	/**
	 * 通过影片ID得到剧集
	 * @param videoID	影片ID
	 * @return
	 */
	List<VolumeBean> getVolumesByVideoID(long videoID);
	
	/**
	 * 删除一集
	 * @param id		集ID
	 */
	boolean deleteVolume(long id);
	/**
	 * 
	 * @param belongTo
	 * @param md5
	 * @return
	 */
	boolean exist(long belongTo,String md5);
}
