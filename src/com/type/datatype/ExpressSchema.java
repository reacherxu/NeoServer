package com.type.datatype;

import java.util.ArrayList;
import java.util.List;

public class ExpressSchema extends ExpressGeneralizedDataType {

	private String name;
	public List<ExpressGeneralizedDataType> definedDataType = new ArrayList<ExpressGeneralizedDataType>();
	protected List<ExpressEntity> entities = new ArrayList<ExpressEntity>();
	protected List<ExpressGeneralizedDataType> simpleDataType = new ArrayList<ExpressGeneralizedDataType>();

	public ExpressSchema(Integer id) {
		super(id);
	}
	
	public ExpressSchema(Integer id, String name) {
		super(id);
		this.name = name;
	}

	// 向Schema里添加数据类型及Entity均使用这个方法
	public void addDataType(ExpressGeneralizedDataType dataType) {
		definedDataType.add(dataType);
	}

	// 将Schema中DataType分为Entity和简单数据类型两类
	public void classifyDataType() {
		for (ExpressGeneralizedDataType dataType : definedDataType) {
			if (dataType instanceof ExpressEntity) {
				entities.add((ExpressEntity) dataType);
			}
			else {
				simpleDataType.add(dataType);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public void setDefinedDataType(List<ExpressGeneralizedDataType> definedDataType) {
		this.definedDataType = definedDataType;
	}

	public void setEntities(List<ExpressEntity> entities) {
		this.entities = entities;
	}

	public List<ExpressGeneralizedDataType> getDefinedDataType() {
		return definedDataType;
	}

	public List<ExpressEntity> getEntities() {
		return entities;
	}

	public List<ExpressGeneralizedDataType> getSimpleDataType() {
		return simpleDataType;
	}
	
	@Override
	public String toString() {
		return "ExpressSchema [" + entities.toString() + "]";
	}

}
