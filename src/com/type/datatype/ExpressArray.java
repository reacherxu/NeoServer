package com.type.datatype;

public class ExpressArray extends ExpressAggregation {

	protected Boolean isOptional = false;
	protected Boolean isUnique = false;

	public ExpressArray(Integer id) {
		super(id);
		bound1 = 0;
		bound2 = 0;
	}

	public ExpressArray(Integer id,Integer bound1, Integer bound2) {
		super(id);
		setBound1(bound1);
		setBound2(bound2);
	}

	@Override
	public void setBound1(Integer bound1) {

		if (bound1 != null) {
			this.bound1 = bound1;
			if (bound2 < bound1) {
				bound2 = bound1;
			}
		}
	}

	@Override
	public void setBound2(Integer bound2) {

		if (bound2 != null && bound2 >= bound1) {
			this.bound2 = bound2;
		}
	}

}
