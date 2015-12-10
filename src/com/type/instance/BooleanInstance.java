package com.type.instance;

import com.type.datatype.ExpressBoolean;

public class BooleanInstance extends GeneralizedInstance {
	
	public BooleanInstance(Integer id, String name, ExpressBoolean dataType) {

		super(id, name, dataType);
	}
	
	public BooleanInstance(Integer id, String name, ExpressBoolean dataType, String value) {

		super(id, name, dataType, value);
	}

	@Override
	public String getValue() {
		return (String) value;
	}

	// Precise exception should be defined!
	public void setValue(String value) throws Exception {
		this.value = value;
	}

	@Override
	public String toString() {
		return "BooleanInstance [name=" + name + ", dataType=" + dataType + ", value=" + getValue() + "]";
	}

}
