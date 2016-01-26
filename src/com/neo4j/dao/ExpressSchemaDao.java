package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressSchema;

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
			schemaList.add( getExpressSchema((Integer)schemas.get(i).get("id")) );
		}

		return schemaList;
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
					schema.getEntities().add(entityDao.getExpressEntity(entity_decl));
				}
				else if("type_decl".equals(member.get(0).get("name").toString()) ) {
					Integer type_decl = (Integer) member.get(0).get("id");
					
					ExpressDefinedDao definedDao = new ExpressDefinedDao();
					schema.getDefinedDataType().add( definedDao.getExpressDefined(type_decl));
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
		System.out.println(new ExpressSchemaDao().getExpressSchema(762));
		
	}

}
