package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressReal;
import com.type.instance.GeneralizedInstance;
import com.type.instance.RealInstance;

public class ExpressRealDao extends BaseDao {
	/**
	 * 进行图遍历，查询所有的ExpressReal
	 * @return
	 */
	public List<ExpressReal> getAllExpressReal() {
		List<ExpressReal> res = new ArrayList<ExpressReal>();
		
		/* 返回real_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='real_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = this.getNeoConn().queryList(sql);
		
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
	/**
	 * 遍历图查询所有的RealInstance
	 * @return
	 */
	public List<RealInstance> getExpressRealInstance() {
		List<RealInstance> realInstances = new ArrayList<RealInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> realInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < realInstance.size() ; j++) {
				realInstances.add( (RealInstance)realInstance.get(i)  );
			}
		}
		
		return realInstances;
	}
	
	
	public static void main(String[] args) {
		ExpressRealDao ins = new ExpressRealDao();

		
//		System.out.println(ins.getAllExpressEntity());
//		System.out.println(ins.getExpressRealInstance());
//		System.out.println(ins.getExpressEntity(133));
//		System.out.println(ins.getSimpleDataTypeInstance(149));
//		System.out.println(ins.getVariables(149));
		List<ExpressReal> realList = ins.getAllExpressReal();
		System.out.println(realList);
//		ins.setLocation(83, 20,21,12,13);
//		List<Double> p =  ins.getLocation(195);
//		System.out.println(p);
//		ins.detachDelete(191);
		
//		ins.setProperty(197, "ignore", false);
		ins.logout();

	}
}
