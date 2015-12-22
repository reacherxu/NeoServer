package com.neo4j.dao;

import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressSet;

public class ExpressSetDao extends BaseDao {
	private static final int GENERAL_SET_TYPE = 4;
	
	/**
	 * 根据指定的general_set_type节点，解析set
	 * @param general_set_type
	 * @return
	 */
	public ExpressSet<ExpressGeneralizedDataType> getExpressSet(Integer general_set_type) {
		Integer bound1 = 0;
		Integer bound2 = null;
		ExpressSet<ExpressGeneralizedDataType> expSet = null;
		ExpressGeneralizedDataType dataType = null;
		
		/* 判断 bound属性是否存在 */
		if( getDirectChildrenNum(general_set_type) == GENERAL_SET_TYPE ) {

			int bound_spec = getIdByName(general_set_type,"bound_spec").get(0);
			int parameter_type = getIdByName(general_set_type, "parameter_type").get(0);
			
			int bound_1 = getIdByName(bound_spec, "bound_1").get(0);
			int bound_2 = getIdByName(bound_spec, "bound_2").get(0);

			/* 寻找bound_1,bound_2的叶子节点 ,添加数值属性 */
			String sql = "start n=node({1}) match (n:Node)-[r:Related_to*0..]->(m:Node) return m.name as name";
			List<Map<String, Object>> bound1Nodes = this.getNeoConn().queryList(sql,bound_1);
			bound1 = Integer.parseInt(bound1Nodes.get(bound1Nodes.size()-1).get("name").toString());
			
			List<Map<String, Object>> bound2Nodes = this.getNeoConn().queryList(sql,bound_2);
			String tmpBound = bound2Nodes.get(bound2Nodes.size()-1).get("name").toString();
			bound2 = tmpBound.equals("?") ? null : Integer.parseInt(tmpBound);
			
			/* 寻找set类型的叶子节点 ,添加属性  */
			String type = (String) getDirectChildren(parameter_type).get(0).get("name");
			Integer type_id = (Integer) getDirectChildren(parameter_type).get(0).get("id");
			
			//TODO parameter_type : generalized_types | named_types | simple_types;
			if(type.equals("simple_types")) 
				dataType = getSimpleDataType(type_id);
			//TODO  named_types : entity_ref | type_ref;
			else if(type.equals("named_types"))
				dataType = getNamedType(type_id);
		}
		expSet = new ExpressSet<ExpressGeneralizedDataType>(general_set_type, bound1, bound2, dataType);
		
		return expSet;
	}
	
	public static void main(String[] args) {
		ExpressSetDao setDao = new ExpressSetDao();
		System.out.println(setDao.getExpressSet(698));
	}

}
