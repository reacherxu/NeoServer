package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neo4j.connection.NeoConnection;
import com.neo4j.util.Util;
import com.type.datatype.ExpressString;

public class BaseDao {
	
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
	
	
	private void logout() {
		neoConn.logout();
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

		Map<String, Object> rs = neoConn.query(sql,name,children_num);
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
		int rows = neoConn.update(sql,id1,id2);
		return rows;
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
	//TODO  一个节点对应两个node(本身和Log)
	public List<ExpressString> getExpressString() {
		List<ExpressString> res = new ArrayList<ExpressString>();
		
		//返回string_type对应的id
		String sql = "start n=node(*) match (n:Node) where n.name='string_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = neoConn.queryList(sql);
		for(int i=0; i<nodeList.size(); i++) {
			int nodeID = (Integer) nodeList.get(i).get("id");
			
			//每个string_type创建Str
			sql = "start n=node({1}),m=node({2}) return n.name as width,m.name as fixed";
			Map<String, Object> map = neoConn.query(sql,nodeID+6,nodeID+8);
			res.add(new ExpressString(nodeID, Integer.parseInt((String)map.get("width")),map.get("fixed").equals("FIXED")?true:false));
		}
		
		return res;
	}
	
	
}
