package com.type.instance;

import com.type.datatype.ExpressString;

public class StringInstance extends GeneralizedInstance {
	
	public StringInstance(Integer id, String name, ExpressString dataType) {

		super(id, name, dataType);
	}
	
	public StringInstance(Integer id, String name, ExpressString dataType, String value) {

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
		return "StringInstance [name=" + name + ", dataType=" + dataType + ", value=" + getValue() + "]";
	}

}
