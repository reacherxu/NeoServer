package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;

import com.type.datatype.ExpressReference;

public class ExpressReferenceDao extends BaseDao {
	protected final String REFERENCE = "REFERENCE";
	protected final String USE = "USE";
	
	protected final String ENTITY = "ENTITY";
	protected final String TYPE = "TYPE";
	protected final String CONSTANT = "CONSTANT";
	protected final String FUNCTION = "FUNCTION";
	protected final String PROCEDURE = "PROCEDURE";
	
	public List<ExpressReference> getExpressReference(Integer interface_specification) {
		List<ExpressReference> listRefs = new ArrayList<ExpressReference>();
		
		//interface_specification : reference_clause | use_clause;
		/* interface_specification下可有多个Reference */
		if( hasDirectChild(interface_specification, "reference_clause")) {
			Integer reference_clause = getIdByName(interface_specification,"reference_clause").get(0);
			
			//reference_clause : REFERENCE FROM schema_ref ( '(' resource_or_rename ( ',' resource_or_rename )* ')' )? ';';
			Integer schema_ref = getIdByName(reference_clause,"schema_ref").get(0);
			
			if( hasDirectChild(reference_clause, "resource_or_rename")) {
				List<Integer> resource_or_renames = getIdByName(reference_clause,"resource_or_rename");

				/* 每一个resource_or_renames 才代表一个Reference */
				for (int i = 0; i < resource_or_renames.size(); i++) {
					Integer resource_or_rename = resource_or_renames.get(i);
					ExpressReference expRef = new ExpressReference(resource_or_rename);

					//resource_or_rename : resource_ref ( AS rename_id )?;
					Integer resource_ref = getIdByName(resource_or_rename,"resource_ref").get(0);
					//resource_ref : constant_ref | entity_ref | function_ref | procedure_ref | type_ref;
					String refType = (String) getDirectChildren(resource_ref).get(0).get("name");
					
					Integer rename_id = null;
					if( hasDirectChild(resource_or_rename, "AS")) {
						rename_id = getDirectIdByName(resource_or_rename,"rename_id").get(0);
					}

					/* 设置reference里的属性 */
					expRef.setType(REFERENCE);
					expRef.setSchemaFrom(getLeaf(schema_ref));
					expRef.setDataName(getLeaf(resource_ref));
					if( rename_id != null ) 
						expRef.setAlias(getLeaf(rename_id));
					if( refType.equals("entity_ref"))
						expRef.setReferenceType(ENTITY);
					else if(refType.equals("type_ref"))
						expRef.setReferenceType(TYPE);
					else if(refType.equals("constant_ref"))
						expRef.setReferenceType(CONSTANT);
					else if(refType.equals("function_ref"))
						expRef.setReferenceType(FUNCTION);
					else
						expRef.setReferenceType(PROCEDURE);
					
					listRefs.add(expRef);
				}
			} else {
				ExpressReference expRef = new ExpressReference(schema_ref);
				expRef.setType(REFERENCE);
				expRef.setSchemaFrom(getLeaf(schema_ref));
				listRefs.add(expRef);
			}

		} else {  
			Integer use_clause = getIdByName(interface_specification,"use_clause").get(0);
			
			//use_clause : USE FROM schema_ref ( '(' named_type_or_rename ( ',' named_type_or_rename )* ')' )? ';';
			Integer schema_ref = getIdByName(use_clause,"schema_ref").get(0);
			
			if( hasDirectChild(use_clause, "named_type_or_rename")) {
				List<Integer> named_type_or_renames = getIdByName(use_clause,"named_type_or_rename");
				for (int i = 0; i < named_type_or_renames.size(); i++) {
					Integer named_type_or_rename = named_type_or_renames.get(i);
					ExpressReference expRef = new ExpressReference(named_type_or_rename);
					
					//named_type_or_rename : named_types ( AS ( entity_id | type_id ) )?;
					Integer named_types = getIdByName(named_type_or_rename,"named_types").get(0);
					//named_types : entity_ref | type_ref;
					String refType = (String) getDirectChildren(named_types).get(0).get("name");
					
					Integer alias = null;
					if( hasDirectChild(named_type_or_rename, "entity_id"))
						alias = getDirectIdByName(named_type_or_rename,"entity_id").get(0);
					if( hasDirectChild(named_type_or_rename, "type_id"))
						alias = getDirectIdByName(named_type_or_rename,"type_id").get(0);
					
					/* 设置reference里的属性 */
					expRef.setType(USE);
					expRef.setSchemaFrom(getLeaf(schema_ref));
					expRef.setDataName(getLeaf(named_types));
					if( alias != null ) 
						expRef.setAlias(getLeaf(alias));
					if( refType.equals("entity_ref"))
						expRef.setReferenceType(ENTITY);
					else 
						expRef.setReferenceType(TYPE);
					
					listRefs.add(expRef);
				}
			} else {
				ExpressReference expRef = new ExpressReference(schema_ref);
				expRef.setType(USE);
				expRef.setSchemaFrom(getLeaf(schema_ref));
				listRefs.add(expRef);
			}
		}
		
		return listRefs;
	}
	
	public static void main(String[] args) {
		ExpressReferenceDao refDao = new ExpressReferenceDao();
		List<ExpressReference> list = refDao.getExpressReference(6124);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		
	}
}
