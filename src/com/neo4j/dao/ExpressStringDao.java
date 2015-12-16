package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressString;
import com.type.instance.GeneralizedInstance;
import com.type.instance.StringInstance;

public class ExpressStringDao extends BaseDao {
	
	/* string_type 的结构 */
	private static final int STRING_TYPE = 2;
	
	private static final int STRING_TYPE_WIDTH_FIXED = 4;
	
	/**
	 * 进行图遍历，查询所有的ExpressString
	 * @return
	 */
	public List<ExpressString> getAllExpressString() {
		List<ExpressString> res = new ArrayList<ExpressString>();
		
		/* 返回string_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='string_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = this.getNeoConn().queryList(sql);
		
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
			List<Map<String, Object>> widthNodes = this.getNeoConn().queryList(sql,width);
			val = Integer.parseInt(widthNodes.get(widthNodes.size()-1).get("name").toString());
			
		}
		expStr = new ExpressString(string_type, val, fixed);
		
		return expStr;
	}
	
	/**
	 * 遍历图并寻找StringInstance
	 * @return
	 */
	public List<StringInstance> getAllExpressStringInstances() {
		List<StringInstance> strInstances = new ArrayList<StringInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> strInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < strInstance.size() ; j++) {
				strInstances.add( (StringInstance)strInstance.get(j)  );
			}
		}
		
		return strInstances;
	}
	
	/**
	 * 根据指定的explicit_attr节点，解析string instance
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getExpressStringInstance(Integer explicit_attr) {
		
		 return getSimpleDataTypeInstance( explicit_attr );
	}
	
	
	public static void main(String[] args) {
		ExpressStringDao ins = new ExpressStringDao();

		// test ExpressStringInstance
		Integer intList[] = {62,72,97};
		for (int i = 0; i < intList.length; i++) {
			List<GeneralizedInstance> strIns = ins.getExpressStringInstance(intList[i]);
			System.out.println(strIns);
		}
		
		/*strIns = ins.getExpressStringInstance(72);
		System.out.println(strIns);
		strIns = ins.getExpressStringInstance(62);
		System.out.println(strIns);*/
//		ExpressString str = ins.getExpressString(79);
//		System.out.println(str);
//		List<ExpressString> strList = ins.getAllExpressString();
//		System.out.println(strList);
		
//		System.out.println(ins.getAllExpressEntity());
//		System.out.println(ins.getExpressRealInstance());
//		System.out.println(ins.getExpressEntity(133));
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
}
