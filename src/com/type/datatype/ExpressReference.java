package com.type.datatype;

public class ExpressReference extends ExpressGeneralizedDataType {
	public ExpressReference(Integer id) {
		super(id);
	}
	
	public ExpressReference(Integer id, String schemaFrom, String dataName, String type) {
		super(-1);
		this.schemaFrom = schemaFrom;
		this.dataName = dataName;
		this.type = type;
	}
	
	public ExpressReference(Integer id, String schemaFrom, String dataName, String type, String referenceType) {
		super(-1);
		this.schemaFrom = schemaFrom;
		this.dataName = dataName;
		this.type = type;
		this.referenceType = referenceType;
	}
	
	//TODO schema
	protected String dataName;
	protected String alias;
	protected String type;
	protected String schemaFrom;
	
	protected String referenceType;
	
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
	
	public String getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}

	@Override
	public String toString() {
		return "ExpressReference [type:" + type +
				",schemaFrom:" + schemaFrom +
				",dataName:" + dataName + 
				",alias:" + alias + "]";
	}
	
}
