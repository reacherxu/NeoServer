package com.type.instance;

import java.awt.Point;
import java.util.List;

import com.type.datatype.ExpressGeneralizedDataType;


public abstract class GeneralizedInstance {
	/* Each node's id */
	private final Integer id;
	/* Each line's position */
	protected List<Point> points;
	
	//TODO　schema
	protected String schemaName;

	public String name = null;
	public ExpressGeneralizedDataType dataType = null;
	public Object value = null;
	
	/* no need to assign value */
	private Boolean isValueAssigned = false;

	public GeneralizedInstance(Integer id, String name, ExpressGeneralizedDataType dataType) {

		if (name == null) {
			throw new NullPointerException("NULL can't be instance name");
		}
		else if (dataType == null) {
			throw new NullPointerException("NULL can't be instance dataType");
		}

		this.id = id;
		this.name = name;
		this.dataType = dataType;
		this.setIsValueAssigned(false);
	}

	public GeneralizedInstance(Integer id, String name, ExpressGeneralizedDataType dataType, Object value) {

		if (name == null) {
			throw new NullPointerException("NULL can't be instance name");
		}
		else if (dataType == null) {
			throw new NullPointerException("NULL can't be instance dataType");
		}

		this.id = id;
		this.name = name;
		this.dataType = dataType;
		this.value = value;
		this.setIsValueAssigned(true);
	}

	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public ExpressGeneralizedDataType getDataType() {
		return dataType;
	}

	public void setDataType(ExpressGeneralizedDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "GeneralizedInstance [name=" + name + ", dataType=" + dataType + ", value=" + value + "]";
	}

	public Boolean getIsValueAssigned() {
		return isValueAssigned;
	}

	public void setIsValueAssigned(Boolean isValueAssigned) {
		this.isValueAssigned = isValueAssigned;
	}

}
