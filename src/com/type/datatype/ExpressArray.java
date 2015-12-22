package com.type.datatype;

public class ExpressArray<T extends ExpressGeneralizedDataType> extends ExpressAggregation<T> {

	protected Boolean isOptional = false;
	protected Boolean isUnique = false;

	public ExpressArray(Integer id) {
		super(id);
		bound1 = 0;
		bound2 = 0;
	}

	public ExpressArray(Integer id,Integer bound1, Integer bound2,T dataType) {
		super(id);
		setBound1(bound1);
		setBound2(bound2);
		setDataType(dataType);
	}

	
	public Boolean getIsOptional() {
		return isOptional;
	}

	public void setIsOptional(Boolean isOptional) {
		this.isOptional = isOptional;
	}

	public Boolean getIsUnique() {
		return isUnique;
	}

	public void setIsUnique(Boolean isUnique) {
		this.isUnique = isUnique;
	}

	@Override
	public void setBound1(Integer bound1) {

		if (bound1 != null) {
			this.bound1 = bound1;
		}
	}

	@Override
	public void setBound2(Integer bound2) {

		if (bound2 != null && bound2 >= bound1) {
			this.bound2 = bound2;
		}
	}
	
	@Override
	public String toString() {
		return "ExpressArray [bound1=" + bound1 + ", bound2=" + bound2 +
				", isOptional=" + isOptional + ", isUnique=" + isUnique + ", dataType=" + dataType + "]";
	}

}
