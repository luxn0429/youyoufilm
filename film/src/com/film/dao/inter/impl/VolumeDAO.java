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
import java.util.Map;

import com.baobao.utils.dbtool.DBCPManager;
import com.baobao.utils.dbtool.DBConstants;
import com.baobao.utils.dbtool.DBNameManager;
import com.film.dao.bean.VolumeBean;
import com.film.dao.inter.IVolumeDAO;

/**
 * @author luxianginng
 *
 */
public class VolumeDAO implements IVolumeDAO {
	private static final String TABLE_NAME = "volume";
	
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	/* (non-Javadoc)
	 * @see com.film.db.inter.VolumeInterface#insert(com.film.db.bean.VolumeBean)
	 */
	@Override
	public boolean insert(VolumeBean bean) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "insert into " + TABLE_NAME + "(url,belongto,volume,player,description,md5) " +
					"values(?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(insert);
			int i=1;
			pstmt.setString(i++,bean.getUrl());
			pstmt.setLong(i++,bean.getBelongto());
			pstmt.setString(i++,bean.getVolume());
			pstmt.setInt(i++,bean.getPlayer());
			pstmt.setString(i++,bean.getDescription());
			pstmt.setString(i,bean.getMd5());
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
	
	public boolean insert(Map<Integer,List<VolumeBean>> beans){
		PreparedStatement pstmt = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "insert into " + TABLE_NAME + "(url,belongto,volume,player,description,md5) " +
					"values(?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(insert);
			for(Map.Entry<Integer,List<VolumeBean>> entry:beans.entrySet()){
				for(VolumeBean bean:entry.getValue()){
					int i=1;
					pstmt.setString(i++,bean.getUrl());
					pstmt.setLong(i++,bean.getBelongto());
					pstmt.setString(i++,bean.getVolume());
					pstmt.setInt(i++,bean.getPlayer());
					pstmt.setString(i++,bean.getDescription());
					pstmt.setString(i,bean.getMd5() );
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			return true;
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
	 * @see com.film.db.inter.VolumeInterface#getVolume(long)
	 */
	@Override
	public VolumeBean getVolume(long id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement("SELECT * FROM "+TABLE_NAME +" WHERE id=?");
			stmt.setLong(1,id);
			rs = stmt.executeQuery();
			if (!rs.first())
				return null;
			while(rs.next()){
				return getBean(rs);
			}
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
	
	private VolumeBean getBean(ResultSet rs) throws SQLException{
		VolumeBean bean = new VolumeBean();
		bean.setId(rs.getLong("id"));
		bean.setUrl(rs.getString("url"));
		bean.setBelongto(rs.getLong("belongto"));
		bean.setVolume(rs.getString("volume"));
		bean.setPlayer(rs.getInt("player"));
		bean.setDescription(rs.getString("description"));
		bean.setMd5(rs.getString("md5"));
		return bean;
	}

	/* (non-Javadoc)
	 * @see com.film.db.inter.VolumeInterface#getVolume(java.util.List)
	 */
	@Override
	public List<VolumeBean> getVolumes(List<Long> ids) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			StringBuffer buffer = new StringBuffer("SELECT * FROM "+TABLE_NAME +" WHERE id IN(");
			
			for(int i=0;i<ids.size();i++){
				buffer.append("?,");
			}
			int comma = buffer.lastIndexOf(",");
			buffer.deleteCharAt(comma);
			buffer.append(")");
			
			stmt = conn.prepareStatement(buffer.toString());
			for(int i=0;i<ids.size();i++){
				stmt.setLong(i+1, ids.get(i));
			}
			
			rs = stmt.executeQuery();
			if (!rs.first())
				return null;
			List<VolumeBean> list = new ArrayList<VolumeBean>();
			while(rs.next()){
				list.add(getBean(rs));
			}
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
	 * @see com.film.db.inter.VolumeInterface#deleteVolume(long)
	 */
	@Override
	public boolean deleteVolume(long id) {
		String update = "delete from  "+TABLE_NAME +" where id=?";
		String DBName = DBNameManager.getPubDBName();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = dbcpManager.getConnection(DBName,DBConstants.HASH_UPDATE_BASIC);
			pstmt = conn.prepareStatement(update);
			////删除评论
			pstmt.setLong(1, id);
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
	public boolean exist(long belongTo, String md5) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement("SELECT * FROM "+TABLE_NAME +" WHERE belongto=? AND md5=?");
			stmt.setLong(1,belongTo);
			stmt.setString(2, md5);
			rs = stmt.executeQuery();
			if (!rs.first())
				return false;
			
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, stmt, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public List<VolumeBean> getVolumesByVideoID(long videoID) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			StringBuffer buffer = new StringBuffer("SELECT * FROM "+TABLE_NAME +" WHERE belongto=? ORDER BY ID");
			stmt = conn.prepareStatement(buffer.toString());
			stmt.setLong(1,videoID);
			
			rs = stmt.executeQuery();
			List<VolumeBean> list = new ArrayList<VolumeBean>();
			while(rs.next()){
				list.add(getBean(rs));
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
	public boolean updatePlayer(String md5, int player) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement("UPDATE "+TABLE_NAME +" SET player=? WHERE md5=?");
			stmt.setInt(1, player);
			stmt.setString(2,md5);
			return stmt.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, stmt, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
