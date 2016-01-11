package com.neo4j.dao;

import java.util.Map;

import com.type.datatype.ExpressConstructedDataType;
import com.type.datatype.ExpressDefined;
import com.type.datatype.ExpressGeneralizedDataType;


public class ExpressDefinedDao extends BaseDao {

	/**
	 * 获取ExpressDefined
	 * @param type_decl
	 * @return
	 */
	public ExpressDefined getExpressDefined(Integer type_decl) {
		ExpressDefined expressDefined = new ExpressDefined(type_decl);
		
		Integer underlying_type = getIdByName(type_decl, "underlying_type").get(0);
		/* underlying_type : concrete_types | constructed_types */
		Map<String,Object> child =  getDirectChildren(underlying_type).get(0);
		
		if( "constructed_types".equals(child.get("name")) )
			expressDefined.setDataType(getConstructTypes((Integer)child.get("id")));
		else 
			expressDefined.setDataType(getConcreteTypes((Integer)child.get("id")));
		
		return expressDefined;
	}

	private ExpressConstructedDataType getConstructTypes(Integer constructed_types) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * concrete_types: aggregation_types | simple_types | type_ref
	 * @param concrete_types
	 */
	private ExpressGeneralizedDataType getConcreteTypes(Integer concrete_types) {
		Map<String,Object> concreteType = getDirectChildren(concrete_types).get(0);
		
		if( "aggregation_types".equals((String)concreteType.get("name")))
			return getAgregationType( (Integer)concreteType.get("id") );
		else if ( "simple_types".equals((String)concreteType.get("name")))
			return getSimpleDataType((Integer)concreteType.get("id"));
		else
			//FIXME  type_ref 记得修改
			return null;
	}

	private ExpressGeneralizedDataType getAgregationType(Integer integer) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(new ExpressDefinedDao().getExpressDefined(72));
	}

}
