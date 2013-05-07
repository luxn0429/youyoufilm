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
import com.film.dao.bean.VideoBean;
import com.film.dao.bean.VideoFilter;
import com.film.dao.inter.IVideoDAO;

/**
 * @author luxianginng
 *
 */
public class VideoDao implements IVideoDAO {
	private static final String TABLE_NAME = "video";
	
	private DBCPManager dbcpManager = DBCPManager.getInstance();

	/* (non-Javadoc)
	 * @see com.film.db.inter.VideoInterface#insert(com.film.db.bean.VideoBean)
	 */
	@Override
	public int insert(VideoBean bean) {
		PreparedStatement  pstmt = null;
		Connection conn = null;
		ResultSet newid = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.HASH_UPDATE_BASIC);
			String insert = "insert into " + TABLE_NAME + "(name,type,country,pubdate,caption" +
					",language,updateTime,performer,poster,introduction,state,format,clarity,classified,seriousIntro) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE updateTime=?";
			pstmt = conn.prepareStatement(insert);
			int i=1;
			pstmt.setString(i++,bean.getName());
			pstmt.setInt(i++,bean.getType());
			pstmt.setInt(i++,bean.getCountry());
			pstmt.setInt(i++,bean.getPubdate());
			pstmt.setInt(i++,bean.getCaption());
			pstmt.setInt(i++,bean.getLanguage());
			pstmt.setInt(i++,(int)(System.currentTimeMillis()/1000L));
			pstmt.setString(i++,bean.getPerformer());
			pstmt.setString(i++,bean.getPoster());
			pstmt.setString(i++,bean.getIntroduction());
			pstmt.setInt(i++,bean.getState());
			pstmt.setInt(i++,bean.getFormat());
			pstmt.setString(i++,bean.getClarity());
			pstmt.setInt(i++,bean.getClassified());
			pstmt.setString(i++, bean.getSeriousIntro());
			pstmt.setInt(i, (int)(System.currentTimeMillis()/1000L));
			
			int up = pstmt.executeUpdate();
			if(up == 2)
				return -2;
			 
			newid = pstmt.getGeneratedKeys();
			 
			if(newid.next()){
			    return newid.getInt(1);
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
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.film.db.inter.VideoInterface#getVideoByType(com.film.db.bean.VideoFilter)
	 */
	@Override
	public List<VideoBean> getVideoByType(VideoFilter filter) {
		StringBuffer select = new StringBuffer("select * from ");
		select.append(TABLE_NAME);
		
		
		if(filter.getLanguate() != -1 || filter.getCountry()!= -1 || 
				filter.getType() != -1 || filter.getStartPubDate() != -1){
			select.append(" where ");
			
			boolean isFirst = true;
			if(filter.getLanguate() != -1){
				select.append(" language=").append(filter.getLanguate());
				isFirst = false;
			}
			
			if(filter.getCountry() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append("country=").append(filter.getCountry());
				isFirst = false;
			}
			
			if(filter.getType() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" type=").append(filter.getType());
				isFirst = false;
			}
			
			if(filter.getStartPubDate() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" pubdate>=").append(filter.getStartPubDate());
				isFirst = false;
			}
			
			if(filter.getEndPubDate() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" pubdate<=").append(filter.getEndPubDate());
				isFirst = false;
			}
			
			/*if(filter.getType() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" type like ',").append(filter.getType()).append(",'");
				isFirst = false;
			}*/
		}
		
		select.append(" order by pubdate DESC,updateTime DESC limit ").append(filter.getStartLine()).append(",").append(filter.getPageNumber());
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement(select.toString());
			rs = stmt.executeQuery();
			
			List<VideoBean> result = new ArrayList<VideoBean>();
			while(rs.next()){
				VideoBean bean = getBean(rs);
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

	@Override
	public boolean delete(long beanId) {
		String update = "delete from  "+TABLE_NAME +" where id=?";
		String DBName = DBNameManager.getPubDBName();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = dbcpManager.getConnection(DBName,DBConstants.HASH_UPDATE_BASIC);
			pstmt = conn.prepareStatement(update);
			////删除评论
			pstmt.setLong(1, beanId);
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
	public VideoBean getVideoBean(long videoId) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		String select = "select * from "+TABLE_NAME+" where id=?";
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement(select.toString());
			stmt.setLong(1,videoId);
			rs = stmt.executeQuery();
			if(rs.next()){
				VideoBean bean = getBean(rs);
				return bean;
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

	private VideoBean getBean(ResultSet rs) throws SQLException {
		VideoBean bean = new VideoBean();
		bean.setId(rs.getInt("id"));
		bean.setName(rs.getString("name"));
		bean.setType(rs.getInt("type"));
		bean.setCountry(rs.getInt("country"));
		bean.setPubdate(rs.getInt("pubdate"));
		bean.setCaption(rs.getInt("caption"));
		bean.setLanguage(rs.getInt("language"));
		bean.setUpdateTime(rs.getInt("updatetime"));
		bean.setPerformer(rs.getString("performer"));
		bean.setPoster(rs.getString("poster"));
		bean.setIntroduction(rs.getString("introduction"));
		return bean;
	}

	@Override
	public int getVideoNumberByType(VideoFilter filter) {
		StringBuffer select = new StringBuffer("select count(*) from ");
		select.append(TABLE_NAME);
		
		
		if(filter.getLanguate() != -1 || filter.getCountry()!= -1 || 
				filter.getType() != -1 || filter.getStartPubDate() != -1){
			select.append(" where ");
			
			boolean isFirst = true;
			if(filter.getLanguate() != -1){
				select.append(" language=").append(filter.getLanguate());
				isFirst = false;
			}
			
			if(filter.getCountry() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append("country=").append(filter.getCountry());
				isFirst = false;
			}
			
			if(filter.getType() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" type=").append(filter.getType());
				isFirst = false;
			}
			
			if(filter.getStartPubDate() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" pubdate>=").append(filter.getStartPubDate());
				isFirst = false;
			}
			
			if(filter.getEndPubDate() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" pubdate<=").append(filter.getEndPubDate());
				isFirst = false;
			}
			
			/*if(filter.getType() != -1){
				if(!isFirst)
					select.append(" and ");
				select.append(" type like ',").append(filter.getType()).append(",'");
				isFirst = false;
			}*/
		}
		
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement(select.toString());
			rs = stmt.executeQuery();
			
			if (!rs.first())
				return 0;
			return rs.getInt(1);
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
		return 0;
	}

	@Override
	public List<VideoBean> getLatestVideo(int type, int number) {
		StringBuffer select = new StringBuffer("select * from ");
		select.append(TABLE_NAME);
		
		int startType = type;;
		int endType = 0;
		if(type == 100){
			endType = 199;
		}else if(type == 200){
			endType = 299;
		}else{
			endType = type;
		}
		select.append(" where type >=").append(startType);
		select.append(" and type<= ").append(endType);
		select.append(" order by pubdate desc,updateTime desc limit ").append(number);
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement(select.toString());
			rs = stmt.executeQuery();
			
			List<VideoBean> result = new ArrayList<VideoBean>();
			if (!rs.first())
				return result;
			VideoBean bean = getBean(rs);
			result.add(bean);
			while(rs.next()){
				bean = getBean(rs);
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

	@Override
	public long getVideoID(String name) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		String select = "select id from "+TABLE_NAME+" where name=?";
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.HASH_QUERY_BASIC);
			stmt = conn.prepareStatement(select.toString());
			stmt.setString(1,name);
			rs = stmt.executeQuery();
			if(rs.next()){
				return rs.getLong(1);
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
		return -1;
	}
	
}
