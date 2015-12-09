package com.type.instance;

import com.type.datatype.ExpressBinary;

public class BinaryInstance extends GeneralizedInstance {
	
	public BinaryInstance(Integer id, String name, ExpressBinary dataType) {

		super(id, name, dataType);
	}
	
	public BinaryInstance(Integer id, String name, ExpressBinary dataType, String value) {

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
		return "BinaryInstance [name=" + name + ", dataType=" + dataType + ", value=" + getValue() + "]";
	}

}
