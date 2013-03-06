/**
 * 加中间表为了以后分布式用
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
import com.film.dao.bean.VideotovolumeBean;
import com.film.dao.inter.IVideoToVolumeDAO;

/**
 * @author luxianginng
 *
 */
public class VideoToVolumeDAO implements IVideoToVolumeDAO {
	private static final String TABLE_NAME = "videotovolume";
	
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	/* (non-Javadoc)
	 * @see com.film.db.inter.VideoToVolumeInterface#insert(com.film.db.bean.VideotovolumeBean)
	 */
	@Override
	public boolean insert(VideotovolumeBean bean) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "insert into " + TABLE_NAME + "(videoId,volumeId) " +
					"values(?,?)";
			pstmt = conn.prepareStatement(insert);
			int i=1;
			pstmt.setLong(i++,bean.getVideoId());
			pstmt.setLong(i++,bean.getVolumeId());
			return pstmt.executeUpdate() != 0;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, pstmt, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.film.db.inter.VideoToVolumeInterface#getAllVideoToVolume(long)
	 */
	@Override
	public List<VideotovolumeBean> getAllVideoToVolume(long vid) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement("SELECT * FROM "+TABLE_NAME +" WHERE videoId=? ORDER BY volumeId");
			stmt.setLong(1,vid);
			rs = stmt.executeQuery();
			if (!rs.first())
				return null;
			List<VideotovolumeBean> list = new ArrayList<VideotovolumeBean>();
			while(rs.next()){
				VideotovolumeBean bean = new VideotovolumeBean();
				bean.setId(rs.getInt("id"));
				bean.setVideoId(vid);
				bean.setVolumeId(rs.getLong("volumeId"));
				list.add(bean);
			}
			return list;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, stmt, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.film.db.inter.VideoToVolumeInterface#delete(int)
	 */
	@Override
	public boolean delete(int id) {
		String update = "delete from  "+TABLE_NAME +" where id=?";
		String DBName = DBNameManager.getPubDBName();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = dbcpManager.getConnection(DBName,DBConstants.HASH_UPDATE_BASIC);
			pstmt = conn.prepareStatement(update);
			////删除评论
			pstmt.setInt(1, id);
			return pstmt.executeUpdate() != 0;
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally
		{
			try {
				dbcpManager.closeConnection(conn, pstmt, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.film.db.inter.VideoToVolumeInterface#deleteAll(long)
	 */
	@Override
	public boolean deleteAll(long vid) {
		String update = "delete from  "+TABLE_NAME +" where videoId=?";
		String DBName = DBNameManager.getPubDBName();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = dbcpManager.getConnection(DBName,DBConstants.HASH_UPDATE_BASIC);
			pstmt = conn.prepareStatement(update);
			////删除评论
			pstmt.setLong(1, vid);
			return pstmt.executeUpdate() != 0;
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally
		{
			try {
				dbcpManager.closeConnection(conn, pstmt, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public List<Long> getAllVolumeID(long vid) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement("SELECT volumeId FROM "+TABLE_NAME +" WHERE videoId=? ORDER BY volumeId");
			stmt.setLong(1,vid);
			rs = stmt.executeQuery();
			if (!rs.first())
				return null;
			List<Long> list = new ArrayList<Long>();
			while(rs.next()){
				list.add(rs.getLong("volumeId"));
			}
			return list;
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, stmt, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean delete(long videoId, long volumeId) {
		String update = "delete from  "+TABLE_NAME +" where videoId=? and volumeId=?";
		String DBName = DBNameManager.getPubDBName();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = dbcpManager.getConnection(DBName,DBConstants.HASH_UPDATE_BASIC);
			pstmt = conn.prepareStatement(update);
			////删除评论
			pstmt.setLong(1, videoId);
			pstmt.setLong(2, volumeId);
			return pstmt.executeUpdate() != 0;
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally
		{
			try {
				dbcpManager.closeConnection(conn, pstmt, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
