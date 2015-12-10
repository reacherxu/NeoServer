package com.type.instance;

import com.type.datatype.ExpressNumber;

public class NumberInstance extends GeneralizedInstance {
	
	public NumberInstance(Integer id, String name, ExpressNumber dataType) {

		super(id, name, dataType);
	}
	
	public NumberInstance(Integer id, String name, ExpressNumber dataType, String value) {

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
		return "NumberInstance [name=" + name + ", dataType=" + dataType + ", value=" + getValue() + "]";
	}

}
