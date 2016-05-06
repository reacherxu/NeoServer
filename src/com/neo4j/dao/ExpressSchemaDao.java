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
import com.type.datatype.ExpressReference;
import com.type.datatype.ExpressSchema;
import com.type.datatype.ExpressSelect;
import com.type.instance.GeneralizedInstance;

public class ExpressSchemaDao extends BaseDao {
	
	protected final String ENTITY = "ENTITY";
	protected final String TYPE = "TYPE";
	
	public ExpressSchemaDao() {}
	
	public ExpressSchemaDao(String ip) {
		super(ip);
	}

	/**
	 * 遍历图，返回图中所有的schema
	 * @return
	 */
	public List<ExpressSchema> getAllExpressSchema() {
		List<ExpressSchema> schemaList = new ArrayList<ExpressSchema>();

		/* 返回schema_decl对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='schema_decl' return ID(n) as id";
		List<Map<String, Object>> schemas = this.getNeoConn().queryList(sql);
		
		/* schema本身先自身刷新 */
		//TODO  defined data type 的id要不要更新
		for (int i = 0; i < schemas.size(); i++) {
			ExpressSchema tmpSchema = getExpressSchema((Integer)schemas.get(i).get("id"));
			schemaList.add( addDefinedRef(tmpSchema) );
		}
		/* 刷新schema中的外部引用  */
		addExternalRef(schemaList);
		
		return schemaList;
	}

	/**
	 * 遍历schema,entity中加入defined datatype的引用
	 * @param tmpSchema
	 * @return
	 */
	private ExpressSchema addDefinedRef(ExpressSchema tmpSchema) {
		/* 用于设置所有元素的名字 */
		String schemaName = tmpSchema.getName();
		
		List<ExpressDefined> defs = tmpSchema.getDefinedDataType();
		List<ExpressEntity> entities = tmpSchema.getEntities();
		List<ExpressReference> refs = tmpSchema.getRefenences();
		
		/* 遍历每一个reference,设置schema name*/
		for (int i = 0; i < refs.size(); i++) {
			ExpressReference ref = refs.get(i);
			ref.setSchemaName(schemaName);
		}

		/* 遍历每一个defined data type,处理defined datatype 嵌套defined datatype*/
		for (int i = 0; i < defs.size(); i++) {
			ExpressDefined def = defs.get(i);
			
			/* 设置所有defined data type的名字 */
			def.setSchemaName(schemaName);

			/* 设置所有 data type的名字 */
			ExpressGeneralizedDataType defDataType = def.getDataType();
			defDataType.setSchemaName(schemaName);
			
			/* 若defined data type 起其他名字 */
			if(defDataType instanceof ExpressDefined) {
				ExpressDefined ed = (ExpressDefined)defDataType;
				
				ExpressGeneralizedDataType dt = getDefinedType(defs, ed.getDataTypeName() );
				if(null != dt) {
					ed.setDataType(dt);
					ed.getDataType().setSchemaName(schemaName);
				}
			}
			
			/* 若是数组类型 */
			if(defDataType instanceof ExpressAggregation) {
				/* aggregation level*/
				ExpressAggregation aggType = (ExpressAggregation)defDataType;
				/* into aggregation level*/
				ExpressGeneralizedDataType basicType = resolveBasicType(aggType);
				if(basicType instanceof ExpressDefined) {
					ExpressDefined ed = (ExpressDefined)basicType;

					ExpressGeneralizedDataType dt = getDefinedType(defs, ed.getDataTypeName() );
					if(null != dt) {
						ed.setDataType(dt);
						ed.getDataType().setSchemaName(schemaName);
					}
				}
			}

			/* 若是select当中嵌套 */
			if(defDataType instanceof ExpressSelect) {
				ExpressSelect selectType = (ExpressSelect)defDataType;

				/* 遍历select当中的成员列表 */
				List<ExpressGeneralizedDataType> list = selectType.getList();
				for (int j = 0; j < list.size(); j++) {
					if(list.get(j) instanceof ExpressDefined) {
						ExpressDefined member = (ExpressDefined)list.get(j);
						
						ExpressGeneralizedDataType dt = getDefinedType(defs, member.getDataTypeName() );
						if(null != dt) {
							member.setDataType(dt);
							member.getDataType().setSchemaName(schemaName);
						}
					}
					
					//TODO select当中entity是没有schemaName的
				}

			}
		}

		/* 遍历每一个entity */
		for (int i = 0; i < entities.size(); i++) {
			ExpressEntity tmpEntity = entities.get(i);
			
			/* 设置所有entity的名字 */
			tmpEntity.setSchemaName(schemaName);
			
			Set<GeneralizedInstance> set = tmpEntity.getMap().keySet();

			/* 遍历entity中的每个instance */
			Iterator<GeneralizedInstance> it=set.iterator();
			while ( it.hasNext() ) {
				GeneralizedInstance ins = it.next();
				
				/* 设置所有entity当中属性的名字 */
				ins.setSchemaName(schemaName);
				/* 设置所有entity当中data type的名字 */
				ins.getDataType().setSchemaName(schemaName);

				if( ins.dataType instanceof ExpressDefined) {
					ExpressDefined def = (ExpressDefined)ins.dataType;
					
					ExpressGeneralizedDataType dt = getDefinedType(defs, def.getDataTypeName() );
					if(null != dt) {
						def.setDataType(dt);
						def.getDataType().setSchemaName(schemaName);
					}
				}
				if( ins.dataType instanceof ExpressEnumeration) {
					ExpressEnumeration tmpEnum = (ExpressEnumeration)ins.dataType;
					ins.setDataType( getDefinedType(defs, tmpEnum.getName()) );
					ins.getDataType().setSchemaName(schemaName);
				}
				if( ins.dataType instanceof ExpressSelect) {
					ExpressSelect tmpSelect = (ExpressSelect)ins.dataType;
					ins.setDataType( getDefinedType(defs, tmpSelect.getName()) );
					ins.getDataType().setSchemaName(schemaName);
				} 

				/* 数组中的类型 */
				if(ins.dataType instanceof ExpressAggregation) {
					
					/* aggregation level*/
					ExpressAggregation aggType = (ExpressAggregation)ins.dataType;
					
					/* into aggregation level*/
					ExpressGeneralizedDataType basicType = resolveBasicType(aggType);
					if(basicType instanceof ExpressDefined) {
						ExpressDefined ed = (ExpressDefined)basicType;
						
						ExpressGeneralizedDataType dt = getDefinedType(defs, ed.getDataTypeName() );
						if(null != dt) {
							ed.setDataType(dt);
							ed.getDataType().setSchemaName(schemaName);
						}
					}
				}
			}
		}
		return tmpSchema;
	}
	
	/**
	 * 解析到数组当中的basic type
	 * @param aggType
	 */
	private ExpressGeneralizedDataType resolveBasicType(ExpressAggregation aggType) {
		ExpressAggregation dataType = aggType;
		String schemaName = aggType.getSchemaName();
		
		while( dataType.getDataType() instanceof ExpressAggregation) {
			dataType = (ExpressAggregation) dataType.getDataType();
			dataType.setSchemaName(schemaName);
		}
		return dataType.getDataType();
	}

	/**
	 * 解决schema中的引用
	 * 包括schema引用和entity引用
	 * @param schemaList
	 */
	private void addExternalRef(List<ExpressSchema> schemaList) {
		for (int i = 0; i < schemaList.size(); i++) {
			ExpressSchema schema = schemaList.get(i);
			
			List<ExpressDefined> defs = schema.getDefinedDataType();
			List<ExpressEntity> entities = schema.getEntities();
			
			/* 遍历每一个defined data type,处理defined datatype 嵌套defined datatype */
			for (int j = 0; j < defs.size(); j++) {
				ExpressDefined def = defs.get(j);
				
				ExpressGeneralizedDataType defDataType = def.getDataType();
				defDataType.setSchemaName(schema.getName());
				
				/* 若defined data type 起其他名字 */
				if(defDataType instanceof ExpressDefined ) {
					ExpressDefined ed = (ExpressDefined)defDataType;
					if( ed.getDataType() == null ) {
						defDataType = getReference(schemaList, schema, ed.getDataTypeName(), TYPE);
					}
				}
				
				/* 若是其他schema中的entity */
				if(defDataType instanceof ExpressEntity) {
					ExpressEntity ee = (ExpressEntity)defDataType;
					if( !hasEntity(entities, ee.getName()) ) {
						defDataType = getReference(schemaList, schema, ee.getName(), ENTITY);
						defDataType.setSchemaName(schema.getName());
					}
				}
				
				
				/* 若是数组类型 */
				if(defDataType instanceof ExpressAggregation) {
					/* aggregation level*/
					ExpressAggregation aggType = (ExpressAggregation)defDataType;
					/* into aggregation level*/
					ExpressGeneralizedDataType basicType = resolveBasicType(aggType);
					if(basicType instanceof ExpressDefined) {
						ExpressDefined ed = (ExpressDefined)basicType;
						if( ed.getDataType() == null ) {
							changeDataType(aggType, getReference(schemaList, schema, ed.getDataTypeName(), TYPE));
						}
					}
					
					if(basicType instanceof ExpressEntity) {
						ExpressEntity ee = (ExpressEntity)basicType;
						if( !hasEntity(entities, ee.getName()) ) {
							basicType = getReference(schemaList, schema, ee.getName(), ENTITY);
							basicType.setSchemaName(schema.getName());
						}
					}
				}

				/* 若是select当中嵌套 */
				if(defDataType instanceof ExpressSelect) {
					ExpressSelect selectType = (ExpressSelect)defDataType;

					/* 遍历select当中的成员列表 */
					List<ExpressGeneralizedDataType> list = selectType.getList();
					for (int k = 0; k < list.size(); k++) {
						if(list.get(k) instanceof ExpressDefined) {
							ExpressDefined member = (ExpressDefined)list.get(k);
							if( member.getDataType() == null ) {
								list.set(k, getReference(schemaList, schema, member.getDataTypeName(), TYPE));
//								member.setDataType(getReference(schemaList, schema, member.getDataTypeName(), TYPE));
//								member.getDataType().setSchemaName(schema.getName());
							}
						}
						
						if(list.get(k) instanceof ExpressEntity) {
							ExpressEntity ee = (ExpressEntity)list.get(k);
							ee.setSchemaName(schema.getName());
							
							if( !hasEntity(entities, ee.getName()) ) {
								list.set(k, getReference(schemaList, schema, ee.getName(), ENTITY));
							}
						}
					}
				}
			}
			
			/* 遍历每一个entity */
			for (int j = 0; j < entities.size(); j++) {
				ExpressEntity tmpEntity = entities.get(j);
				
				Set<GeneralizedInstance> set = tmpEntity.getMap().keySet();

				/* 遍历entity中的每个instance */
				Iterator<GeneralizedInstance> it=set.iterator();
				while ( it.hasNext() ) {
					GeneralizedInstance ins = it.next();
//					if(ins instanceof DefinedInstance) {
//						if(ins.getDataType() == null)
//							ins.setDataType( getReference(schemaList, schema, ins.getDataTypeName(), TYPE) );
//					}
					if( ins.dataType instanceof ExpressDefined) {
						ExpressDefined def = (ExpressDefined)ins.dataType;
						if(def.getDataType() == null) {
							ins.dataType = getReference(schemaList, schema, def.getDataTypeName(), TYPE);
							ins.getDataType().setSchemaName(schema.getName());
						}
					}
					if(ins.dataType instanceof ExpressEntity) {
						ExpressEntity ee = (ExpressEntity)ins.dataType;
						if( !hasEntity(entities, ee.getName()) ) {
							ins.dataType = getReference(schemaList, schema, ee.getName(), ENTITY);
							ins.dataType.setSchemaName(schema.getName());
						}
					}

					/* 数组中的类型 */
					if(ins.dataType instanceof ExpressAggregation) {
						/* aggregation level*/
						ExpressAggregation aggType = (ExpressAggregation)ins.dataType;
						/* into aggregation level*/
						ExpressGeneralizedDataType basicType = resolveBasicType(aggType);
						if(basicType instanceof ExpressDefined) {
							ExpressDefined ed = (ExpressDefined)basicType;
							if(ed.getDataType() == null) {
								changeDataType(aggType, getReference(schemaList, schema, ed.getDataTypeName(), TYPE));
							}
						} else if(basicType instanceof ExpressEntity) {
							ExpressEntity ee = (ExpressEntity)basicType;
							if( !hasEntity(entities, ee.getName()) ) {
								basicType = getReference(schemaList, schema, ee.getName(), ENTITY);
								basicType.setSchemaName(schema.getName());
							}
						}
					}
				}
			}
		}
	}


	/**
	 * 若数组中的底层数据类型是defined data type，且是外部引用，则直接赋值为ExpressReference
	 * @param aggType
	 * @param reference
	 */
	private void changeDataType(ExpressAggregation aggType,
			ExpressReference reference) {
		ExpressAggregation dataType = aggType;
		String schemaName = aggType.getSchemaName();
		
		while( dataType.getDataType() instanceof ExpressAggregation) {
			dataType = (ExpressAggregation) dataType.getDataType();
			dataType.setSchemaName(schemaName);
		}
		dataType.setDataType(reference);
	}

	/**
	 * 判断当前schema中是否含有entity,有则true
	 * @param schema
	 * @param name
	 * @return
	 */
	private boolean hasEntity(List<ExpressEntity> entities, String name) {
		for (int i = 0; i < entities.size(); i++) {
			if(entities.get(i).getName().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * 在schemaList中查找来自哪个schema
	 * @param schemaList
	 * @param currentSchema
	 * @param refName
	 * @param refType 是entity还是defined data type
	 * @return
	 */
	private ExpressReference getReference(
			List<ExpressSchema> schemaList,ExpressSchema currentSchema, 
			String refName, String refType) {
		//在当前schema中的references里面找
		List<ExpressReference> curRefs = currentSchema.getRefenences();
		for (int i = 0; i < curRefs.size(); i++) {
			ExpressReference curRef = curRefs.get(i);
			
			/* 为外部引用添加schemaName */
			curRef.setSchemaName(currentSchema.getName());
			
			//若是别名
			if( refName.equals(curRef.getAlias()) ) {
				curRef.setReferenceType(refType);
				return curRef;
			}
			
			//否则，去schema中寻找
			String schemaName = curRef.getSchemaFrom();
			ExpressSchema schema = getSchemaByName(schemaList, schemaName);
			if( hasSchema(schema, refName))
				return new ExpressReference(-1, schemaName, refName, curRef.getType(), refType);
		}
		return null;
	}

	/**
	 * 根据schema的名字寻找schema
	 * @param schemaList
	 * @param schemaName
	 * @return
	 */
	private ExpressSchema getSchemaByName(List<ExpressSchema> schemaList,
			String schemaName) {
		for (int i = 0; i < schemaList.size(); i++) {
			ExpressSchema schema = schemaList.get(i);
			if(schema.getName().equals(schemaName))
				return schema;
		}
		return null;
	}

	/**
	 * 看schema中是否有refName
	 * @param schema
	 * @param refName
	 * @return
	 */
	private boolean hasSchema(ExpressSchema schema, String refName) {
		List<ExpressDefined> defs = schema.getDefinedDataType();
		for (int i = 0; i < defs.size(); i++) {
			if(defs.get(i).getDataTypeName().equals(refName))
				return true;
		}
		
		List<ExpressEntity> entities = schema.getEntities();
		for (int i = 0; i < entities.size(); i++) {
			if(entities.get(i).getName().equals(refName))
				return true;
		}
		
		return false;
	}

	/**
	 * 先从defined data type中找，若没有，则是reference
	 * @param defs
	 * @param dataTypeName
	 * @return
	 */
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

		//schema_body :( interface_specification )* ( constant_decl )? ( declaration | rule_decl )*;
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
		if(hasDirectChild(schema_body, "rule_decl")) {
			//TODO　rule_decl
		} 
		if( hasDirectChild(schema_body, "interface_specification") ) {
			
			List<Integer> interface_specifications = getIdByName(schema_body,"interface_specification");
			
			List<ExpressReference> refs = new ArrayList<ExpressReference>();
			for (int i = 0; i < interface_specifications.size(); i++) {
				Integer interface_specification = interface_specifications.get(i);
				
				ExpressReferenceDao refDao = new ExpressReferenceDao();
				refs.addAll(refDao.getExpressReference(interface_specification));
			}
			
			schema.setRefenences(refs);
		} 
		if(hasDirectChild(schema_body, "constant_decl")){
			//TODO　constant_decl
		}
		return schema;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*ExpressSchemaDao es = new ExpressSchemaDao("localhost:7474");
		List<ExpressSchema> tmpSchema = es.getAllExpressSchema();
		System.out.println(tmpSchema);
		es.logout();*/

		/*ExpressSchemaDao dao = new ExpressSchemaDao();
		List<com.type.datatype.ExpressSchema> list = dao.getAllExpressSchema();
		dao.logout();

		System.out.println("\nschema test");
		for (com.type.datatype.ExpressSchema schema : list) {
			System.out.println("Shcema Name: " + schema.getName());
			for (com.type.datatype.ExpressEntity e : schema.getEntities()) {
				System.out.println("\tEntity Name: " + e.getName());
				for (com.type.instance.GeneralizedInstance instance : e.getMap().keySet()) {
					System.out.println("\t\tInstance Name: " + (instance == null ? "NULL" : instance.getName()));
					System.out.println("\t\tInstance DataType: " + (instance == null || instance.getDataType() == null ? "NULL" : instance.getDataType().getClass()));
					System.out.println();
				}
			}
		}*/
		
		ExpressSchemaDao dao = new ExpressSchemaDao("localhost:7474");
		List<com.type.datatype.ExpressSchema> list = dao.getAllExpressSchema();
		dao.logout();

		System.out.println("\nschema test");
		for (com.type.datatype.ExpressSchema schema : list) {
			System.out.println("Schema Name: " + schema.getName());
			for (com.type.datatype.ExpressDefined e : schema.getDefinedDataType()) {
				if (e.getDataType() instanceof com.type.datatype.ExpressEnumeration) {
					System.out.println("\tDefined name: " + e.getDataTypeName() + " enum " +
										((com.type.datatype.ExpressEnumeration) e.getDataType()).getItems());
				}
				else if (e.getDataType() instanceof com.type.datatype.ExpressEnumeration) {
					System.out.println("\tDefined name: " + e.getDataTypeName() + " select " +
										((com.type.datatype.ExpressSelect) e.getDataType()).getList());
				}
			}
		}
	}

}
