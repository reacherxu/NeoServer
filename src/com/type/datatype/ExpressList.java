package com.type.datatype;

public class ExpressList extends ExpressAggregation {

	Boolean isUnique = false;

	public ExpressList(Integer id) {
		super(id);
		bound1 = 0;
		bound2 = null;
	}
	
	public ExpressList(Integer id,Integer bound1, Integer bound2,ExpressGeneralizedDataType dataType) {
		super(id);
		setBound1(bound1);
		setBound2(bound2);
		setDataType(dataType);
	}

	@Override
	public void setBound1(Integer bound1) {
		if (bound1 != null && bound1 >= 0) {
			this.bound1 = bound1;
			if (bound2 != null && bound2 < bound1) {
				bound2 = bound1;
			}
		}
	}

	@Override
	public void setBound2(Integer bound2) {

		if (bound2 == null) {
			this.bound2 = null;
		}
		else if (bound2 >= bound1) {
			this.bound2 = bound2;
		}
	}

	public Boolean getIsUnique() {
		return isUnique;
	}

	public void setIsUnique(Boolean isUnique) {
		this.isUnique = isUnique;
	}
	
	@Override
	public String toString() {
		return "ExpressList [bound1=" + bound1 + ", bound2=" + bound2 +
				", isUnique=" + isUnique + ", dataType=" + dataType + "]";
	}

}
