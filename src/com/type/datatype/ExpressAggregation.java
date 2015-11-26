package com.type.datatype;

public abstract class ExpressAggregation extends ExpressGeneralizedDataType {

	protected Integer bound1 = null;
	protected Integer bound2 = null;
	protected ExpressGeneralizedDataType dataType = null;

	public ExpressAggregation(Integer id) {
		super(id);
	}
	
	public Integer getBound1() {
		return bound1;
	}

	public abstract void setBound1(Integer bound1);

	public Integer getBound2() {
		return bound2;
	}

	public abstract void setBound2(Integer bound2);

	public Integer getSize() {
		return bound2 - bound1;
	}

	public ExpressGeneralizedDataType getDataType() {
		return dataType;
	}

	public void setDataType(ExpressGeneralizedDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "ExpressAggregation [bound1=" + bound1 + ", bound2=" + bound2 + ", dataType=" + dataType + "]";
	}

}
