package com.type.datatype;

public class ExpressSet extends ExpressAggregation {


	public ExpressSet(Integer id,ExpressGeneralizedDataType dataType) {
		super(id);
		bound1 = 0;
		bound2 = null;
		this.dataType = dataType;
	}

	public ExpressSet(Integer id,Integer bound1, Integer bound2,ExpressGeneralizedDataType dataType) {
		super(id);
		setBound1(bound1);
		setBound2(bound2);
		setDataType(dataType);
	}

	@Override
	public void setBound1(Integer bound1) {
		if (bound1 != null && bound1 >= 0) {
			this.bound1 = bound1;
			if (bound2 != null && bound2 < this.bound1) {
				bound2 = bound1;
			}
		}

	}

	@Override
	public void setBound2(Integer bound2) {
		this.bound2 = bound2;
	}

	@Override
	public String toString() {
		return "ExpressSet [bound1=" + bound1 + ", bound2=" + bound2 + ", dataType=" + dataType + "]";
	}

}
