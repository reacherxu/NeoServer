package com.type.instance;

import com.type.datatype.ExpressLogical;

public class LogicalInstance extends GeneralizedInstance {
	
	public LogicalInstance(Integer id, String name, ExpressLogical dataType) {

		super(id, name, dataType);
	}
	
	public LogicalInstance(Integer id, String name, ExpressLogical dataType, String value) {

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
		return "LogicalInstance [name=" + name + ", dataType=" + dataType + ", value=" + getValue() + "]";
	}

}
