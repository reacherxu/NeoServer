package com.type.datatype;

import com.type.interfaces.IRectangleCoordinate;

public abstract class ExpressGeneralizedDataType implements IRectangleCoordinate {

	/* Each node's id */
	private final Integer id;
	
	public ExpressGeneralizedDataType(Integer id) {
		this.id = id;
	}

	
	public Integer getId() {
		return id;
	}


	@Override
	public Integer getShapeWidth() {
		return p2.x - p1.x;
	}

	@Override
	public Integer getShapeHeight() {
		return p2.y - p1.y;
	}

	public String printShape() {
		return "[" + "Generalized Data Type" + "]" + p1 + "," + p2;
	}

	@Override
	public String toString() {
		return "ExpressGeneralizedDataType []";
	}

}