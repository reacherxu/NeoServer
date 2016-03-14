package com.neo4j.dao;

import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressSet;

public class ExpressSetDao extends ExpressSADao {
	
	/**
	 * 根据指定的general_set_type节点，解析set
	 * @param general_set_type
	 * @return
	 */
	public ExpressSet getExpressSet(Integer general_set_type) {
		Integer bounds[] = {0,null};
		ExpressSet expSet = null;
		ExpressGeneralizedDataType dataType = null;
		
		/* 判断 bound属性是否存在 */
		int parameter_type = getIdByName(general_set_type, "parameter_type").get(0);
		/* 寻找set类型的叶子节点 ,添加属性  */
		String type = (String) getDirectChildren(parameter_type).get(0).get("name");
		Integer type_id = (Integer) getDirectChildren(parameter_type).get(0).get("id");
		
		if(type.equals("simple_types")) 
			dataType = getSimpleDataType(type_id);
		else if(type.equals("named_types"))
			dataType = getNamedType(type_id);
		else {
			dataType = getGeneralizedType(general_set_type);
		}
		
		if( hasDirectChild(general_set_type,"bound_spec") ) {
			int bound_spec = getIdByName(general_set_type,"bound_spec").get(0);
			
			bounds = getBound(bound_spec);
		}
		expSet = new ExpressSet(general_set_type, bounds[0], bounds[1], dataType);
		
		return expSet;
	}
	

	public static void main(String[] args) {
		ExpressSetDao setDao = new ExpressSetDao();
		System.out.println(setDao.getExpressSet(330));
	}

}
