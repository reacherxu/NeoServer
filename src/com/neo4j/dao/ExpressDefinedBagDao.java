package com.neo4j.dao;

import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressBag;

public class ExpressDefinedBagDao extends ExpressDefinedDao {

	/**
	 * 解析entity外的bag
	 * bag_type : BAG ( bound_spec )? OF instantiable_type;
	 * @param bag_type
	 * @return
	 */
	public ExpressBag getExpressBag(Integer bag_type) {
		Integer bound1 = 0;
		Integer bound2 = null;
		ExpressBag expBag = null ;
		ExpressGeneralizedDataType dataType = null;
		
		if( hasDirectChild(bag_type,"bound_spec") ) {
			int bound_spec = getIdByName(bag_type,"bound_spec").get(0);
			int instantiable_type = getIdByName(bag_type, "instantiable_type").get(0);

			int bound_1 = getIdByName(bound_spec, "bound_1").get(0);
			int bound_2 = getIdByName(bound_spec, "bound_2").get(0);

			/* 寻找bound_1,bound_2的叶子节点 ,添加数值属性 */
			bound1 = Integer.parseInt(getLeaf(bound_1));

			String tmpBound = getLeaf(bound_2);
			bound2 = tmpBound.equals("?") ? null : Integer.parseInt(tmpBound);

			/* 寻找array类型的叶子节点 ,添加属性 instantiable_type : concrete_types | entity_ref; */
			String type = (String) getDirectChildren(instantiable_type).get(0).get("name");
			Integer type_id = (Integer) getDirectChildren(instantiable_type).get(0).get("id");

			if(type.equals("concrete_types")) 
				dataType = getConcreteTypes(type_id);
			else
				dataType = getEntityRef(type_id);

		}

		expBag = new ExpressBag(bag_type, bound1, bound2, dataType);
		
		return expBag;
	}
}
