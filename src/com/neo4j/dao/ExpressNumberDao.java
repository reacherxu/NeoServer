package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressNumber;
import com.type.instance.NumberInstance;
import com.type.instance.GeneralizedInstance;

public class ExpressNumberDao extends BaseDao {
	
	/**
	 * 进行图遍历，查询所有的ExpressNumber
	 * @return
	 */
	public List<ExpressNumber> getAllExpressNumber() {
		List<ExpressNumber> res = new ArrayList<ExpressNumber>();
		
		/* 返回number_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='number_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = this.getNeoConn().queryList(sql);
		
		/* 每个number_type创建ExpressNumber */
		for(int i=0; i<nodeList.size(); i++) {
			int number_type = (Integer) nodeList.get(i).get("id");
			
			res.add(getExpressNumber(number_type));
		}
		
		return res;
	}
	
	/**
	 * 根据指定的number节点，解析number
	 * @param number_type
	 * @return
	 */
	public ExpressNumber getExpressNumber(Integer number_type) {
		return new ExpressNumber(number_type);
	}
	
	/**
	 * 遍历图并寻找NumberInstance
	 * @return
	 */
	public List<NumberInstance> getAllExpressNumberInstances() {
		List<NumberInstance> numberInstances = new ArrayList<NumberInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> numberInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < numberInstance.size() ; j++) {
				numberInstances.add( (NumberInstance)numberInstance.get(j)  );
			}
		}
		
		return numberInstances;
	}
	
	/**
	 * 根据指定的explicit_attr节点，解析number instance
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getExpressNumberInstance(Integer explicit_attr) {
		
		 return getSimpleDataTypeInstance( explicit_attr );
	}
	
	
	public static void main(String[] args) {
		ExpressNumberDao ins = new ExpressNumberDao();

		// test ExpressStringInstance
		List<GeneralizedInstance> numberIns = ins.getExpressNumberInstance(119);
		System.out.println(numberIns);

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
