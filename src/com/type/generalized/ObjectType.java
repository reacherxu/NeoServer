package com.type.generalized;

public abstract class ObjectType {
	//对应的数据库节点
	private final int id;
	//TODO 变量名
//	public String val;
	
	
	public ObjectType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
