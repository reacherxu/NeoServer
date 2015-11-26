package com.type.datatype;

public class ExpressReal extends ExpressSimpleDataType {

	/**
	 * 小数点后位数
	 */
	Integer precision = null;

	public ExpressReal(Integer id) {
		super(id);
	}

	public ExpressReal(Integer id,Integer precision) {
		super(id);
		this.precision = precision;
	}

	@Override
	public String toString() {
		return "ExpressReal [precision=" + precision + "]";
	}

}
