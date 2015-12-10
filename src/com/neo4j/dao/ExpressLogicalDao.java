package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressLogical;
import com.type.instance.LogicalInstance;
import com.type.instance.GeneralizedInstance;

public class ExpressLogicalDao extends BaseDao {
	
	/**
	 * 进行图遍历，查询所有的ExpressLogical
	 * @return
	 */
	public List<ExpressLogical> getAllExpressLogical() {
		List<ExpressLogical> res = new ArrayList<ExpressLogical>();
		
		/* 返回logical_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='logical_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = this.getNeoConn().queryList(sql);
		
		/* 每个logical_type创建ExpressLogical */
		for(int i=0; i<nodeList.size(); i++) {
			int logical_type = (Integer) nodeList.get(i).get("id");
			
			res.add(getExpressLogical(logical_type));
		}
		
		return res;
	}
	
	/**
	 * 根据指定的logical节点，解析logical
	 * @param logical_type
	 * @return
	 */
	public ExpressLogical getExpressLogical(Integer logical_type) {
		return new ExpressLogical(logical_type);
	}
	
	/**
	 * 遍历图并寻找ExpressLogical
	 * @return
	 */
	public List<LogicalInstance> getAllExpressLogicalInstances() {
		List<LogicalInstance> logicalInstances = new ArrayList<LogicalInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> logicalInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < logicalInstance.size() ; j++) {
				logicalInstances.add( (LogicalInstance)logicalInstance.get(j)  );
			}
		}
		
		return logicalInstances;
	}
	
	/**
	 * 根据指定的explicit_attr节点，解析logical instance
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getExpressLogicalInstance(Integer explicit_attr) {
		
		 return getSimpleDataTypeInstance( explicit_attr );
	}
	
	
	public static void main(String[] args) {
		ExpressLogicalDao ins = new ExpressLogicalDao();

		// test ExpressStringInstance
		List<GeneralizedInstance> boolIns = ins.getExpressLogicalInstance(119);
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
