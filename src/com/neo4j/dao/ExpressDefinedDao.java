package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressDefined;
import com.type.datatype.ExpressEnumeration;
import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressSelect;


public class ExpressDefinedDao extends BaseDao {


	/**
	 * 获取ExpressDefined
	 * @param type_decl
	 * @return
	 */
	public ExpressDefined getExpressDefined(Integer type_decl) {
		ExpressDefined expressDefined = new ExpressDefined(type_decl);
		
		Integer type_id = getIdByName(type_decl, "type_id").get(0);
		expressDefined.setDataTypeName(getLeaf(type_id)); 
		
		Integer underlying_type = getIdByName(type_decl, "underlying_type").get(0);
		/* underlying_type : concrete_types | constructed_types */
		Map<String,Object> child =  getDirectChildren(underlying_type).get(0);
		
		if( "constructed_types".equals(child.get("name")) )  //由getConstructTypes()设置数据类型
			getConstructTypes(expressDefined,(Integer)child.get("id"));
		else 
			expressDefined.setDataType(getConcreteTypes((Integer)child.get("id")));
		
		return expressDefined;
	}

	/**
	 * constructed_types : enumeration_type | select_type;
	 * @param constructed_types
	 * @return
	 */
	private void getConstructTypes(ExpressDefined expressDefined,Integer constructed_types) {
		
		Map<String,Object> child =  getDirectChildren(constructed_types).get(0);
		if( "enumeration_type".equals(child.get("name")) )
			expressDefined.setDataType(getExpressEnumeration((Integer)child.get("id")));
		else 
			expressDefined.setDataType(getExpressSelect((Integer)child.get("id")));
		
	}
	
	/**
	 *  解析select
	 * select_type : ( EXTENSIBLE ( GENERIC_ENTITY )? )? SELECT ( select_list | select_extension )?;
	 * @param select_type
	 * @return
	 */
	private ExpressSelect getExpressSelect(Integer select_type) {
		ExpressSelect expSelect = new ExpressSelect(select_type);
		
		if( hasDirectChild(select_type, "EXTENSIBLE")) {
			//TODO 是否是GENERIC_ENTITY
			expSelect.setIsExtensible(true);
		}
		
		//select_list : '(' named_types ( ',' named_types )* ')';
		if(  hasDirectChild(select_type, "select_list")) {
			getSelectList(expSelect,select_type);
		}
		
		//select_extension : BASED_ON type_ref ( WITH select_list )?;
		if(hasDirectChild(select_type, "select_extension")) {
			Integer select_extension = getIdByName(select_type, "select_extension").get(0);
			
			if(  hasDirectChild(select_extension, "select_list")) {
				getSelectList(expSelect,select_type);
			}
			
			Integer type_ref = getIdByName(select_extension, "type_ref").get(0);
			expSelect.setExtension(new ExpressSelect(-1,getLeaf(type_ref)));
			
		}
		return expSelect;
	}

	/**
	 * 获取select type中 的list
	 * @param expSelect
	 * @param select_type
	 */
	private void getSelectList(ExpressSelect expSelect, Integer select_type) {
		Integer select_list = getIdByName(select_type, "select_list").get(0);
		
		List<ExpressGeneralizedDataType> list = new ArrayList<ExpressGeneralizedDataType>();
		List<Integer> named_types_ = getIdByName(select_list, "named_types");
		for (int i = 0; i < named_types_.size(); i++) {
			Integer named_types = named_types_.get(i);
			list.add(getNamedType(named_types));
		}
		expSelect.setList(list);
	}

	/**
	 * 解析enumeration
	 * enumeration_type :
	 *  ( EXTENSIBLE )? ENUMERATION ( ( OF enumeration_items ) | enumeration_extension )?;
	 * @param integer
	 * @return
	 */
	private ExpressEnumeration getExpressEnumeration(Integer enumeration_type) {
		ExpressEnumeration expEnum = new ExpressEnumeration(enumeration_type);
		
		if( hasDirectChild(enumeration_type, "EXTENSIBLE"))
			expEnum.setIsExtensible(true);
		
		//enumeration_extension	: BASED_ON type_ref ( WITH enumeration_items )?;
		if(  hasDirectChild(enumeration_type, "enumeration_extension")) {
			Integer enumeration_extension = getIdByName(enumeration_type, "enumeration_extension").get(0);
			Integer type_ref = getIdByName(enumeration_extension, "type_ref").get(0);
			
			//enumeration_items	: '(' enumeration_id ( ',' enumeration_id )* ')';
			if( hasDirectChild(enumeration_extension,"enumeration_items")) {
				Integer enumeration_items = getIdByName(enumeration_extension,"enumeration_items").get(0);
				
				List<String> items = new ArrayList<String>();
				List<Integer> enumeration_ids = getIdByName(enumeration_items, "enumeration_id");
				for (int i = 0; i < enumeration_ids.size(); i++) {
					Integer enumeration_id = enumeration_ids.get(i);
					items.add(getLeaf(enumeration_id));
				}
				expEnum.setItems(items);
			}
			expEnum.setExtension(new ExpressEnumeration(-1,getLeaf(type_ref)));
		}
		
		return expEnum;
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
			return getTypeRef((Integer)concreteType.get("id"));
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
//		System.out.println(new ExpressDefinedDao().getExpressDefined(180));
		ExpressDefinedDao expDef = new ExpressDefinedDao();
//		ExpressEnumeration expEnum = expDef.getExpressEnumeration(370);
		ExpressGeneralizedDataType expSelect =  expDef.getConcreteTypes(678);
		System.out.println(expSelect);
	}

}
