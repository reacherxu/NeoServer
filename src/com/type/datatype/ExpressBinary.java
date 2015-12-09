package com.type.datatype;

public class ExpressBinary extends ExpressSimpleDataType {

	private Integer width = null;
	private Boolean isFixed = false;

	public ExpressBinary(Integer id,Integer width) {
		super(id);
		this.width = width;
	}

	public ExpressBinary(Integer id,Integer width, Boolean isFixed) {
		super(id);
		this.width = width;
		this.isFixed = isFixed;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Boolean getIsFixed() {
		return isFixed;
	}

	public void setIsFixed(Boolean isFixed) {
		this.isFixed = isFixed;
	}

	@Override
	public String toString() {
		return "Binary [width=" + width + ", isFixed=" + isFixed + "]";
	}
}
