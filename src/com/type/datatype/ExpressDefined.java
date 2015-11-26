package com.type.datatype;

public class ExpressDefined extends ExpressGeneralizedDataType {

	ExpressGeneralizedDataType dataType = null;
	String dataName = null;

	public ExpressDefined(Integer id,ExpressGeneralizedDataType dataType, String dataName) {
		super(id);
		this.dataType = dataType;
		this.dataName = dataName;
	}

	public ExpressGeneralizedDataType getDataType() {
		return dataType;
	}

	public void setDataType(ExpressGeneralizedDataType dataType) {
		this.dataType = dataType;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	@Override
	public String toString() {
		return "ExpressDefined [dataType=" + dataType + ", dataName=" + dataName + "]";
	}

}
