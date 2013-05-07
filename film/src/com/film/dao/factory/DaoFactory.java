/**
 * 
 */
package com.film.dao.factory;

import com.film.dao.inter.IIPDao;
import com.film.dao.inter.IVideoClickDAO;
import com.film.dao.inter.IVideoDAO;
import com.film.dao.inter.IVolumeDAO;
import com.film.dao.inter.impl.IPDaoImpl;
import com.film.dao.inter.impl.VideoClickDaoImpl;
import com.film.dao.inter.impl.VideoDao;
import com.film.dao.inter.impl.VolumeDAO;

/**
 * @author luxianginng
 *
 */
public class DaoFactory {
	private IVideoDAO videoDAO = new VideoDao();
	
	private IVolumeDAO volumeDAO = new VolumeDAO();
	
	private IVideoClickDAO videoClick = new VideoClickDaoImpl();
	
	private IIPDao ipDao = new IPDaoImpl();
	
	private DaoFactory(){
		
	}
	
	private static DaoFactory instance = new DaoFactory();
	public static DaoFactory getInstance(){return instance;}
	
	public IVideoDAO getVideoDAO(){return videoDAO;}
	public IVolumeDAO getVolumeDAO(){return volumeDAO;}
	public IVideoClickDAO getVideoClickDAO(){return videoClick;}
	public IIPDao getIPDao(){return ipDao;}
}
