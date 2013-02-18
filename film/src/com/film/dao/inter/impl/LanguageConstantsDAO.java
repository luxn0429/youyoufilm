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
import com.film.dao.bean.LanguageBean;
import com.film.dao.inter.IConstantsDAO;

/**
 * @author luxianginng
 *
 */
public class LanguageConstantsDAO extends IConstantsDAO<Integer,LanguageBean> {
	private DBCPManager dbcpManager = DBCPManager.getInstance();
	private static final String TABLE_NAME = "language";
	
	private LanguageConstantsDAO(){};
	private static LanguageConstantsDAO instance = new LanguageConstantsDAO();
	public static LanguageConstantsDAO getInstance(){return instance;}
	
	/**
	 * 加载语言设定
	 */
	@Override
	public Map<Integer, LanguageBean> loadAllBean() {
		String DBName = DBNameManager.getPubDBName();
		
		String select = "select * from " + TABLE_NAME;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
			conn = dbcpManager.getConnection(DBName,	DBConstants.PUBLIC_QUERY_BASIC);
			stmt = conn.prepareStatement(select);
			rs = stmt.executeQuery();
			if (!rs.first())
				return null;
			Map<Integer,LanguageBean> result = new HashMap<Integer,LanguageBean>();
			while(rs.next()){
				LanguageBean bean = new LanguageBean();
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
