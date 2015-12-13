package com.neo4j.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressEntity;
import com.type.instance.GeneralizedInstance;

public class ExpressEntityDao extends BaseDao {
	private String OPTIONAL = "OPTIONAL";
	
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
	 * 根据指定entity_decl节点，解析entity
	 * @param entity_decl
	 * @return	Class ExpressEntity
	 */
	public ExpressEntity getExpressEntity(Integer entity_decl) {
		String name = null;
		Map<GeneralizedInstance,List<String>> entityBody = null;
		
		/* 获取entity name */
		int entity_head = getIdByName(entity_decl, "entity_head").get(0);
		int entity_id = getIdByName(entity_head, "entity_id").get(0);
		name = (String) getDirectChildren(entity_id).get(0).get("name");
		
		/* 获取entity 中的变量申明 */
		entityBody = getExpressInstance(entity_decl);
		
		return new ExpressEntity(entity_decl, name, entityBody);
	}
		
	
	
	/**
	 * 寻找指定Entity中 所有的Instance
	 * @return
	 */
	public Map<GeneralizedInstance,List<String>> getExpressInstance(Integer entity_decl) {
		Map<GeneralizedInstance,List<String>> instances = new HashMap<GeneralizedInstance,List<String>>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node({1}) match (n:Node)-[*1..]->(m:Node) where m.name='explicit_attr' return ID(m) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql,entity_decl);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			Integer explicit_attr = (Integer)explicit_attrs.get(i).get("id");
			
			List<GeneralizedInstance> tmpInstance = getSimpleDataTypeInstance( explicit_attr );
			//TODO　add attribute optional (大小写敏感)
			if( getIdByName(explicit_attr,"OPTIONAL").size() != 0 )
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
	
	public static void main(String[] args) {
		ExpressEntityDao ins = new ExpressEntityDao();
//		ExpressString str = ins.getExpressString(191);
//		System.out.println(str);
//		List<ExpressString> strList = ins.getAllExpressString();
//		System.out.println(strList);
		
//		System.out.println(ins.getAllExpressEntity());
//		System.out.println(ins.getExpressRealInstance());
		System.out.println(ins.getExpressEntity(104));
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
