package com.neo4j.dao;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.neo4j.connection.NeoConnection;
import com.neo4j.util.Util;
import com.type.datatype.ExpressArray;
import com.type.datatype.ExpressBag;
import com.type.datatype.ExpressBinary;
import com.type.datatype.ExpressBoolean;
import com.type.datatype.ExpressEntity;
import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressInteger;
import com.type.datatype.ExpressList;
import com.type.datatype.ExpressLogical;
import com.type.datatype.ExpressNumber;
import com.type.datatype.ExpressReal;
import com.type.datatype.ExpressSet;
import com.type.datatype.ExpressString;
import com.type.instance.ArrayInstance;
import com.type.instance.BagInstance;
import com.type.instance.BinaryInstance;
import com.type.instance.BooleanInstance;
import com.type.instance.EntityInstance;
import com.type.instance.GeneralizedInstance;
import com.type.instance.IntegerInstance;
import com.type.instance.ListInstance;
import com.type.instance.LogicalInstance;
import com.type.instance.NumberInstance;
import com.type.instance.RealInstance;
import com.type.instance.SetInstance;
import com.type.instance.StringInstance;

public class BaseDao {
	
	public NeoConnection neoConn ;
	
	//数据的版本信息
	private final String VERSION = "0.1-SNAPSHOT";
	
	public enum Label {
		/**
		 * 正常的树节点
		 */
		Node,
		/**
		 * 用于记录每个节点上操作的日志节点
		 */
		Log,
		/**
		 * 用户操作，暂时无用
		 */
		User
	}

	/*
	 * 构造函数
	 */
	public BaseDao() {
		neoConn = NeoConnection.getNeoConnection();
	}
	

	public String getVERSION() {
		return VERSION;
	}

	public NeoConnection getNeoConn() {
		return neoConn;
	}


	public void setNeoConn(NeoConnection neoConn) {
		this.neoConn = neoConn;
	}
	
	public static void main(String[] args) {
		//"114.212.83.134:7474"  "172.26.13.122:7474"
		BaseDao ins = new BaseDao();
		System.out.println(ins.getDirectChildrenNum(12));
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

		
//		BaseDao dao = new BaseDao();
//		
//		System.out.println(dao.getRoot());
//		Point p1= new Point(1,2);
//		Point p2= new Point(3,4);
//		dao.setLocation(205, p1,p2);
//		Point[] p = dao.getLocation(205);
//		for (int i = 0; i < p.length; i++) {
//			System.out.println(p[i]);
//		}
//		System.out.println(dao.getIdByName(188, "OPTIONAL"));
		
		
		
		ins.logout();
	}
	
	/**
	 * 登出
	 */
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
	
	/**
	 * 查询id节点的name
	 * @param id
	 * @return
	 */
	public String getName(int id) {
		String sql = "start n=node({1}) return n.name as name";
		Map<String, Object> rs = neoConn.query(sql,id);
		return (String) rs.get("name");
	}
	
	/**
	 * 查询图中根节点的id
	 * @return
	 */
	public Integer getRoot() {
		String sql = "start n=node(*) match (n:Node) where n.name='syntax' return ID(n) as id";
		Map<String, Object> rs = neoConn.query(sql);
		return (Integer) rs.get("id");
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
	 * 判断id节点是否有名为property的直接子节点
	 * @param id
	 * @param property
	 * @return
	 */
	public boolean hasDirectChild(Integer id,String property) {
		String sql = "start n=node({1}) match (n:Node)-[]->(m:Node) where m.name='" + property + "' return m.id as id";
		List<Map<String, Object>> result = neoConn.queryList(sql,id);
		return result.size() == 0 ? false : true;
	}
	
	/**
	 *  寻找id的叶子节点 ,添加属性 
	 * @param id
	 * @return
	 */
	public String getLeaf(Integer id) {
		String sql = "start n=node({1}) match (n:Node)-[r:Related_to*0..]->(m:Node) return m.name as name";
		List<Map<String, Object>> nodes = this.getNeoConn().queryList(sql,id);
		return nodes.get(nodes.size()-1).get("name").toString();
	}
	
	/**
	 * 根据simple_types节点   解析基本数据类型
	 * @param simple_types
	 * @return
	 */
	public ExpressGeneralizedDataType getSimpleDataType(Integer simple_types) {
		ExpressGeneralizedDataType simpleDateType = null;
		
		String simpleDataType = (String)getDirectChildren(simple_types).get(0).get("name");

		/* 封装成基本数据类型 */
		/* string_type */
		if( simpleDataType.equals("string_type") ) {
			int string_type = getIdByName(simple_types, "string_type").get(0);

			ExpressStringDao stringDao = new ExpressStringDao();
			simpleDateType = stringDao.getExpressString(string_type);
		} 
		/* binary_type */
		else if(simpleDataType.equals("binary_type")) {
			int binary_type = getIdByName(simple_types, "binary_type").get(0);

			ExpressBinaryDao binaryDao = new ExpressBinaryDao();
			simpleDateType = binaryDao.getExpressBinary(binary_type);
		} 
		/* real_type */
		else if( simpleDataType.equals("real_type") ) {
			int real_type = getIdByName(simple_types, "real_type").get(0);
			
			ExpressRealDao realDao = new ExpressRealDao();
			simpleDateType = realDao.getExpressReal(real_type);
		} 
		/* boolean_type */
		else if( simpleDataType.equals("boolean_type") ) {
			int boolean_type = getIdByName(simple_types, "boolean_type").get(0);

			ExpressBooleanDao boolDao = new ExpressBooleanDao();
			simpleDateType = boolDao.getExpressBoolean(boolean_type);
		}
		/* logical_type */
		else if( simpleDataType.equals("logical_type") ) {
			int logical_type = getIdByName(simple_types, "logical_type").get(0);

			ExpressLogicalDao logicalDao = new ExpressLogicalDao();
			simpleDateType = logicalDao.getExpressLogical(logical_type);

		}
		/* number_type */
		else if( simpleDataType.equals("number_type") ) {
			int number_type = getIdByName(simple_types, "number_type").get(0);

			ExpressNumberDao numberDao = new ExpressNumberDao();
			simpleDateType = numberDao.getExpressNumber(number_type);
		}
		else {
			int integer_type = getIdByName(simple_types, "integer_type").get(0);

			ExpressIntegerDao intDao = new ExpressIntegerDao();
			simpleDateType = intDao.getExpressInteger(integer_type);
		}
		
		return simpleDateType;
	}
	
	
	/**
	 * 获得 某个explicit_attr下的基本数据类型实例
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getSimpleDataTypeInstance(Integer explicit_attr) {
		List<GeneralizedInstance> simpleIns = new ArrayList<GeneralizedInstance>();
		
		int parameter_type = getIdByName(explicit_attr, "parameter_type").get(0);
		
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
	 * 获得 某个explicit_attr下的generalized_type实例
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getGeneralizedTypeInstance(Integer explicit_attr) {
		List<GeneralizedInstance> generalizedIns = new ArrayList<GeneralizedInstance>();

		int parameter_type = getIdByName(explicit_attr, "parameter_type").get(0);
		
		/* generalized_types | named_types | simple_types */
		if( getDirectChildren(parameter_type).get(0).get("name").equals("generalized_types")) {
			Integer generalized_types = getIdByName(parameter_type, "generalized_types").get(0);
			
			/* aggregate_type | general_aggregation_types | generic_entity_type | generic_type */
			if( getDirectChildren(generalized_types).get(0).get("name").equals("general_aggregation_types") ) {
				Integer general_aggregation_types = getIdByName(generalized_types, "general_aggregation_types").get(0);
				
				/* general_array_type | general_bag_type | general_list_type | general_set_type */
				if( getDirectChildren(general_aggregation_types).get(0).get("name").equals("general_set_type")) {
					Integer general_set_type = getIdByName(general_aggregation_types,"general_set_type").get(0);

					ExpressSetDao setDao = new ExpressSetDao();
					ExpressSet<ExpressGeneralizedDataType> tmpSet = setDao.getExpressSet(general_set_type);
					
					List<Map<String,Object>> tmpVars = getVariables(explicit_attr);
					/* 可以申明多个实例 */
					for (int i = 0; i < tmpVars.size(); i++) {
						generalizedIns.add( new SetInstance( (Integer)tmpVars.get(i).get("id"), (String)tmpVars.get(i).get("name"), tmpSet) );
					}
				} 
				else if(getDirectChildren(general_aggregation_types).get(0).get("name").equals("general_bag_type")) {
					Integer general_bag_type = getIdByName(general_aggregation_types,"general_bag_type").get(0);

					ExpressBagDao bagDao = new ExpressBagDao();
					ExpressBag<ExpressGeneralizedDataType> tmpBag = bagDao.getExpressBag(general_bag_type);
					
					List<Map<String,Object>> tmpVars = getVariables(explicit_attr);
					/* 可以申明多个实例 */
					for (int i = 0; i < tmpVars.size(); i++) {
						generalizedIns.add( new BagInstance( (Integer)tmpVars.get(i).get("id"), (String)tmpVars.get(i).get("name"), tmpBag) );
					}
				}
				else if(getDirectChildren(general_aggregation_types).get(0).get("name").equals("general_array_type")) {
					Integer general_array_type = getIdByName(general_aggregation_types,"general_array_type").get(0);

					ExpressArrayDao arrayDao = new ExpressArrayDao();
					ExpressArray<ExpressGeneralizedDataType> tmpArray = arrayDao.getExpressArray(general_array_type);
					
					List<Map<String,Object>> tmpVars = getVariables(explicit_attr);
					/* 可以申明多个实例 */
					for (int i = 0; i < tmpVars.size(); i++) {
						generalizedIns.add( new ArrayInstance( (Integer)tmpVars.get(i).get("id"), (String)tmpVars.get(i).get("name"), tmpArray) );
					}
				}
				else {
					Integer general_list_type = getIdByName(general_aggregation_types,"general_list_type").get(0);

					ExpressListDao listDao = new ExpressListDao();
					ExpressList<ExpressGeneralizedDataType> tmpList = listDao.getExpressList(general_list_type);
					
					List<Map<String,Object>> tmpVars = getVariables(explicit_attr);
					/* 可以申明多个实例 */
					for (int i = 0; i < tmpVars.size(); i++) {
						generalizedIns.add( new ListInstance( (Integer)tmpVars.get(i).get("id"), (String)tmpVars.get(i).get("name"), tmpList) );
					}
				}
			}
		}
		
		return generalizedIns;
	}
	
	/**
	 * 解析对应的entity_ref和type_ref
	 * @param named_types
	 * @return
	 */
	public ExpressGeneralizedDataType getNamedType(Integer named_types) {
		ExpressGeneralizedDataType dataType = null;
		
		String type_name = (String) getDirectChildren(named_types).get(0).get("name");
		if(type_name.equals("entity_ref")) {
			dataType = getEntityRef(named_types);
		} else {
			//TODO　　type_ref
		}
		
		return dataType;
	}
	
	protected ExpressGeneralizedDataType getEntityRef(Integer named_types) {
		String entityName = getLeaf(named_types);
		
		//TODO　 暂时出现实体引用，则new一个id为-1的
		return new ExpressEntity(-1, entityName);
	}


	/**
	 * 获得 某个explicit_attr下的named_type实例
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getNamedTypeInstance(Integer explicit_attr) {
		List<GeneralizedInstance> namedIns = new ArrayList<GeneralizedInstance>();
		String refName = null;
		
		int parameter_type = getIdByName(explicit_attr, "parameter_type").get(0);
		
		if( getDirectChildren(parameter_type).get(0).get("name").equals("named_types") ) {
			/* 寻找named_types的叶子节点 ,添加属性 */
			int named_types = (Integer) getDirectChildren(parameter_type).get(0).get("id");
			ExpressGeneralizedDataType dataType = getNamedType(named_types);

			//TODO  不知道是否是entity
			List<Map<String,Object>> tmpIns = getVariables(explicit_attr);
			/* 可以申明多个实例 */
			for (int i = 0; i < tmpIns.size(); i++) {
				namedIns.add(  new EntityInstance( (Integer)tmpIns.get(i).get("id"), (String)tmpIns.get(i).get("name"), (ExpressEntity) dataType) );
			}

		}
		
		return namedIns;
	}

	/**
	 * 获取实例变量名称
	 * simple_types and derived_attr
	 * @param explicit_attr
	 * @return
	 */
	protected List<Map<String,Object>> getVariables(Integer explicit_attr) {
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
		/* order by 确保得到的子节点是最近的 */
		String sql = "start n=node({1}) match (n:Node)-[r*0..]->(m:Node) where m.name={2} return ID(m) as id order by ID(m)";
		
		List<Map<String, Object>> nameList = neoConn.queryList(sql,id,name);
		
		List<Integer> names = new ArrayList<Integer>();
		for (int i = 0; i < nameList.size(); i++) 
			names.add( (Integer)nameList.get(i).get("id") );
		
		return names;
	}
	
}
