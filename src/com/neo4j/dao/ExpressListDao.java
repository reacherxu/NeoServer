package com.neo4j.dao;

import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressList;

public class ExpressListDao extends BaseDao {
	
	/**
	 * 根据指定的general_list_type节点，解析list
	 * @param general_list_type
	 * @return
	 */
	public ExpressList getExpressList(Integer general_list_type) {
		Integer bound1 = 0;
		Integer bound2 = null;
		ExpressList expList = null ;
		ExpressGeneralizedDataType dataType = null;
		
		int parameter_type = getIdByName(general_list_type, "parameter_type").get(0);
		/* 寻找set类型的叶子节点 ,添加属性 parameter_type : generalized_types | named_types | simple_types; */
		String type = (String) getDirectChildren(parameter_type).get(0).get("name");
		Integer type_id = (Integer) getDirectChildren(parameter_type).get(0).get("id");
		
		if(type.equals("simple_types")) 
			dataType = getSimpleDataType(type_id);
		else if(type.equals("named_types"))
			dataType = getNamedType(type_id);
		else {
			dataType = getGeneralizedType(general_list_type);
		}
		
		/* 判断 bound属性是否存在 */
		if( hasDirectChild(general_list_type,"bound_spec") ) {
			
			int bound_spec = getIdByName(general_list_type,"bound_spec").get(0);
			
			
			int bound_1 = getIdByName(bound_spec, "bound_1").get(0);
			int bound_2 = getIdByName(bound_spec, "bound_2").get(0);

			/* 寻找bound_1,bound_2的叶子节点 ,添加数值属性 */
			bound1 = Integer.parseInt(getLeaf(bound_1));
			
			String tmpBound = getLeaf(bound_2);
			bound2 = tmpBound.equals("?") ? null : Integer.parseInt(tmpBound);
			
		}
		expList = new ExpressList(general_list_type, bound1, bound2, dataType);
		
		/* 判断是否有 unique属性 */
		if(hasDirectChild(general_list_type,"UNIQUE"))
			expList.setIsUnique(true);
		
		return expList;
	}
	

	public static void main(String[] args) {
		ExpressListDao listDao = new ExpressListDao();
		System.out.println(listDao.getExpressList(534));
	}

}
