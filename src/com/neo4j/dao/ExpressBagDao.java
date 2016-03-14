package com.neo4j.dao;

import com.type.datatype.ExpressBag;
import com.type.datatype.ExpressGeneralizedDataType;

public class ExpressBagDao extends ExpressSADao {
	private static final int GENERAL_BAG_TYPE = 4;
	
	/**
	 * 根据指定的general_bag_type节点，解析bag
	 * @param general_bag_type
	 * @return
	 */
	public ExpressBag getExpressBag(Integer general_bag_type) {
		Integer bounds[] = {0,null};
		ExpressBag expBag = null;
		ExpressGeneralizedDataType dataType = null;
		
		int parameter_type = getIdByName(general_bag_type, "parameter_type").get(0);
		/* 寻找set类型的叶子节点 ,添加属性 parameter_type : generalized_types | named_types | simple_types; */
		String type = (String) getDirectChildren(parameter_type).get(0).get("name");
		Integer type_id = (Integer) getDirectChildren(parameter_type).get(0).get("id");
		
		if(type.equals("simple_types")) 
			dataType = getSimpleDataType(type_id);
		else if(type.equals("named_types"))
			dataType = getNamedType(type_id);
		else {
			dataType = getGeneralizedType(general_bag_type);
		}
		
		
		/* 判断 bound属性是否存在 */
		if( getDirectChildrenNum(general_bag_type) == GENERAL_BAG_TYPE ) {

			int bound_spec = getIdByName(general_bag_type,"bound_spec").get(0);
			
			
			bounds = getBound(bound_spec);
		}
		expBag = new ExpressBag(general_bag_type, bounds[0], bounds[1], dataType);
		
		return expBag;
	}
	


	public static void main(String[] args) {
		ExpressBagDao bagDao = new ExpressBagDao();
		System.out.println(bagDao.getExpressBag(618));
	}

}
