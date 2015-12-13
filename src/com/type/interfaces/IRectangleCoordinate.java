package com.type.interfaces;

import com.type.enums.LineType;

public interface IRectangleCoordinate {
	public LineType lineType = LineType.SOLD;

	public Integer getShapeWidth();

	public Integer getShapeHeight();
}
