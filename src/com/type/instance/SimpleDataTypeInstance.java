package com.type.instance;

import com.type.datatype.ExpressSimpleDataType;

public abstract class SimpleDataTypeInstance extends GeneralizedInstance {

	public SimpleDataTypeInstance(Integer id, String name, ExpressSimpleDataType dataType) {
		super(id, name, dataType);
	}

	public SimpleDataTypeInstance(Integer id, String name, ExpressSimpleDataType dataType, Object value) {
		super(id, name, dataType, value);
	}

	@Override
	public String toString() {
		return "SimpleDataTypeInstance [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
