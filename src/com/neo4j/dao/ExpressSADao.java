package com.neo4j.dao;


public class ExpressSADao extends BaseDao {

	/**
	 * 寻找边界属性
	 * @param bound_spec
	 * @return
	 */
	protected Integer[] getBound(int bound_spec) {
		Integer[] bounds = {0,null};
		int bound_1 = getIdByName(bound_spec, "bound_1").get(0);
		int bound_2 = getIdByName(bound_spec, "bound_2").get(0);

		/* 寻找bound_1,bound_2的叶子节点 ,添加数值属性 */
		bounds[0] = Integer.parseInt(getLeaf(bound_1));

		String tmpBound = getLeaf(bound_2);
		bounds[1] = tmpBound.equals("?") ? null : Integer.parseInt(tmpBound);
		return bounds;
	}
}
