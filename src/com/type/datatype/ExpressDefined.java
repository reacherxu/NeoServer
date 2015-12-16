package com.type.datatype;


public class ExpressDefined extends ExpressGeneralizedDataType {

	private ExpressGeneralizedDataType dataType = null;
	private String dataTypeName = null;
	
	public ExpressDefined(Integer id) {
		super(id);
	}
	
	public ExpressDefined(Integer id,ExpressGeneralizedDataType dataType,String dataTypeName) {
		super(id);
		this.dataType = dataType;
		this.dataTypeName = dataTypeName;
	}

	public ExpressGeneralizedDataType getDataType() {
		return dataType;
	}

	public void setDataType(ExpressGeneralizedDataType dataType) {
		this.dataType = dataType;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}
	

}
