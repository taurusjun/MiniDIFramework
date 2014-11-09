package exercise.shape.def;

import exercise.shape.assist.Point;

public interface IShape {

	String getAliasName();

	double getArea();

	boolean containPoint(Point point);

}
