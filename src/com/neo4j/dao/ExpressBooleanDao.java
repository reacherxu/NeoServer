package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressBoolean;
import com.type.instance.BinaryInstance;
import com.type.instance.GeneralizedInstance;

public class ExpressBooleanDao extends BaseDao {
	
	/**
	 * 进行图遍历，查询所有的ExpressBoolean
	 * @return
	 */
	public List<ExpressBoolean> getAllExpressBoolean() {
		List<ExpressBoolean> res = new ArrayList<ExpressBoolean>();
		
		/* 返回boolean_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='boolean_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = this.getNeoConn().queryList(sql);
		
		/* 每个boolean_type创建ExpressBoolean */
		for(int i=0; i<nodeList.size(); i++) {
			int boolean_type = (Integer) nodeList.get(i).get("id");
			
			res.add(getExpressBoolean(boolean_type));
		}
		
		return res;
	}
	
	/**
	 * 根据指定的boolean节点，解析boolean
	 * @param boolean_type
	 * @return
	 */
	public ExpressBoolean getExpressBoolean(Integer boolean_type) {
		return new ExpressBoolean(boolean_type);
	}
	
	/**
	 * 遍历图并寻找BinaryInstance
	 * @return
	 */
	public List<BinaryInstance> getAllExpressBinaryInstances() {
		List<BinaryInstance> binaryInstances = new ArrayList<BinaryInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> binaryInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < binaryInstance.size() ; j++) {
				binaryInstances.add( (BinaryInstance)binaryInstance.get(j)  );
			}
		}
		
		return binaryInstances;
	}
	
	/**
	 * 根据指定的explicit_attr节点，解析boolean instance
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getExpressBooleanInstance(Integer explicit_attr) {
		
		 return getSimpleDataTypeInstance( explicit_attr );
	}
	
	
	public static void main(String[] args) {
		ExpressBooleanDao ins = new ExpressBooleanDao();

		// test ExpressStringInstance
		List<GeneralizedInstance> boolIns = ins.getExpressBooleanInstance(119);
		System.out.println(boolIns);

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
