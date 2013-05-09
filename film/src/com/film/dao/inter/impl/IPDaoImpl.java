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
import com.film.dao.bean.IPBean;
import com.film.dao.inter.IIPDao;

/**
 * @author xiangning
 *
 */
public class IPDaoImpl implements IIPDao {
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	private static final String TABLE_NAME = "proxy";
	/**
	 * 
	 */
	public IPDaoImpl() {
	}

	/* (non-Javadoc)
	 * @see com.film.dao.inter.IIPDao#insert(java.lang.String)
	 */
	@Override
	public void insert(String ip) {
		PreparedStatement  pstmt = null;
		Connection conn = null;
		ResultSet newid = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "insert into " + TABLE_NAME + "(proxy,state) " +
					"values(?,?) ON DUPLICATE KEY UPDATE state=0";
			pstmt = conn.prepareStatement(insert);
			int i=1;
			pstmt.setString(i++,ip);
			pstmt.setInt(i,0);
			
			pstmt.executeUpdate();
			
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try {
				dbcpManager.closeConnection(conn, pstmt, newid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.film.dao.inter.IIPDao#getValidIP()
	 */
	@Override
	public List<IPBean> getValidIP() {
		StringBuffer select = new StringBuffer("select * from ");
		select.append(TABLE_NAME).append(" where state=0");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement(select.toString());
			rs = stmt.executeQuery();
			
			List<IPBean> result = new ArrayList<IPBean>();
			while(rs.next()){
				IPBean bean = new IPBean();
				bean.setId(rs.getInt("id"));
				bean.setState(rs.getInt("state"));
				String proxy = rs.getString("proxy");
				String[] split = proxy.split(":");
				if(split.length<2)
					continue;
				bean.setIp(split[0]);
				bean.setPort(Integer.valueOf(split[1]));
				result.add(bean);
			}
			return result;
		}catch(SQLException e){
			System.out.println(select.toString());
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
	 * @see com.film.dao.inter.IIPDao#modifyIPState(int, int)
	 */
	@Override
	public boolean modifyIPState(int id, int state) {
		String update = "update "+TABLE_NAME +" set state=?";
		String DBName = DBNameManager.getPubDBName();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = dbcpManager.getConnection(DBName,DBConstants.HASH_UPDATE_BASIC);
			pstmt = conn.prepareStatement(update);
			pstmt.setInt(1, state);
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
	 * @see com.film.dao.inter.IIPDao#deleteNotValidIP()
	 */
	@Override
	public boolean deleteNotValidIP() {
		String update = "delete from  "+TABLE_NAME +" where state>1";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_UPDATE_BASIC);
			pstmt = conn.prepareStatement(update);
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
