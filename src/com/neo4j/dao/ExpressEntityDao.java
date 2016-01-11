package com.neo4j.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressBag;
import com.type.datatype.ExpressEntity;
import com.type.datatype.ExpressSet;
import com.type.instance.BagInstance;
import com.type.instance.EntityInstance;
import com.type.instance.GeneralizedInstance;
import com.type.instance.SetInstance;

public class ExpressEntityDao extends BaseDao {
	private String OPTIONAL = "OPTIONAL:true";
	private static List<String> DERIVE = null;
	
	static {
		DERIVE = new ArrayList<String>();
		DERIVE.add("DERIVE:expression");
	}

	/**
	 * 遍历图，返回图中所有的entity
	 * @return
	 */
	public List<ExpressEntity> getAllExpressEntity() {
		List<ExpressEntity> entityList = new ArrayList<ExpressEntity>();

		/* 返回entity_decl对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='entity_decl' return ID(n) as id";
		List<Map<String, Object>> entities = this.getNeoConn().queryList(sql);

		for (int i = 0; i < entities.size(); i++) {
			entityList.add( getExpressEntity((Integer)entities.get(i).get("id")) );
		}

		return entityList;
	}

	/**
	 * 根据entity的名字返回对应的entity
	 * 若图中无entity，返回null
	 * @param name
	 * @return
	 */
	public ExpressEntity getEntityByName(String name) {
		List<ExpressEntity> entityList = getAllExpressEntity();
		for (int i = 0; i < entityList.size(); i++) {
			if( entityList.get(i).getName().equals(name) ) {
				return entityList.get(i);
			}
		}

		return null;
	}

	/**
	 * 根据entity_decl获取父类列表
	 * @param entity_decl
	 */
	private List<String> getBase(Integer entity_decl) {
		List<String> bases = new ArrayList<String>();

		/* 返回subsuper对应的id */
		Integer subsuper = null;
		Integer entity_head = getIdByName(entity_decl, "entity_head").get(0);
		if( getIdByName(entity_head, "subsuper").size() == 0)
			return bases;
		else
			subsuper = getIdByName(entity_head, "subsuper").get(0);

		//TODO supertype_constraint
		if( getIdByName(subsuper, "subtype_declaration").size() != 0 ) {
			Integer subtype_declaration = getIdByName(subsuper, "subtype_declaration").get(0);
			List<Integer> entity_refs = getIdByName(subtype_declaration, "entity_ref");
			/* 添加所有的父类 */
			for (int i = 0; i < entity_refs.size(); i++) {
				String sql = "start n=node({1}) match (n:Node)-[r*2..2]->(m:Node) return m.name as name";
				bases.add( (String)this.getNeoConn().query(sql,entity_refs.get(i)).get("name") );
			}
		}
		return bases;
	}

	/**
	 * 根据指定entity_decl节点，解析entity
	 * @param entity_decl
	 * @return	Class ExpressEntity
	 */
	public ExpressEntity getExpressEntity(Integer entity_decl) {
		ExpressEntity expressEntity = null;
		String name = null;
		Map<GeneralizedInstance,List<String>> entityBody = null;
		Map<String,List<String>> uniqueList = null;

		/* 获取entity name */
		int entity_head = getIdByName(entity_decl, "entity_head").get(0);
		int entity_id = getIdByName(entity_head, "entity_id").get(0);
		name = (String) getDirectChildren(entity_id).get(0).get("name");

		/* 获取entity父类列表 */
		List<String> baseList = getBase(entity_decl);

		/* 获取entity 中的变量申明 */
		String sql = "start n=node({1}) match (n:Node)-[*1..]->(m:Node) where m.name={2} return ID(m) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql,entity_decl,new String("explicit_attr"));
		List<Map<String, Object>> derive_clauses = this.getNeoConn().queryList(sql,entity_decl,new String("derive_clause"));
		List<Map<String, Object>> inverse_clauses = this.getNeoConn().queryList(sql,entity_decl,new String("inverse_clause"));
		List<Map<String, Object>> unique_clauses = this.getNeoConn().queryList(sql,entity_decl,new String("unique_clause"));

		entityBody = getExpressExplicitInstance(explicit_attrs);
		entityBody.putAll(getExpressDeriveInstance(derive_clauses));
		entityBody.putAll(getExpressInverseInstance(inverse_clauses));
		uniqueList = getExpressUniqueInstance(unique_clauses);

		/* 设置entity属性 */
		expressEntity = new ExpressEntity(entity_decl, name, null);
		expressEntity.setSuperTypes(baseList);
		expressEntity.setMap(entityBody);
		expressEntity.setInstanceList();
		expressEntity.setUniqueList(uniqueList);

		return expressEntity;
	}


	/**
	 * 寻找指定Entity中 所有的explicit_attr instance
	 * @return
	 */
	private Map<GeneralizedInstance,List<String>> getExpressExplicitInstance(List<Map<String, Object>> explicit_attrs) {
		Map<GeneralizedInstance,List<String>> instances = new HashMap<GeneralizedInstance,List<String>>();

		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			Integer explicit_attr = (Integer)explicit_attrs.get(i).get("id");

			/* 
			 * 分为generalized_types | named_types | simple_types  
			 */
			List<GeneralizedInstance> tmpInstance = new ArrayList<GeneralizedInstance>();

			tmpInstance.addAll(getSimpleDataTypeInstance(explicit_attr));
			
			/* inverse里另外写了，entity外的也另外写 */
			tmpInstance.addAll(getGeneralizedTypeInstance(explicit_attr));
			
			//TODO:type_ref
			tmpInstance.addAll(getNamedTypeInstance(explicit_attr));

			if( hasDirectChild(explicit_attr,"OPTIONAL")  )
				for (int j = 0; j < tmpInstance.size() ; j++) {
					List<String> tmpStr = new ArrayList<String>();
					tmpStr.add(OPTIONAL);
					instances.put( tmpInstance.get(j) , tmpStr);
				}
			else
				for (int j = 0; j < tmpInstance.size() ; j++) 
					instances.put( tmpInstance.get(j) , null);

		}

		return instances;
	}
	
	/**
	 * 寻找指定Entity中 所有的derive attribute instance
	 * @return
	 */
	private Map<? extends GeneralizedInstance, ? extends List<String>> getExpressDeriveInstance(
			List<Map<String, Object>> derive_clauses) {
		Map<GeneralizedInstance,List<String>> instances = new HashMap<GeneralizedInstance,List<String>>();

		/* 解析每一个derive_clauses下的实例 */
		for (int i = 0; i < derive_clauses.size(); i++) {
			Integer derive_clause = (Integer)derive_clauses.get(i).get("id");

			String sql = "start n=node({1}) match (n:Node)-[*1..]->(m:Node) where m.name={2} return ID(m) as id";
			List<Map<String, Object>> derived_attrs = this.getNeoConn().queryList(sql,derive_clause,new String("derived_attr"));
			for (int j = 0; j < derived_attrs.size(); j++) {
				Integer derived_attr = (Integer) derived_attrs.get(j).get("id");

				/* 分为generalized_types | named_types | simple_types   */
				List<GeneralizedInstance> tmpInstance = new ArrayList<GeneralizedInstance>();
				//XXX :默认是基本数据类型
				tmpInstance.addAll(getSimpleDataTypeInstance(derived_attr));

				for (int k = 0; k < tmpInstance.size(); k++) 
					instances.put( tmpInstance.get(k) , DERIVE);
					
			}
		}

		return instances;
	}

	/**
	 * 寻找指定Entity中 所有的inverse attribute instance
	 * @return
	 */
	private Map<? extends GeneralizedInstance, ? extends List<String>> 
		getExpressInverseInstance(List<Map<String, Object>> inverse_clauses) {
		Map<GeneralizedInstance,List<String>> instances = new HashMap<GeneralizedInstance,List<String>>();

		/* 解析每一个inverse_clauses下的实例 */
		for (int i = 0; i < inverse_clauses.size(); i++) {
			Integer inverse_clause = (Integer)inverse_clauses.get(i).get("id");

			String sql = "start n=node({1}) match (n:Node)-[*1..]->(m:Node) where m.name={2} return ID(m) as id";
			List<Map<String, Object>> inverse_attrs = this.getNeoConn().queryList(sql,inverse_clause,new String("inverse_attr"));
			for (int j = 0; j < inverse_attrs.size(); j++) {
				Integer inverse_attr = (Integer) inverse_attrs.get(j).get("id");


				Integer entity_ref = getIdByName(inverse_attr, "entity_ref").get(0);
				Integer attribute_ref = getIdByName(inverse_attr, "attribute_ref").get(0);
				List<Map<String,Object>> tmpVars = getVariables(inverse_attr);

				if( hasDirectChild(inverse_attr, "OF") ) {
					Integer bound_spec = getIdByName(inverse_attr, "bound_spec").get(0);

					if( hasDirectChild(inverse_attr, "SET")) {

						/* new Express Set instance*/
						Integer SET = (Integer) getDirectChildren(inverse_attr).get(0).get("id");
						ExpressSetDao setDao = new ExpressSetDao();
						Integer[] bounds = setDao.getBound(bound_spec);
						ExpressEntity innerEntity = (ExpressEntity) getEntityRef(entity_ref);
						ExpressSet<ExpressEntity> tmpSet = new ExpressSet<ExpressEntity>
									(SET, bounds[0], bounds[1], innerEntity);

						for (int k = 0; k < tmpVars.size(); k++) {
							SetInstance<EntityInstance,ExpressEntity> setIns = new SetInstance<EntityInstance,ExpressEntity>
							((Integer)tmpVars.get(k).get("id"),(String)tmpVars.get(i).get("name"),tmpSet);

							List<String> tmpList = new ArrayList<String>();
							tmpList.add("INVERSE:" + innerEntity.getName() + " FOR " + getLeaf(attribute_ref));

							instances.put(setIns, tmpList);
						}
					} else {
						/* new Express Bag instance*/
						Integer BAG = (Integer) getDirectChildren(inverse_attr).get(0).get("id");
						ExpressBagDao bagDao = new ExpressBagDao();
						Integer[] bounds = bagDao.getBound(bound_spec);
						ExpressEntity innerEntity = (ExpressEntity) getEntityRef(entity_ref);
						ExpressBag<ExpressEntity> tmpBag = new ExpressBag<ExpressEntity>
						(BAG, bounds[0], bounds[1], innerEntity);

						for (int k = 0; k < tmpVars.size(); k++) {
							BagInstance<EntityInstance,ExpressEntity> bagIns = new BagInstance<EntityInstance,ExpressEntity>
							((Integer)tmpVars.get(k).get("id"),(String)tmpVars.get(i).get("name"),tmpBag);

							List<String> tmpList = new ArrayList<String>();
							tmpList.add("INVERSE:" + innerEntity.getName() + " FOR " + getLeaf(attribute_ref));

							instances.put(bagIns, tmpList);
						}
					}
				} else {
					ExpressEntity innerEntity = (ExpressEntity) getEntityRef(entity_ref);
					for (int k = 0; k < tmpVars.size(); k++) {
						EntityInstance entityIns = new EntityInstance
								((Integer)tmpVars.get(k).get("id"),(String)tmpVars.get(i).get("name"), innerEntity);

						List<String> tmpList = new ArrayList<String>();
						tmpList.add("INVERSE:" + innerEntity.getName() + " FOR " + getLeaf(attribute_ref));

						instances.put(entityIns, tmpList);
					}
				}
			}

		}

		return instances;
	}

	/**
	 * 寻找指定Entity中 所有的unique_clauses instance
	 * @param unique_clauses
	 * @return
	 */
	private Map<String,List<String>> getExpressUniqueInstance(List<Map<String, Object>> unique_clauses) {
		Map<String,List<String>> uniqueList = new HashMap<String,List<String>>();

		/* 解析每一个unique_clause下的实例  */
		for (int i = 0; i < unique_clauses.size(); i++) {
			Integer unique_clause = (Integer)unique_clauses.get(i).get("id");

			List<Integer> unique_rules = getIdByName(unique_clause, "unique_rule");

			/* 解析rule name和references */
			String ruleName = null;
			List<String> ruleVal = new ArrayList<String>();
			for (int j = 0; j < unique_rules.size(); j++) {
				Integer unique_rule = unique_rules.get(j);

				Integer rule_label_id = (Integer) getDirectChildren(unique_rule).get(0).get("id");
				ruleName = (String) getDirectChildren(rule_label_id).get(0).get("name");

				List<Integer> referenced_attributes = getIdByName(unique_rule, "referenced_attribute");
				//TODO  qualified_attribute;
				for (int k = 0; k < referenced_attributes.size(); k++) {
					Integer referenced_attribute = referenced_attributes.get(k);
					ruleVal.add(getLeaf(referenced_attribute));
				}
			}
			uniqueList.put(ruleName, ruleVal);
		}

		return uniqueList;
	}

	public static void main(String[] args) {
		ExpressEntityDao ins = new ExpressEntityDao();

		//		ExpressString str = ins.getExpressString(191);
		//		System.out.println(str);
		//		List<ExpressString> strList = ins.getAllExpressString();
		//		System.out.println(strList);

		//		System.out.println(ins.getAllExpressEntity());
		//		System.out.println(ins.getExpressRealInstance());

		//		List<ExpressEntity> list = ins.getAllExpressEntity();
		//		for (int i = 0; i < list.size(); i++) {
		//			System.out.println(list.get(i));
		//		}

		//		System.out.println(ins.getExpressEntity(104)); 
		System.out.println(ins.getAllExpressEntity());


		//		System.out.println(ins.getSimpleDataTypeInstance(149));
		//		System.out.println(ins.getVariables(149));
		//		List<ExpressReal> realList = ins.getExpressReal();
		//		System.out.println(realList);
		//		ins.setLocation(83, 20,21,12,13);
		//		List<Double> p =  ins.getLocation(195);
		//		System.out.println(p);
		//		ins.detachDelete(191);

		//		ins.setProperty(197, "ignore", false);
		ins.logout();

	}
}
