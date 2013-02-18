/**
 * 
 */
package com.film.dao.factory;

import com.film.dao.inter.IVideoDAO;
import com.film.dao.inter.IVolumeDAO;
import com.film.dao.inter.impl.VideoDao;
import com.film.dao.inter.impl.VolumeDAO;

/**
 * @author luxianginng
 *
 */
public class DaoFactory {
	private IVideoDAO videoDAO = new VideoDao();
	
	private IVolumeDAO volumeDAO = new VolumeDAO();
	
	private DaoFactory(){
		
	}
	
	private static DaoFactory instance = new DaoFactory();
	public static DaoFactory getInstance(){return instance;}
	
	public IVideoDAO getVideoDAO(){return videoDAO;}
	public IVolumeDAO getVolumeDAO(){return volumeDAO;}
}
