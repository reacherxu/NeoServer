package com.neo4j.dao;

import com.type.datatype.ExpressArray;
import com.type.datatype.ExpressGeneralizedDataType;

public class ExpressDefinedArrayDao extends ExpressDefinedDao {

	/**
	 * 解析entity外的array
	 * array_type : ARRAY bound_spec OF ( OPTIONAL )? ( UNIQUE )? instantiable_type;
	 * @param array_type
	 * @return
	 */
	public ExpressArray<ExpressGeneralizedDataType> getExpressArray(Integer array_type) {
		Integer bound1 = 0;
		Integer bound2 = null;
		ExpressArray<ExpressGeneralizedDataType> expArray = null ;
		ExpressGeneralizedDataType dataType = null;
		
		/*-------- 解析属性  --------*/
		int bound_spec = getIdByName(array_type,"bound_spec").get(0);
		int instantiable_type = getIdByName(array_type, "instantiable_type").get(0);

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
		
		/*-------- 结束解析属性  --------*/

		expArray = new ExpressArray<ExpressGeneralizedDataType>(array_type, bound1, bound2, dataType);
		
		/* 判断是否有optional unique属性 */
		if(hasDirectChild(array_type,"OPTIONAL"))
			expArray.setIsOptional(true);
		if(hasDirectChild(array_type,"UNIQUE"))
			expArray.setIsUnique(true);
		
		return expArray;
	}
}


