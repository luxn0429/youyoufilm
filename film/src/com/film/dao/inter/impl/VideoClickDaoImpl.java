/**
 * 
 */
package com.film.dao.inter.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.baobao.utils.dbtool.DBCPManager;
import com.baobao.utils.dbtool.DBConstants;
import com.baobao.utils.dbtool.DBNameManager;
import com.film.dao.bean.VideoClickBean;
import com.film.dao.inter.IVideoClickDAO;

/**
 * @author xiangning
 *
 */
public class VideoClickDaoImpl implements IVideoClickDAO {
	private static final String TABLE_NAME = "videoclick";
	
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	/* (non-Javadoc)
	 * @see com.film.dao.inter.IVideoClickDao#insert(com.film.dao.bean.VideoClickBean)
	 */
	@Override
	public int insert(VideoClickBean bean) {
		PreparedStatement  pstmt = null;
		Connection conn = null;
		ResultSet newid = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "insert into " + TABLE_NAME + " (id,totalClick,weekClick,monthClick,type,classfied) " +
					"values(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE totalClick=?,weekClick=?,monthClick=?";
			pstmt = conn.prepareStatement(insert);
			int i=1;
			pstmt.setLong(i++,bean.getVideoId());
			pstmt.setInt(i++,bean.getTotalClick());
			pstmt.setInt(i++,bean.getWeekClick());
			pstmt.setInt(i++,bean.getMonthClick());
			pstmt.setInt(i++,bean.getType());
			pstmt.setInt(i++,bean.getClassified());
			pstmt.setInt(i++,bean.getTotalClick());
			pstmt.setInt(i++,bean.getWeekClick());
			pstmt.setInt(i++,bean.getMonthClick());
			
			return pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, pstmt, newid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.film.dao.inter.IVideoClickDao#getClickOrder(int)
	 */
	@Override
	public List<VideoClickBean> getClickOrder(int type,int number) {
		PreparedStatement  pstmt = null;
		Connection conn = null;
		ResultSet newid = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "select * from  " + TABLE_NAME + " order by ";
			if(type == 1)
				insert += "totalClick";
			else if(type == 2)
				insert += "monthClick";
			else insert += "weekClick";
			
			insert += " limit ?";
					
			pstmt = conn.prepareStatement(insert);
			
			pstmt.setInt(1, number);
			newid = pstmt.executeQuery();
			List<VideoClickBean> result = new ArrayList<VideoClickBean>();
			while(newid.next()){
				VideoClickBean bean = new VideoClickBean();
				bean.setVideoId(newid.getLong("id"));
				bean.setTotalClick(newid.getInt("totalClick"));
				bean.setMonthClick(newid.getInt("monthClick"));
				bean.setWeekClick(newid.getInt("weekClick"));
				result.add(bean);
			}
			return result;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, pstmt, newid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	public int updateVideoClick(long videoId,int number){
		PreparedStatement  pstmt = null;
		Connection conn = null;
		ResultSet newid = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "insert into " + TABLE_NAME + "(id,totalClick,weekClick,monthClick) " +
					" values(?,?,?,?) ON DUPLICATE KEY UPDATE totalClick=totalClick+?,weekClick= weekClick+?,monthClick=monthClick+?";
			pstmt = conn.prepareStatement(insert);
			int i=1;
			pstmt.setLong(i++,videoId);
			pstmt.setInt(i++,number);
			pstmt.setInt(i++,number);
			pstmt.setInt(i++,number);
			pstmt.setInt(i++,number);
			pstmt.setInt(i++,number);
			pstmt.setInt(i++,number);
			
			return pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, pstmt, newid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	@Override
	public VideoClickBean getClickBean(long videoId) {
		PreparedStatement  pstmt = null;
		Connection conn = null;
		ResultSet newid = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "select * from  " + TABLE_NAME + " where id=? ";
			pstmt = conn.prepareStatement(insert);
			
			pstmt.setLong(1, videoId);
			newid = pstmt.executeQuery();
			if(newid.first()){
				VideoClickBean bean = new VideoClickBean();
				bean.setVideoId(newid.getLong("id"));
				bean.setTotalClick(newid.getInt("totalClick"));
				bean.setMonthClick(newid.getInt("monthClick"));
				bean.setWeekClick(newid.getInt("weekClick"));
				return bean;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, pstmt, newid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public List<VideoClickBean> getClickOrder(int type, int classified,
			int time, int number) {
		PreparedStatement  pstmt = null;
		Connection conn = null;
		ResultSet newid = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			StringBuilder insert = new StringBuilder("select * from  ").append(TABLE_NAME);
			insert.append(" where ");
			if(type>0) insert.append(" type=? ");
			insert.append(" classified=? ");
			insert.append(" order by ");
			if(time == 1)
				insert.append("totalClick");
			else if(time == 2)
				insert.append("monthClick");
			else insert.append("weekClick");
			insert.append(" limit ?");
			pstmt = conn.prepareStatement(insert.toString());
			
			int i=1;
			if(type>0) pstmt.setInt(i++, type);
			pstmt.setInt(i++,classified);
			pstmt.setInt(i, number);
			
			newid = pstmt.executeQuery();
			List<VideoClickBean> result = new ArrayList<VideoClickBean>();
			while(newid.next()){
				VideoClickBean bean = new VideoClickBean();
				bean.setVideoId(newid.getLong("id"));
				bean.setTotalClick(newid.getInt("totalClick"));
				bean.setMonthClick(newid.getInt("monthClick"));
				bean.setWeekClick(newid.getInt("weekClick"));
				result.add(bean);
			}
			return result;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, pstmt, newid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
