package com.neo4j.dao;

import com.type.datatype.ExpressBag;
import com.type.datatype.ExpressGeneralizedDataType;

public class ExpressBagDao extends BaseDao {
	private static final int GENERAL_BAG_TYPE = 4;
	
	/**
	 * 根据指定的general_bag_type节点，解析bag
	 * @param general_bag_type
	 * @return
	 */
	public ExpressBag<ExpressGeneralizedDataType> getExpressBag(Integer general_bag_type) {
		Integer bound1 = 0;
		Integer bound2 = null;
		ExpressBag<ExpressGeneralizedDataType> expBag = null;
		ExpressGeneralizedDataType dataType = null;
		
		/* 判断 bound属性是否存在 */
		if( getDirectChildrenNum(general_bag_type) == GENERAL_BAG_TYPE ) {

			int bound_spec = getIdByName(general_bag_type,"bound_spec").get(0);
			int parameter_type = getIdByName(general_bag_type, "parameter_type").get(0);
			
			int bound_1 = getIdByName(bound_spec, "bound_1").get(0);
			int bound_2 = getIdByName(bound_spec, "bound_2").get(0);

			/* 寻找bound_1,bound_2的叶子节点 ,添加数值属性 */
			bound1 = Integer.parseInt(getLeaf(bound_1));
			
			String tmpBound = getLeaf(bound_2);
			bound2 = tmpBound.equals("?") ? null : Integer.parseInt(tmpBound);
			
			/* 寻找set类型的叶子节点 ,添加属性 parameter_type : generalized_types | named_types | simple_types; */
			String type = (String) getDirectChildren(parameter_type).get(0).get("name");
			Integer type_id = (Integer) getDirectChildren(parameter_type).get(0).get("id");
			
			//TODO  named_types : entity_ref | type_ref;
			if(type.equals("simple_types")) 
				dataType = getSimpleDataType(type_id);
		}
		expBag = new ExpressBag<ExpressGeneralizedDataType>(general_bag_type, bound1, bound2, dataType);
		
		return expBag;
	}
	
	public static void main(String[] args) {
		ExpressBagDao bagDao = new ExpressBagDao();
		System.out.println(bagDao.getExpressBag(618));
	}

}
