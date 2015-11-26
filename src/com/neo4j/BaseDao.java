package com.neo4j;

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

import com.type.datatype.ExpressString;

public class BaseDao {
	// 使用log4j记录日志
	private static Logger logger = Logger.getLogger(BaseDao.class);
	// 连接路径
	private static final String URL = "jdbc:neo4j://172.26.13.122:7474";
	// neo4j用户名
	private static final String USERNAME = "neo4j";
	// neo4j密码
	private static final String PASSWORD = "reacher";
	//TODO  Neo4j 连接 ，是否要做连接池
	private Neo4jConnection conn = null;
	//数据的版本信息
	private final String VERSION = "0.1-SNAPSHOT";
	//TODO 节点类型
	public enum Label {
		Node,Log,User
	}
	
	public Neo4jConnection getConn() {
		return conn;
	}

	public void setConn(Neo4jConnection conn) {
		this.conn = conn;
	}

	/*
	 * 构造函数
	 * 设置log4j
	 * 初始化Connection
	 */
	public BaseDao() {
		PropertyConfigurator.configure("log4j.properties");
		setConn(getConnection());
	}

	public static void main(String[] args) {
		BaseDao ins = new BaseDao();
		/*String sql = "MATCH (:Movie {title:{1}})<-[:ACTED_IN]-(a:Person) RETURN a.name as actor";
			List<Map<String, Object>> list = ins.queryList(sql,"The Matrix");
			System.out.println("------------------------");
			System.out.println(list);*/

		/*int nodeA = ins.creatNode("D1",2);
			int nodeB1 = ins.creatNode("E1", 3);
			int nodeB2 = ins.creatNode("E2", 0);
			ins.createRelationshipTo(nodeA, nodeB1);
			ins.createRelationshipTo(nodeA, nodeB2);

			int nodeC1 = ins.creatNode("F1", 0);
			int nodeC2 = ins.creatNode("F2", 0);
			int nodeC3 = ins.creatNode("F3", 0);
			ins.createRelationshipTo(nodeB1, nodeC1);
			ins.createRelationshipTo(nodeB1, nodeC2);
			ins.createRelationshipTo(nodeB1, nodeC3);

			System.out.println(ins.getDirectChildren(nodeA));
			System.out.println(ins.getChildren(nodeA));
			System.out.println(ins.getChildrenNum(nodeA));*/

		List<ExpressString> list = ins.getExpressString();
		System.out.println(list);

		ins.logout();

	}
	
	

	/**
	 * 获取数据库连接
	 * @return
	 */
	public Neo4jConnection getConnection() {
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
	 * 创建节点
	 * @param name	创建的节点名称
	 * @param children_num	子节点的个数
	 * @return	返回创建的节点id
	 */
	public int creatNode(String name,int children_num) {
		//TODO　　封装节点
		String sql = "create (n:" + Label.Node + 
				" {version:'" + VERSION + "',created_time:'" + Util.getCurrentTime()
				+ "',ip:'" + Util.getIP() + "',name:{1},children_num:{2}}),(n2:" + Label.Log +" {name:{1}})," 
				+ "(n)-[:Log_for]->(n2) return ID(n)";

		Map<String, Object> rs = query(sql,name,children_num);
		return (Integer) rs.get("ID(n)");
		
	}
	
	/**
	 * 根据节点id创建关系
	 * @param id1
	 * @param id2
	 * @return	返回受影响的行数
	 */
	public int createRelationshipTo(int id1,int id2) {
		String sql = "start m=node({1}),n=node({2}) CREATE m-[r:Related_to]->n";
		int rows = update(sql,id1,id2);
		return rows;
	}

	
	public String getName(int id) {
		String sql = "start n=node({1}) return n.name as name";
		Map<String, Object> rs = query(sql,id);
		return (String) rs.get("name");
	}
	
	/**
	 * 返回指定节点的直接子节点
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getDirectChildren(int id) {
		String sql = "start n=node({1}) match n-[]->(m:" + Label.Node + ") return ID(m) as id";
		List<Map<String, Object>> result = queryList(sql,id);
		return result;
	}
	
	/**
	 * 返回指定节点的全部子节点
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getChildren(int id) {
		String sql = "start n=node({1}) match n-[*1..]->(m:" + Label.Node + ") return ID(m) as id";
		List<Map<String, Object>> result = queryList(sql,id);
		return result;
	}
	
	/**
	 * 返回指定节点的全部子节点个数
	 * @param id
	 * @return
	 */
	public int getChildrenNum(int id) {
		String sql = "start n=node({1}) match n-[*1..]->(m:" + Label.Node + ") return ID(m) as id";
		List<Map<String, Object>> result = queryList(sql,id);
		return result.size();
	}
	
	/**
	 * 进行图遍历，查询所有的Str
	 * @return
	 */
	//TODO  一个节点对应两个node(本身和Log)
	public List<ExpressString> getExpressString() {
		List<ExpressString> res = new ArrayList<ExpressString>();
		
		//返回string_type对应的id
		String sql = "start n=node(*) match (n:Node) where n.name='string_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = queryList(sql);
		for(int i=0; i<nodeList.size(); i++) {
			int nodeID = (Integer) nodeList.get(i).get("id");
			
			//每个string_type创建Str
			sql = "start n=node({1}),m=node({2}) return n.name as width,m.name as fixed";
			Map<String, Object> map = query(sql,nodeID+6,nodeID+8);
			res.add(new ExpressString(nodeID, Integer.parseInt((String)map.get("width")),map.get("fixed").equals("FIXED")?true:false));
		}
		
		return res;
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
