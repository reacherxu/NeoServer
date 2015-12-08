package com.neo4j.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neo4j.connection.NeoConnection;
import com.neo4j.util.Util;
import com.type.datatype.ExpressEntity;
import com.type.datatype.ExpressReal;
import com.type.datatype.ExpressString;
import com.type.instance.GeneralizedInstance;
import com.type.instance.RealInstance;
import com.type.instance.StringInstance;

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
			ins.createRelationshipTo(nodeB1, nodeC3);*/

//			System.out.println(ins.getDirectChildren(149));
//			System.out.println(ins.getChildren(nodeA));
//			System.out.println(ins.getChildrenNum(nodeA));

//		ExpressString str = ins.getExpressString(191);
//		System.out.println(str);
//		List<ExpressString> strList = ins.getAllExpressString();
//		System.out.println(strList);
		
		System.out.println(ins.getAllExpressEntity());
//		System.out.println(ins.getExpressInstance(133));
//		System.out.println(ins.getSimpleDataTypeInstance(149));
//		System.out.println(ins.getVariables(149));
//		List<ExpressReal> realList = ins.getExpressReal();
//		System.out.println(realList);
//		ins.setLocation(83, 20,21,12,13);
//		List<Double> p =  ins.getLocation(195);
//		System.out.println(p);
//		ins.detachDelete(191);
		
//		ins.setProperty(197, "ignore", false);
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
		String sql = "start n=node({1}) match n-[]->(m:" + Label.Node + ") return ID(m) as id,m.name as name";
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
	 * 遍历图，返回图中所有的entity
	 * @return
	 */
	public List<ExpressEntity> getAllExpressEntity() {
		List<ExpressEntity> entityList = new ArrayList<ExpressEntity>();
		
		/* 返回entity_decl对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='entity_decl' return ID(n) as id";
		List<Map<String, Object>> entities = neoConn.queryList(sql);
		
		for (int i = 0; i < entities.size(); i++) {
			entityList.add( getExpressEntity((Integer)entities.get(i).get("id")) );
		}
		
		return entityList;
	}
	
	/**
	 * 根据指定entity_decl节点，解析entity
	 * @param entity_decl
	 * @return	Class ExpressEntity
	 */
	public ExpressEntity getExpressEntity(Integer entity_decl) {
		String name = null;
		List<Map<GeneralizedInstance,List<String>>> entityBody = new ArrayList<Map<GeneralizedInstance,List<String>>>();
		
		/* 获取entity name */
		int entity_head = getIdByName(entity_decl, "entity_head").get(0);
		int entity_id = getIdByName(entity_head, "entity_id").get(0);
		name = (String) getDirectChildren(entity_id).get(0).get("name");
		
		/* 获取entity 中的变量申明 */
		List<GeneralizedInstance> entity = getExpressInstance(entity_decl);
		for (int i = 0; i < entity.size(); i++) {
			Map<GeneralizedInstance,List<String>> tmpMap = new HashMap<GeneralizedInstance,List<String>>();
			tmpMap.put(entity.get(i), null);
			entityBody.add(tmpMap);
		}
		
		return new ExpressEntity(entity_decl, name, entityBody);
	}
		
	/**
	 * 进行图遍历，查询所有的ExpressString
	 * @return
	 */
	public List<ExpressString> getAllExpressString() {
		List<ExpressString> res = new ArrayList<ExpressString>();
		
		/* 返回string_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='string_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = neoConn.queryList(sql);
		
		/* 每个string_type创建ExpressString */
		for(int i=0; i<nodeList.size(); i++) {
			int string_type = (Integer) nodeList.get(i).get("id");
			
			res.add(getExpressString(string_type));
		}
		
		return res;
	}
	
	/**
	 * 根据指定的string节点，解析string
	 * @param string_type
	 * @return
	 */
	public ExpressString getExpressString(Integer string_type) {
		ExpressString expStr = null;
		Integer val = null;
		Boolean fixed = false;
		

		/* 判断 数值属性是否存在 */
		if( getDirectChildrenNum(string_type) == STRING_TYPE ) {

			int width_spec = getIdByName(string_type,"width_spec").get(0);

			/*　判断 fixed属性是否存在 */
			if( getDirectChildrenNum(width_spec) == STRING_TYPE_WIDTH_FIXED ) {
				fixed = true;
			}

			int width = getIdByName(width_spec,"width").get(0);

			/* 寻找width的叶子节点 ,添加数值属性 */
			String sql = "start n=node({1}) match (n:Node)-[r:Related_to*0..]->(m:Node) return m.name as name";
			List<Map<String, Object>> widthNodes = neoConn.queryList(sql,width);
			val = Integer.parseInt(widthNodes.get(widthNodes.size()-1).get("name").toString());
			
			expStr = new ExpressString(string_type, val, fixed);
		}

		return expStr;
	}
	
	/**
	 * 遍历图并寻找StringInstance
	 * @return
	 */
	public List<StringInstance> getExpressStringInstance() {
		List<StringInstance> strInstances = new ArrayList<StringInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = neoConn.queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> strInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < strInstance.size() ; j++) {
				strInstances.add( (StringInstance)strInstance.get(i)  );
			}
		}
		
		return strInstances;
	}
	
	/**
	 * 寻找指定Entity中 所有的Instance
	 * @return
	 */
	public List<GeneralizedInstance> getExpressInstance(Integer entity_decl) {
		List<GeneralizedInstance> instances = new ArrayList<GeneralizedInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node({1}) match (n:Node)-[*1..]->(m:Node) where m.name='explicit_attr' return ID(m) as id";
		List<Map<String, Object>> explicit_attrs = neoConn.queryList(sql,entity_decl);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> tmpInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < tmpInstance.size() ; j++) {
				instances.add( tmpInstance.get(j)  );
			}
		}
		
		return instances;
	}
	
	/**
	 * 进行图遍历，查询所有的ExpressReal
	 * @return
	 */
	public List<ExpressReal> getAllExpressReal() {
		List<ExpressReal> res = new ArrayList<ExpressReal>();
		
		/* 返回real_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='real_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = neoConn.queryList(sql);
		
		/* 每个real_type创建ExpressReal */
		for(int i=0; i<nodeList.size(); i++) {
			int real_type = (Integer) nodeList.get(i).get("id");
			
			res.add(getExpressReal(real_type));
		}
		
		return res;
	}

	/**
	 * 获取ExpressReal
	 * @param real_type
	 * @return
	 */
	public ExpressReal getExpressReal(Integer real_type) {
		//TODO　看有精度的时候是怎么样的　
		return new ExpressReal(real_type);
	}
	
	//TODO　any use?
	public List<RealInstance> getExpressRealInstance() {
		List<RealInstance> realInstances = new ArrayList<RealInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = neoConn.queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			//TODO
		}
		
		return realInstances;
	}

	/**
	 * 获得 某个explicit_attr下的基本数据类型实例
	 * @param explicit_attr
	 * @return
	 */
	private List<GeneralizedInstance> getSimpleDataTypeInstance(Integer explicit_attr) {
		List<GeneralizedInstance> simpleIns = new ArrayList<GeneralizedInstance>();
		
		int parameter_type = getIdByName(explicit_attr, "parameter_type").get(0);
		int simple_types = getIdByName(parameter_type,"simple_types").get(0);
		String simpleDataType = (String)getDirectChildren(simple_types).get(0).get("name");
		
		/* 封装成基本实例类型 */
		if( simpleDataType.equals("string_type") ) {
			int string_type = getIdByName(simple_types, "string_type").get(0);
			ExpressString tmpStr = getExpressString(string_type);

			List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
			/* 可以申明多个实例 */
			for (int i = 0; i < tmpIns.size(); i++) {
				simpleIns.add( new StringInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpStr) );
			}
			
		} else if( simpleDataType.equals("real_type") ) {
			ExpressReal tmpReal = getExpressReal(simple_types);

			List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
			 /* 可以申明多个实例 */
			for (int i = 0; i < tmpIns.size(); i++) {
				simpleIns.add( new RealInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpReal) );
			}
		} else {
			//TODO	其他数据类型
		}
		
		return simpleIns;
	}
	
	/**
	 * 获取实例变量名称
	 * @param explicit_attr
	 * @return
	 */
	private List<Map<String,Object>> getVariables(Integer explicit_attr) {
		List<Map<String,Object>> variableNames = new ArrayList<Map<String,Object>>();
		
		/* 可能有多个attribute_decl */
		List<Integer> attribute_decl = getIdByName(explicit_attr, "attribute_decl");
		
		for (int i = 0; i < attribute_decl.size(); i++) {
			int attribute_id = getIdByName(attribute_decl.get(i), "attribute_id").get(0);
			
			List<Map<String, Object>> tmpVariables = getDirectChildren(attribute_id);
			variableNames.add( tmpVariables.get(0) );
		}
		
		return variableNames;
	}
	
	/**
	 * 返回某节点 名字为name的节点id(可以是多个节点)
	 * @param id
	 * @param name
	 * @return
	 */
	public List<Integer> getIdByName(int id,String name) {
		String sql = "start n=node({1}) match (n:Node)-[r*0..]->(m:Node) where m.name={2} return ID(m) as id";
		
		List<Map<String, Object>> nameList = neoConn.queryList(sql,id,name);
		
		List<Integer> names = new ArrayList<Integer>();
		for (int i = 0; i < nameList.size(); i++) 
			names.add( (Integer)nameList.get(i).get("id") );
		
		return names;
	}
	
	
}
