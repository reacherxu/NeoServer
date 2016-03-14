package com.neo4j.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.type.datatype.ExpressAggregation;
import com.type.datatype.ExpressDefined;
import com.type.datatype.ExpressEntity;
import com.type.datatype.ExpressEnumeration;
import com.type.datatype.ExpressGeneralizedDataType;
import com.type.datatype.ExpressSchema;
import com.type.datatype.ExpressSelect;
import com.type.instance.GeneralizedInstance;

public class ExpressSchemaDao extends BaseDao {

	/**
	 * 遍历图，返回图中所有的schema
	 * @return
	 */
	public List<ExpressSchema> getAllExpressSchema() {
		List<ExpressSchema> schemaList = new ArrayList<ExpressSchema>();

		/* 返回schema_decl对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='schema_decl' return ID(n) as id";
		List<Map<String, Object>> schemas = this.getNeoConn().queryList(sql);

		for (int i = 0; i < schemas.size(); i++) {
			ExpressSchema tmpSchema = getExpressSchema((Integer)schemas.get(i).get("id"));
			schemaList.add( addDefinedRef(tmpSchema) );
		}

		return schemaList;
	}
	
	/**
	 * 遍历schema,entity中加入defined datatype的引用
	 * @param tmpSchema
	 * @return
	 */
	private ExpressSchema addDefinedRef(ExpressSchema tmpSchema) {
		List<ExpressDefined> defs = tmpSchema.getDefinedDataType();
		List<ExpressEntity> entities = tmpSchema.getEntities();
		
		/* 遍历每一个entity */
		for (int i = 0; i < entities.size(); i++) {
			ExpressEntity tmpEntity = entities.get(i);
			Set<GeneralizedInstance> set = tmpEntity.getMap().keySet();
			
			 /* 遍历entity中的每个instance */
			Iterator<GeneralizedInstance> it=set.iterator();
			while ( it.hasNext() ) {
				GeneralizedInstance ins = it.next();
				
				if( ins.dataType instanceof ExpressDefined) {
					ExpressDefined def = (ExpressDefined)ins.dataType;
					ins.setDataType( getDefinedType(defs, def.getDataTypeName()) );
				}
				if( ins.dataType instanceof ExpressEnumeration) {
					ExpressEnumeration tmpEnum = (ExpressEnumeration)ins.dataType;
					ins.setDataType( getDefinedType(defs, tmpEnum.getName()) );
				}
				if( ins.dataType instanceof ExpressSelect) {
					ExpressSelect tmpSelect = (ExpressSelect)ins.dataType;
					ins.setDataType( getDefinedType(defs, tmpSelect.getName()) );
				} 
				
				/* 数组中的类型 */
				//TODO　数组是范型的，得不到名字
				if(ins.dataType instanceof ExpressAggregation) {
					/* aggregation level*/
					ExpressAggregation aggType = (ExpressAggregation)ins.dataType;
					/* into aggregation level*/
					ExpressGeneralizedDataType intoAggType = aggType.getDataType();
					if(intoAggType instanceof ExpressDefined) {
						ExpressDefined ed = (ExpressDefined)intoAggType;
						((ExpressDefined) intoAggType).setDataType(getDefinedType(defs, ed.getDataTypeName() ));
					}
				}
			}
		}
		return tmpSchema;
	}


	private ExpressGeneralizedDataType getDefinedType(
			List<ExpressDefined> defs, String dataTypeName) {
		for (int i = 0; i < defs.size(); i++) {
			if(defs.get(i).getDataTypeName().equals(dataTypeName))
				return defs.get(i);
		}
		return null;
	}

	/**
	 * 根据schema_decl，解析schema
	 * @param schema_decl
	 * @return
	 */
	private ExpressSchema getExpressSchema(Integer schema_decl) {
		ExpressSchema schema = new ExpressSchema(schema_decl);
		
		/* 为schema设置名字 */
		Integer schema_id = getIdByName(schema_decl, "schema_id").get(0);
		schema.setName(getLeaf(schema_id));
		
		/* 为schema设置其他属性 */
		Integer schema_body = getIdByName(schema_decl, "schema_body").get(0);
		
		//( interface_specification )* ( constant_decl )? ( declaration | rule_decl )*;
		if( hasDirectChild(schema_body, "declaration") ) {
			List<Integer> declarations =  getIdByName(schema_body,"declaration");
			for (int i = 0; i < declarations.size(); i++) {
				
				Integer declaration = declarations.get(i);
				List<Map<String, Object>> member = getDirectChildren(declaration);
				
				/*entity_decl | function_decl | procedure_decl | subtype_constraint_decl | type_decl;*/
				if( "entity_decl".equals(member.get(0).get("name").toString() )){
					Integer entity_decl = (Integer) member.get(0).get("id");
					
					ExpressEntityDao entityDao = new ExpressEntityDao();
					ExpressEntity tmpEntity = entityDao.getExpressEntity(entity_decl);
					schema.getEntities().add(tmpEntity);
				}
				else if("type_decl".equals(member.get(0).get("name").toString()) ) {
					Integer type_decl = (Integer) member.get(0).get("id");
					
					ExpressDefinedDao definedDao = new ExpressDefinedDao();
					ExpressDefined tmpdef = definedDao.getExpressDefined(type_decl);
					schema.getDefinedDataType().add( tmpdef );
				}
				else if("function_decl".equals(member.get(0).get("name").toString())) {
					//TODO function_decl
				}
				else if("procedure_decl".equals(member.get(0).get("name").toString())) {
					//TODO procedure_decl
				}
				else {
					//TODO subtype_constraint_decl
				}
			}
			
		}
		return schema;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExpressSchemaDao es = new ExpressSchemaDao();
		List<ExpressSchema> tmpSchema = es.getAllExpressSchema();
		System.out.println(tmpSchema);
		es.logout();
		
		
	}

}
