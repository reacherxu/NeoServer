package com.neo4j.dao;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neo4j.connection.NeoConnection;
import com.neo4j.util.Util;
import com.type.datatype.ExpressBinary;
import com.type.datatype.ExpressBoolean;
import com.type.datatype.ExpressInteger;
import com.type.datatype.ExpressLogical;
import com.type.datatype.ExpressNumber;
import com.type.datatype.ExpressReal;
import com.type.datatype.ExpressString;
import com.type.instance.BinaryInstance;
import com.type.instance.BooleanInstance;
import com.type.instance.GeneralizedInstance;
import com.type.instance.IntegerInstance;
import com.type.instance.LogicalInstance;
import com.type.instance.NumberInstance;
import com.type.instance.RealInstance;
import com.type.instance.StringInstance;

public class BaseDao {
	
	public NeoConnection neoConn ;
	
	//数据的版本信息
	private final String VERSION = "0.1-SNAPSHOT";
	//TODO 节点类型
	public enum Label {
		Node,Log,User
	}

	/*
	 * 构造函数
	 */
	public BaseDao() {
		neoConn = NeoConnection.getNeoConnection();
	}

	
	public NeoConnection getNeoConn() {
		return neoConn;
	}


	public void setNeoConn(NeoConnection neoConn) {
		this.neoConn = neoConn;
	}
	
	public static void main(String[] args) {
//		BaseDao ins = new ExpressEntityDao();
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
		
//		System.out.println(ins.getAllExpressEntity());
//		System.out.println(ins.getExpressRealInstance());
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
//		ins.logout();

		
		BaseDao dao = new BaseDao();
		
//		Point p1= new Point(1,2);
//		Point p2= new Point(3,4);
//		dao.setLocation(205, p1,p2);
		Point[] p = dao.getLocation(205);
		for (int i = 0; i < p.length; i++) {
			System.out.println(p[i]);
		}
		
		dao.logout();
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
	public void setLocation(int id,Point... points) {
		StringBuffer location = new StringBuffer("");
		for (int i = 0; i < points.length; i++) {
			location.append(points[i].getX() + "," + points[i].getY() + ";");
		}
		
		String sql = "start n=node("+id+") set n.location={1}";
		neoConn.update(sql, location.toString());
		
		writeLog(id+1,Util.getIP(),Util.getCurrentTime()," revised the location information ");
	}
	
	/*-------------------------查询节点信息--------------------------------------------*/
	
	/**
	 * 获取节点位置信息
	 * @param id
	 * @return
	 */
	public Point[] getLocation(int id) {
		String sql = "start n=node({1}) return n.location as location";
		Map<String, Object> result = neoConn.query(sql,id);
		
		String locStr[] = result.get("location").toString().split(";");
		Point[] location = new Point[locStr.length];
		for (int i = 0; i < location.length; i++) {
			String[] pos = locStr[i].split(",",2);
			location[i] = new Point((int)Double.parseDouble(pos[0].toString()),(int)Double.parseDouble(pos[1].toString()));
		}
		
		return location;
	}
	
	public String getName(int id) {
		String sql = "start n=node({1}) return n.name as name";
		Map<String, Object> rs = neoConn.query(sql,id);
		return (String) rs.get("name");
	}
	
	/**
	 * 返回指定节点的直接子节点的id和name
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getDirectChildren(int id) {
		String sql = "start n=node({1}) match n-[]->(m:" + Label.Node + ") return ID(m) as id,m.name as name order by ID(m)";
		List<Map<String, Object>> result = neoConn.queryList(sql,id);
		return result;
	}
	
	/**
	 * 返回指定节点的全部子节点
	 * order by ID(m)  --->指定是否要排序
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getChildren(int id) {
		String sql = "start n=node({1}) match n-[*1..]->(m:" + Label.Node + ") return ID(m) as id order by ID(m)";
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
		String sql = "start n=node({1}) match n-[*1..]->(m:" + Label.Node + ") return ID(m) as id ";
		List<Map<String, Object>> result = neoConn.queryList(sql,id);
		return result.size();
	}
	
	
	/**
	 * 获得 某个explicit_attr下的基本数据类型实例
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getSimpleDataTypeInstance(Integer explicit_attr) {
		List<GeneralizedInstance> simpleIns = new ArrayList<GeneralizedInstance>();
		
		int parameter_type = getIdByName(explicit_attr, "parameter_type").get(0);
		
		//TODO　　出现自定义类型
		if( getDirectChildren(parameter_type).get(0).get("name").equals("simple_types") ) {
			int simple_types = getIdByName(parameter_type,"simple_types").get(0);
			String simpleDataType = (String)getDirectChildren(simple_types).get(0).get("name");

			/* 封装成基本实例类型 */
			/* string_type */
			if( simpleDataType.equals("string_type") ) {
				int string_type = getIdByName(simple_types, "string_type").get(0);

				ExpressStringDao stringDao = new ExpressStringDao();
				ExpressString tmpStr = stringDao.getExpressString(string_type);

				List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
				/* 可以申明多个实例 */
				for (int i = 0; i < tmpIns.size(); i++) {
					simpleIns.add( new StringInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpStr) );
				}

			} 
			/* binary_type */
			else if(simpleDataType.equals("binary_type")) {
				int binary_type = getIdByName(simple_types, "binary_type").get(0);

				ExpressBinaryDao binaryDao = new ExpressBinaryDao();
				ExpressBinary tmpBinary = binaryDao.getExpressBinary(binary_type);

				List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
				/* 可以申明多个实例 */
				for (int i = 0; i < tmpIns.size(); i++) {
					simpleIns.add( new BinaryInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpBinary) );
				}

			} 
			/* real_type */
			else if( simpleDataType.equals("real_type") ) {
				int real_type = getIdByName(simple_types, "real_type").get(0);
				
				ExpressRealDao realDao = new ExpressRealDao();
				ExpressReal tmpReal = realDao.getExpressReal(real_type);

				List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
				/* 可以申明多个实例 */
				for (int i = 0; i < tmpIns.size(); i++) {
					simpleIns.add( new RealInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpReal) );
				}
			} 
			/* boolean_type */
			else if( simpleDataType.equals("boolean_type") ) {
				int boolean_type = getIdByName(simple_types, "boolean_type").get(0);

				ExpressBooleanDao boolDao = new ExpressBooleanDao();
				ExpressBoolean tmpBool = boolDao.getExpressBoolean(boolean_type);

				List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
				/* 可以申明多个实例 */
				for (int i = 0; i < tmpIns.size(); i++) {
					simpleIns.add( new BooleanInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpBool) );
				}
			}
			/* logical_type */
			else if( simpleDataType.equals("logical_type") ) {
				int logical_type = getIdByName(simple_types, "logical_type").get(0);

				ExpressLogicalDao logicalDao = new ExpressLogicalDao();
				ExpressLogical tmpLogical = logicalDao.getExpressLogical(logical_type);

				List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
				/* 可以申明多个实例 */
				for (int i = 0; i < tmpIns.size(); i++) {
					simpleIns.add( new LogicalInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpLogical) );
				}
			}
			/* number_type */
			else if( simpleDataType.equals("number_type") ) {
				int number_type = getIdByName(simple_types, "number_type").get(0);

				ExpressNumberDao numberDao = new ExpressNumberDao();
				ExpressNumber tmpNumber = numberDao.getExpressNumber(number_type);

				List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
				/* 可以申明多个实例 */
				for (int i = 0; i < tmpIns.size(); i++) {
					simpleIns.add( new NumberInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpNumber) );
				}
			}
			else {
				int integer_type = getIdByName(simple_types, "integer_type").get(0);

				ExpressIntegerDao intDao = new ExpressIntegerDao();
				ExpressInteger tmpInt = intDao.getExpressInteger(integer_type);

				List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
				/* 可以申明多个实例 */
				for (int i = 0; i < tmpIns.size(); i++) {
					simpleIns.add( new IntegerInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), tmpInt) );
				}
			}
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
