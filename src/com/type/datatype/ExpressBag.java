package com.type.datatype;

public class ExpressBag extends ExpressAggregation {

	public ExpressBag(Integer id) {
		super(id);
		bound1 = 0;
		bound2 = null;
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
		if (bound2 == null) {
			this.bound2 = bound2;
		}
		else if (bound2 >= bound1) {
			this.bound2 = bound1;
		}
	}

}