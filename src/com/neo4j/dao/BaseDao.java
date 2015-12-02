package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neo4j.connection.NeoConnection;
import com.neo4j.util.Util;
import com.type.datatype.ExpressString;

public class BaseDao {
	/* string_type 的结构 */
	private static final int STRING_TYPE = 2;
	
	private static final int STRING_TYPE_WIDTH_FIXED = 4;
	
	private NeoConnection neoConn ;
	
	//数据的版本信息
	private final String VERSION = "0.1-SNAPSHOT";
	//TODO 节点类型
	public enum Label {
		Node,Log,User
	}

	/*
	 * 构造函数
	 * 初始化Connection
	 */
	public BaseDao() {
		neoConn = new NeoConnection();
		neoConn.getConnection();
	}

	public static void main(String[] args) {
		BaseDao ins = new BaseDao();
		/*String sql = "MATCH (:Movie {title:{1}})<-[:ACTED_IN]-(a:Person) RETURN a.name as actor";
			List<Map<String, Object>> list = ins.queryList(sql,"The Matrix");
			System.out.println("------------------------");
			System.out.println(list);*/

	/*	int nodeA = ins.creatNode("D1",2);
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
			System.out.println(ins.getChildrenNum(nodeA));

		List<ExpressString> list = ins.getExpressString();
		System.out.println(list);*/
		
//		ins.setLocation(83, 20,21,12,13);
//		List<Double> p =  ins.getLocation(195);
//		System.out.println(p);
//		ins.detachDelete(191);
		
		ins.setProperty(197, "ignore", false);
		ins.logout();

	}
	
	
	public void logout() {
		neoConn.logout();
	}

	/*-------------------------添加节点信息--------------------------------------------*/
	
	/**
	 * 写日志信息
	 * @param id	日志节点
	 * @param ip
	 * @param time
	 * @param operation	操作内容
	 */
	private void writeLog(int id,String ip,String time,String operation) {
		String sql = "start n=node("+id+") set n.log_"+System.currentTimeMillis()+"='client " + ip + operation + " at "+time+"'";
		neoConn.update(sql, ip, time, operation);
	}
	
	/**
	 * 创建节点
	 * @param name	创建的节点名称
	 * @param children_num	子节点的个数
	 * @return	返回创建的节点id
	 */
	public int creatNode(String name,int children_num) {
		String sql = "create (n:" + Label.Node + 
				" {version:'" + VERSION + "',created_time:'" + Util.getCurrentTime()
				+ "',ip:'" + Util.getIP() + "',name:{1},children_num:{2}}),(n2:" + Label.Log +" {name:{1}})," 
				+ "(n)-[:Log_for]->(n2) return ID(n)";

		Map<String, Object> rs = neoConn.query(sql,name,children_num);
		int nodeID = (Integer) rs.get("ID(n)");
				
		writeLog(nodeID+1,Util.getIP(),Util.getCurrentTime()," created the node:" + name);
		
		return nodeID;
	}
	
	/**
	 * 根据节点id创建关系
	 * @param id1
	 * @param id2
	 * @return	返回受影响的行数
	 */
	public int createRelationshipTo(int id1,int id2) {
		String sql = "start m=node({1}),n=node({2}) CREATE m-[r:Related_to]->n";
		int rows = neoConn.update(sql,id1,id2);
		return rows;
	}

	/*-------------------------删除节点信息--------------------------------------------*/
	
	/**
	 * 级联删除节点，包括自身
	 * @param id
	 * @return	返回受影响的行数
	 */
	public int detachDelete(int id) {
		String sql = "start n=node({1}) match (n)-[*0..]->(m) detach delete m";
		int rows = neoConn.update(sql,id);
		return rows;
	}
	
	/*-------------------------修改节点信息--------------------------------------------*/
	
	/**
	 * 设置节点属性  或   添加节点属性
	 * @param id	节点id
	 * @param name	属性名
	 * @param value	属性值
	 */
	public void setProperty(int id, Object name, Object value) {
		String sql = "start n=node({1}) set n." + name.toString() + "=" + value.toString();
		neoConn.update(sql, id);
		
		writeLog(id+1,Util.getIP(),Util.getCurrentTime()," revised the property information,set " + name + ":" + value);
	}
	
	/**
	 * 设置节点位置信息
	 * @param id
	 */
	public void setLocation(int id,Object... obj) {
		String sql = "start n=node("+id+") set n.location_a={1},n.location_b={2}," +
				"n.location_c={3},n.location_d={4}";
		neoConn.update(sql, obj);
		
		writeLog(id+1,Util.getIP(),Util.getCurrentTime()," revised the location information ");
	}
	
	/*-------------------------查询节点信息--------------------------------------------*/
	
	/**
	 * 获取节点位置信息
	 * @param id
	 * @return
	 */
	public List<Double> getLocation(int id) {
		List<Double> position = new ArrayList<Double>();
		
		String sql = "start n=node({1}) return n.location_a as location_a,n.location_b as location_b," +
				"n.location_c as location_c,n.location_d as location_d";
		Map<String, Object> result = neoConn.query(sql,id);
		
		position.add( Double.parseDouble(result.get("location_a").toString()) );
		position.add( Double.parseDouble(result.get("location_b").toString()) );
		position.add( Double.parseDouble(result.get("location_c").toString()) );
		position.add( Double.parseDouble(result.get("location_d").toString()) );
		
		return position;
	}
	
	public String getName(int id) {
		String sql = "start n=node({1}) return n.name as name";
		Map<String, Object> rs = neoConn.query(sql,id);
		return (String) rs.get("name");
	}
	
	/**
	 * 返回指定节点的直接子节点
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getDirectChildren(int id) {
		String sql = "start n=node({1}) match n-[]->(m:" + Label.Node + ") return ID(m) as id";
		List<Map<String, Object>> result = neoConn.queryList(sql,id);
		return result;
	}
	
	/**
	 * 返回指定节点的全部子节点
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getChildren(int id) {
		String sql = "start n=node({1}) match n-[*1..]->(m:" + Label.Node + ") return ID(m) as id";
		List<Map<String, Object>> result = neoConn.queryList(sql,id);
		return result;
	}
	
	/**
	 * 返回指定节点的直接子节点个数
	 * @param id
	 * @return
	 */
	public int getDirectChildrenNum(int id) {
		String sql = "start n=node({1}) match n-[]->(m:" + Label.Node + ") return ID(m) as id";
		List<Map<String, Object>> result = neoConn.queryList(sql,id);
		return result.size();
	}
	
	/**
	 * 返回指定节点的全部子节点个数
	 * @param id
	 * @return
	 */
	public int getChildrenNum(int id) {
		String sql = "start n=node({1}) match n-[*1..]->(m:" + Label.Node + ") return ID(m) as id";
		List<Map<String, Object>> result = neoConn.queryList(sql,id);
		return result.size();
	}
	
	/**
	 * 进行图遍历，查询所有的ExpressString
	 * @return
	 */
	public List<ExpressString> getExpressString() {
		List<ExpressString> res = new ArrayList<ExpressString>();
		Integer val = null;
		Boolean fixed = false;
		
		/* 返回string_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='string_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = neoConn.queryList(sql);
		
		/* 每个string_type创建ExpressString */
		for(int i=0; i<nodeList.size(); i++) {
			int string_type = (Integer) nodeList.get(i).get("id");
			
			/* 判断 数值属性是否存在 */
			if( getDirectChildrenNum(string_type) == STRING_TYPE ) {
				
				int width_spec = getIdByName(string_type,"width_spec");
				
				/*　判断 fixed属性是否存在 */
				if( getDirectChildrenNum(width_spec) == STRING_TYPE_WIDTH_FIXED ) {
					fixed = true;
				}
				
				int width = getIdByName(width_spec,"width");
				
				/* 寻找width的叶子节点 ,添加数值属性 */
				sql = "start n=node({1}) match (n:Node)-[r:Related_to*0..]->(m:Node) return m.name as name";
				List<Map<String, Object>> widthNodes = neoConn.queryList(sql,width);
				val = Integer.parseInt(widthNodes.get(widthNodes.size()-1).get("name").toString());
			}
			
			res.add(new ExpressString(string_type, val, fixed));
		}
		
		return res;
	}
	
	
	/**
	 * 返回某节点 名字为name的节点id
	 * @param id
	 * @param name
	 * @return
	 */
	public int getIdByName(int id,String name) {
		String sql = "start n=node({1}) match (n:Node)-[r*0..]->(m:Node) where m.name={2} return ID(m) as id";
		
		Map<String, Object> map = neoConn.query(sql,id,name);
		
		return (Integer) map.get("id");
	}
	
	
}
