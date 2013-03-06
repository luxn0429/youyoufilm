/**
 * 
 */
package com.film.dao.inter.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.baobao.utils.dbtool.DBCPManager;
import com.baobao.utils.dbtool.DBConstants;
import com.baobao.utils.dbtool.DBNameManager;
import com.film.dao.bean.VideoClickBean;
import com.film.dao.inter.IVideoClickDao;

/**
 * @author xiangning
 *
 */
public class VideoClickDaoImpl implements IVideoClickDao {
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
			String insert = "insert into " + TABLE_NAME + "(id,totalClick,weekClick,monthClick) " +
					"values(?,?,?,?) ON DUPLICATE KEY UPDATE totalClick=?,weekClick=?,monthClick=?";
			pstmt = conn.prepareStatement(insert);
			int i=1;
			pstmt.setLong(i++,bean.getVideoId());
			pstmt.setInt(i++,bean.getTotalClick());
			pstmt.setInt(i++,bean.getWeekClick());
			pstmt.setInt(i++,bean.getMonthClick());
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
	public List<Long> getClickOrder(int type,int number) {
		
		return null;
	}

}
