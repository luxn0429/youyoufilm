package com.film.dao.inter.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.baobao.utils.dbtool.DBCPManager;
import com.baobao.utils.dbtool.DBConstants;
import com.baobao.utils.dbtool.DBNameManager;
import com.film.dao.inter.IHotTableDAO;


public class AbstractHotListDAOImpl<T> implements IHotTableDAO<Integer, T>
{
	protected String TABLE_NAME;
	protected String insert;
	protected String select;

	protected AbstractHotListDAOImpl(String tableName)
	{
		this.TABLE_NAME = tableName;
	}

	protected DBCPManager dbcpManager = DBCPManager.getInstance();

	/**
	 * 插入数据到大表，如果数据存在则更新
	 * 
	 * @return 插入结果
	 * @throws NullConnectionException
	 * @throws SQLException
	 */
	@Override
	public boolean insertData(Integer p1, Integer p2, T data) throws SQLException,
			IOException
	{
		// //大表按照ID来进行映射
		Connection conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.PUBLIC_UPDATE_BASIC);
		PreparedStatement pstmt = null;
		ByteArrayOutputStream stream = null;
		ObjectOutputStream objwrite = null;
		try
		{
			// 插入表态信息
			pstmt = conn.prepareStatement(insert);
			pstmt.setInt(1, p1);
			pstmt.setInt(2, p2);
			stream = new ByteArrayOutputStream();
			objwrite = new ObjectOutputStream(stream);
			objwrite.writeObject(data);
			pstmt.setBytes(2, stream.toByteArray());
			pstmt.setBytes(3, stream.toByteArray());
			return pstmt.executeUpdate() != 0;
		}
		finally
		{
			if (null != stream)
				stream.close();
			if (null != objwrite)
				objwrite.close();
			dbcpManager.closeConnection(conn, pstmt, null);
		}
	}

	/**
	 * 得到数据
	 * 
	 * @param id  大表数据对应ID
	 * @return 大表中存储的数据
	 * @throws NullConnectionException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getData(Integer p1,Integer p2) throws SQLException, IOException
	{
		Connection conn = dbcpManager.getConnection(DBNameManager.getPubDBName(), DBConstants.PUBLIC_QUERY_BASIC);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ByteArrayInputStream bin = null;
		ObjectInputStream oin = null;
		try
		{
			stmt = conn.prepareStatement(select);
			stmt.setInt(1, p1);
			stmt.setInt(2, p2);
			rs = stmt.executeQuery();
			if (!rs.first())
				return null;
			byte[] bytes = rs.getBytes(1);
			if (bytes != null)
			{
				bin = new ByteArrayInputStream(bytes);
				oin = new ObjectInputStream(bin);
				try
				{
					return (T) oin.readObject();
				}
				catch (ClassNotFoundException e)
				{
					e.getStackTrace();
				}
			}
			return null;
		}
		finally
		{
			if (null != bin)
				bin.close();
			if (null != oin)
				oin.close();
			dbcpManager.closeConnection(conn, stmt, rs);
		}
	}
}
