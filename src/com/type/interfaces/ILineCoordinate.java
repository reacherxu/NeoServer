package com.type.interfaces;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.type.enums.LineType;


public interface ILineCoordinate {

	public List<Point> points = new ArrayList<Point>();
	public String startText = null;
	public String endText = null;
	public String text = null;
	public LineType lineType = LineType.SOLD;
}
