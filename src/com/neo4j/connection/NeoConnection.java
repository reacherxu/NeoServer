package com.neo4j.connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.neo4j.jdbc.Driver;
import org.neo4j.jdbc.Neo4jConnection;

import com.neo4j.dao.BaseDao;

public class NeoConnection {

	// 使用log4j记录日志
	private static Logger logger = Logger.getLogger(BaseDao.class);
	// XXX： 连接路径,如何不指定且能保证在一个connection中      114.212.83.134
	private static final String URL = "jdbc:neo4j://114.212.83.4:7474";
	// neo4j用户名
	private static final String USERNAME = "neo4j";
	// neo4j密码
	private static final String PASSWORD = "reacher";
	
	private Neo4jConnection conn = null;
	
	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	/* 单例模式 */
	private static NeoConnection instance = null;
	
	private NeoConnection() {}
	
	/**
	 * 一个NeoConnection只对应一个连接
	 * @return
	 */
	public static NeoConnection getNeoConnection() {
		/* 注意：不管有多少个Dao,neoConnect操作只执行一次,不会出现多次连接数据库的操作 */
		if( instance == null ) {
			instance = new NeoConnection();
		}
		instance.neoConnect();
		return instance;
	}
	
	public Neo4jConnection getConn() {
		return conn;
	}

	public void setConn(Neo4jConnection conn) {
		this.conn = conn;
	}
	

	public static void main(String[] args) {
		NeoConnection connection = NeoConnection.getNeoConnection();
		connection.neoConnect();
		connection.logout();
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	public Neo4jConnection neoConnect() {
//		Neo4jConnection conn = null;
		final Driver driver = new Driver();
		final Properties props = new Properties();
		props.put("user", USERNAME);
		props.put("password", PASSWORD);

		logger.debug("开始连接数据库");
		try {
			conn = driver.connect(URL, props);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("数据库连接失败！", e);
		}
		logger.debug("数据库连接成功");
		return conn;
	}

	/**
	 * 关闭数据库连接
	 * @param rs	ResultSet
	 * @param ps	PreparedStatement
	 * @param conn	Neo4jConnection
	 */
	public void close(ResultSet rs, PreparedStatement ps, Neo4jConnection conn) {
		//最后打开的最先关闭
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("关闭ResultSet失败", e);
			}
		}
		if (ps != null) {
			try {
				ps.close();
				ps = null;
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("关闭PreparedStatement失败", e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("关闭Connection失败", e);
			}
		}
	}
	
	/**
	 * 关闭连接
	 */
	public void logout() {
		this.close(null, null, this.getConn());
	}

	/**
	 * 查询多个对象
	 * @param sql	要执行的query语句
	 * @param obj	可变参数列表
	 * @return
	 */
	public List<Map<String, Object>> queryList(String sql, Object... obj) {
		//返回的list
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
//		Neo4jConnection conn = getConnection(); 
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			// 创建PreparedStatement对象
			ps = conn.prepareStatement(sql);
			// 为查询语句设置参数
			setParameter(ps, obj);
			// 获得ResultSet结果集
			rs = ps.executeQuery();
			// 获得结果集信息
			ResultSetMetaData rsmd = rs.getMetaData();
			// 获得列的总数
			int columnCount = rsmd.getColumnCount();
			
			// 遍历结果集，根据信息封装成map
			Map<String, Object> row = null;
			while (rs.next()) {
				row = new HashMap<String, Object>();
				for (int i = 0; i < columnCount; i++) {
					String columnLabel = rsmd.getColumnLabel(i + 1);
					row.put(columnLabel, rs.getObject(columnLabel));
				}
				data.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("数据库操作异常", e);
		} finally {
			close(rs, ps, null);
			logger.debug("释放资源成功");
		}
		return data;
	}

	/**
	 * 查询一个对象
	 * @param sql	要执行的sql语句
	 * @param obj	可变参数列表
	 * @return
	 */
	public Map<String, Object> query(String sql, Object... obj) {
		Map<String, Object> data = null;
		
//		Neo4jConnection conn = getConnection(); 
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			// 创建PreparedStatement对象
			ps = conn.prepareStatement(sql);
			// 为查询语句设置参数
			setParameter(ps, obj);
			// 获得ResultSet结果集
			rs = ps.executeQuery();
			// 获得结果集信息
			ResultSetMetaData rsmd = rs.getMetaData();
			// 获得列的总数
			int columnCount = rsmd.getColumnCount();
			
			// 遍历结果集，根据信息封装成Map
			while (rs.next()) {
				data = new HashMap<String, Object>();
				for (int i = 0; i < columnCount; i++) {
					String columnLabel = rsmd.getColumnLabel(i + 1);
					data.put(columnLabel, rs.getObject(columnLabel));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("数据库操作异常", e);
		} finally {
			close(rs, ps, null);
			logger.debug("释放资源成功");
		}
		return data;
	}

	/**
	 * 数据库更新   Create,Update,Delete
	 * @param sql	要执行的sql语句
	 * @param obj	可变参数列表
	 * @return
	 */
	public int update(String sql, Object... obj) {
//		Neo4jConnection conn = getConnection(); 
		PreparedStatement ps = null;
		int rows = 0;
		try {
			// 创建PreparedStatement对象
			ps = conn.prepareStatement(sql);
			// 为查询语句设置参数
			setParameter(ps, obj);
			// 获得受影响的行数
			rows = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("数据库操作异常", e);
		} finally {
			// 关闭连接
			close(null, ps, null);
			logger.debug("释放资源成功");
		}
		return rows;
	}
	
	/**
	 * 为预编译对象设置参数
	 * @param ps	PreparedStatement
	 * @param obj	parameters
	 * @throws SQLException
	 */
	public void setParameter(PreparedStatement ps, Object... obj)
			throws SQLException {
		if (obj != null && obj.length > 0) {
			// 循环设置参数
			for (int i = 0; i < obj.length; i++) {
				ps.setObject(i + 1, obj[i]);
			}
		}
	}
}
