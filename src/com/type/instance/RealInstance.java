package com.type.instance;

import com.type.datatype.ExpressReal;

public class RealInstance extends GeneralizedInstance {

	public RealInstance(Integer id, String name, ExpressReal dataType) {
		super(id, name, dataType);
	}
	
	public RealInstance(Integer id, String name, ExpressReal dataType, Double value) {
		super(id, name, dataType, value);
	}

	@Override
	public Double getValue() {
		return (Double) value;
	}

	// Precise exception should be defined!
	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "RealInstance [name=" + name + ", dataType=" + dataType + ", value=" + getValue() + "]";
	}
}
