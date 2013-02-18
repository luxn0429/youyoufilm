/**
 * 
 */
package com.film.dao.inter.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.baobao.utils.dbtool.DBCPManager;
import com.baobao.utils.dbtool.DBConstants;
import com.baobao.utils.dbtool.DBNameManager;
import com.film.dao.bean.VideocountryBean;
import com.film.dao.inter.IConstantsDAO;

/**
 * @author luxianginng
 *
 */
public class VideoCountryConstantsDAO extends IConstantsDAO<Integer,VideocountryBean> {
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	private static final String TABLE_NAME = "videocountry";
	
	private VideoCountryConstantsDAO(){};
	private static VideoCountryConstantsDAO instance = new VideoCountryConstantsDAO();
	public static VideoCountryConstantsDAO getInstance(){return instance;}
	
	/**
	 * 加载语言设定
	 */
	@Override
	public Map<Integer, VideocountryBean> loadAllBean() {
		String select = "select * from " + TABLE_NAME;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBNameManager.getPubDBName(),DBConstants.PUBLIC_QUERY_BASIC);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			if (!rs.first())
				return null;
			Map<Integer,VideocountryBean> result = new HashMap<Integer,VideocountryBean>();
			while(rs.next()){
				VideocountryBean bean = new VideocountryBean();
				bean.setId(rs.getInt("id"));
				bean.setName(rs.getString("name"));
				result.put(bean.getId(), bean);
				super.name2Bean.put(bean.getName(),bean);
			}
			return result;
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

}
