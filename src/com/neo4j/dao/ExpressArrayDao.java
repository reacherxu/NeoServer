package com.neo4j.dao;

import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressArray;

public class ExpressArrayDao extends BaseDao {
	
	/**
	 * 根据指定的general_array_type节点，解析array
	 * @param general_array_type
	 * @return
	 */
	public ExpressArray<ExpressGeneralizedDataType> getExpressArray(Integer general_array_type) {
		Integer bound1 = 0;
		Integer bound2 = null;
		ExpressArray<ExpressGeneralizedDataType> expArray = null ;
		ExpressGeneralizedDataType dataType = null;
		
		/* 判断 bound属性是否存在 */
		if( hasDirectChild(general_array_type,"bound_spec") ) {
			
			int bound_spec = getIdByName(general_array_type,"bound_spec").get(0);
			int parameter_type = getIdByName(general_array_type, "parameter_type").get(0);
			
			int bound_1 = getIdByName(bound_spec, "bound_1").get(0);
			int bound_2 = getIdByName(bound_spec, "bound_2").get(0);

			/* 寻找bound_1,bound_2的叶子节点 ,添加数值属性 */
			String sql = "start n=node({1}) match (n:Node)-[r:Related_to*0..]->(m:Node) return m.name as name";
			List<Map<String, Object>> bound1Nodes = this.getNeoConn().queryList(sql,bound_1);
			bound1 = Integer.parseInt(bound1Nodes.get(bound1Nodes.size()-1).get("name").toString());
			
			List<Map<String, Object>> bound2Nodes = this.getNeoConn().queryList(sql,bound_2);
			String tmpBound = bound2Nodes.get(bound2Nodes.size()-1).get("name").toString();
			bound2 = tmpBound.equals("?") ? null : Integer.parseInt(tmpBound);
			
			/* 寻找set类型的叶子节点 ,添加属性 parameter_type : generalized_types | named_types | simple_types; */
			String type = (String) getDirectChildren(parameter_type).get(0).get("name");
			Integer type_id = (Integer) getDirectChildren(parameter_type).get(0).get("id");
			
			//TODO  named_types : entity_ref | type_ref;
			if(type.equals("simple_types")) 
				dataType = getSimpleDataType(type_id);
			
		}
		expArray = new ExpressArray<ExpressGeneralizedDataType>(general_array_type, bound1, bound2, dataType);
		
		/* 判断是否有optional unique属性 */
		if(hasDirectChild(general_array_type,"OPTIONAL"))
			expArray.setIsOptional(true);
		if(hasDirectChild(general_array_type,"UNIQUE"))
			expArray.setIsUnique(true);
		
		return expArray;
	}
	

	public static void main(String[] args) {
		ExpressArrayDao arrayDao = new ExpressArrayDao();
		System.out.println(arrayDao.getExpressArray(450));
	}

}
