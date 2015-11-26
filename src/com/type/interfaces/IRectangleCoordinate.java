package com.type.interfaces;

import java.awt.Point;

import com.type.enums.LineType;

public interface IRectangleCoordinate {
	public Point p1 = new Point(0, 0);
	public Point p2 = new Point(0, 0);
	public LineType lineType = LineType.SOLD;

	public Integer getShapeWidth();

	public Integer getShapeHeight();
}
