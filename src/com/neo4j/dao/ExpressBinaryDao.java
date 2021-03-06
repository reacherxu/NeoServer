package com.neo4j.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.type.datatype.ExpressBinary;
import com.type.instance.GeneralizedInstance;
import com.type.instance.BinaryInstance;

public class ExpressBinaryDao extends BaseDao {
	
	/* binary_type 的结构 */
	private static final int BINARY_TYPE = 2;
	
	private static final int BINARY_TYPE_WIDTH_FIXED = 4;
	
	/**
	 * 进行图遍历，查询所有的ExpressBinary
	 * @return
	 */
	public List<ExpressBinary> getAllExpressBinary() {
		List<ExpressBinary> res = new ArrayList<ExpressBinary>();
		
		/* 返回string_type对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='binary_type' return ID(n) as id";
		List<Map<String, Object>> nodeList = this.getNeoConn().queryList(sql);
		
		/* 每个binary_type创建ExpressBinary */
		for(int i=0; i<nodeList.size(); i++) {
			int binary_type = (Integer) nodeList.get(i).get("id");
			
			res.add(getExpressBinary(binary_type));
		}
		
		return res;
	}
	
	/**
	 * 根据指定的binary节点，解析binary
	 * @param binary_type
	 * @return
	 */
	public ExpressBinary getExpressBinary(Integer binary_type) {
		ExpressBinary expBinary = null;
		Integer val = null;
		Boolean fixed = false;
		

		/* 判断 数值属性是否存在 */
		if( getDirectChildrenNum(binary_type) == BINARY_TYPE ) {

			int width_spec = getIdByName(binary_type,"width_spec").get(0);

			/*　判断 fixed属性是否存在 */
			if( getDirectChildrenNum(width_spec) == BINARY_TYPE_WIDTH_FIXED ) {
				fixed = true;
			}

			int width = getIdByName(width_spec,"width").get(0);

			/* 寻找width的叶子节点 ,添加数值属性 */
			val = Integer.parseInt(getLeaf(width));
			
		}
		expBinary = new ExpressBinary(binary_type, val, fixed);
		
		return expBinary;
	}
	
	/**
	 * 遍历图并寻找BinaryInstance
	 * @return
	 */
	public List<BinaryInstance> getAllExpressBinaryInstances() {
		List<BinaryInstance> binaryInstances = new ArrayList<BinaryInstance>();
		
		/* 返回explicit_attr对应的id */
		String sql = "start n=node(*) match (n:Node) where n.name='explicit_attr' return ID(n) as id";
		List<Map<String, Object>> explicit_attrs = this.getNeoConn().queryList(sql);
		
		/* 解析每一个explicit_attr下的实例 */
		for (int i = 0; i < explicit_attrs.size(); i++) {
			List<GeneralizedInstance> binaryInstance = getSimpleDataTypeInstance( (Integer)explicit_attrs.get(i).get("id") );
			for (int j = 0; j < binaryInstance.size() ; j++) {
				binaryInstances.add( (BinaryInstance)binaryInstance.get(j)  );
			}
		}
		
		return binaryInstances;
	}
	
	/**
	 * 根据指定的explicit_attr节点，解析binary instance
	 * @param explicit_attr
	 * @return
	 */
	public List<GeneralizedInstance> getExpressBinaryInstance(Integer explicit_attr) {
		
		 return getSimpleDataTypeInstance( explicit_attr );
	}
	
	
	public static void main(String[] args) {
		ExpressBinaryDao ins = new ExpressBinaryDao();

		// test ExpressStringInstance
		Integer intList[] = {62,72,97};
		for (int i = 0; i < intList.length; i++) {
			List<GeneralizedInstance> binaryIns = ins.getExpressBinaryInstance(intList[i]);
			System.out.println(binaryIns);
		}
		
		/*strIns = ins.getExpressStringInstance(72);
		System.out.println(strIns);
		strIns = ins.getExpressStringInstance(62);
		System.out.println(strIns);*/
//		ExpressString str = ins.getExpressString(79);
//		System.out.println(str);
//		List<ExpressString> strList = ins.getAllExpressString();
//		System.out.println(strList);
		
//		System.out.println(ins.getAllExpressEntity());
//		System.out.println(ins.getExpressRealInstance());
//		System.out.println(ins.getExpressEntity(133));
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
