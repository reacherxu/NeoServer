package com.type.instance;

import com.type.datatype.ExpressInteger;

public class IntegerInstance extends GeneralizedInstance {
	
	public IntegerInstance(Integer id, String name, ExpressInteger dataType) {

		super(id, name, dataType);
	}
	
	public IntegerInstance(Integer id, String name, ExpressInteger dataType, String value) {

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
		return "IntegerInstance [name=" + name + ", dataType=" + dataType + ", value=" + getValue() + "]";
	}

}
