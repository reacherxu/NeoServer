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
	protected ExpressGeneralizedDataType getConcreteTypes(Integer concrete_types) {
		Map<String,Object> concreteType = getDirectChildren(concrete_types).get(0);
		
		if( "aggregation_types".equals((String)concreteType.get("name")))
			return getAgregationType( (Integer)concreteType.get("id") );
		else if ( "simple_types".equals((String)concreteType.get("name")))
			return getSimpleDataType((Integer)concreteType.get("id"));
		else
			//FIXME  type_ref 记得修改
			return null;
	}

	/**
	 * aggregation_types : array_type | bag_type | list_type | set_type;
	 * @param aggregation_types
	 * @return
	 */
	private ExpressGeneralizedDataType getAgregationType(Integer aggregation_types) {
		Map<String, Object> collectionType = getDirectChildren(aggregation_types).get(0);
		Integer type = (Integer) collectionType.get("id");
		
		if( "array_type".equals( (String)collectionType.get("name")) ) {
			return new ExpressDefinedArrayDao().getExpressArray(type);
		} 
		else if( "list_type".equals( (String)collectionType.get("name"))) {
			return new ExpressDefinedListDao().getExpressList(type);
		} 
		else if( "set_type".equals( (String)collectionType.get("name"))) {
			return new ExpressDefinedSetDao().getExpressSet(type);
		} 
		else {
			return new ExpressDefinedBagDao().getExpressBag(type);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new ExpressDefinedDao().getExpressDefined(180));
	}

}
