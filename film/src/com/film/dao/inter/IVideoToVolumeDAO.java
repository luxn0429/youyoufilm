/**
 * 
 */
package com.film.dao.inter;

import java.util.List;

import com.film.dao.bean.VideotovolumeBean;

/**
 * @author luxianginng
 *
 */
public interface IVideoToVolumeDAO {
	/**
	 * 插入中间对应数据
	 * @param bean		中间对应Bean
	 * @return
	 */
	boolean insert(VideotovolumeBean bean);
	/**
	 * 得到某个视频的所有集
	 * @param vid		视频ID
	 * @return
	 */
	List<VideotovolumeBean> getAllVideoToVolume(long vid);
	/**
	 * 通过视频ID得到所有集的ID
	 * @param vid		视频ID
	 * @return
	 */
	List<Long> getAllVolumeID(long vid);
	/**
	 * 删除一个对应关系
	 * @param id
	 * @return
	 */
	boolean delete(int id);
	
	boolean delete(long videoId,long volumeId);
	/**
	 * 删除某个视频的所有集
	 * @param vid
	 * @return
	 */
	boolean deleteAll(long vid);
}
