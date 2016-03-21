package com.type.datatype;

public class ExpressReference extends ExpressGeneralizedDataType {
	public ExpressReference(Integer id) {
		super(id);
	}
	
	//TODO schema
	protected String dataName;
	protected String alias;
	protected String type;
	protected String schemaFrom;
	
	public String getDataName() {
		return dataName;
	}
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSchemaFrom() {
		return schemaFrom;
	}
	public void setSchemaFrom(String schemaFrom) {
		this.schemaFrom = schemaFrom;
	}
	
	@Override
	public String toString() {
		return "ExpressReference [type:" + type +
				",schemaFrom:" + schemaFrom +
				",dataName:" + dataName + 
				",alias:" + alias + "]";
	}
	
}
