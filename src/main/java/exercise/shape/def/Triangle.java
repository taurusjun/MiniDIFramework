package exercise.shape.def;

import org.apache.log4j.Logger;

import exercise.shape.assist.Point;
import exercise.shape.assist.Vector;
import exercise.shape.exception.InvalidShapeInitParamException;

public final class Triangle implements IShape {
	private final String aliasName;
	private final int MAX_PARAMS_LEN = 6;
	final Point[] vertices;

	Logger logger = Logger.getLogger(Triangle.class);

	public Triangle(double[] params,String aliasName) throws InvalidShapeInitParamException {
		if (params.length < MAX_PARAMS_LEN) {
			String msg="Must initialize triangle with " + MAX_PARAMS_LEN
					+ " parameters.";
			logger.error(msg);
			throw new InvalidShapeInitParamException(msg);
		}

		vertices = new Point[3];
		vertices[0] = new Point(params[0], params[1]);
		vertices[1] = new Point(params[2], params[3]);
		vertices[2] = new Point(params[4], params[5]);
		this.aliasName=aliasName;

		logger.debug("Triangle is initialized with three angles:"
				+ vertices[0].toString() + "," + vertices[1].toString() + ","
				+ vertices[2].toString());
	}

	public String getAliasName() {
		return this.aliasName;
	}

	public double getArea() {
		double a = vertices[0].distance(vertices[1]);
		double b = vertices[0].distance(vertices[2]);
		double c = vertices[1].distance(vertices[2]);
		double s = (a + b + c) / 2;

		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	public boolean containPoint(Point point) {
		Vector a = new Vector(vertices[0]);
		Vector b = new Vector(vertices[1]);
		Vector c = new Vector(vertices[2]);
		Vector p = new Vector(point);

		Vector v0 = c.minus(a);
		Vector v1 = b.minus(a);
		Vector v2 = p.minus(a);
		double dot00 = v0.dot(v0);
		double dot01 = v0.dot(v1);
		double dot02 = v0.dot(v2);
		double dot11 = v1.dot(v1);
		double dot12 = v1.dot(v2);

		double inverDeno = 1 / (dot00 * dot11 - dot01 * dot01);

		double u = (dot11 * dot02 - dot01 * dot12) * inverDeno;
		if (u < 0 || u > 1) // if u out of range, return directly
		{
			return false;
		}

		double v = (dot00 * dot12 - dot01 * dot02) * inverDeno;
		if (v < 0 || v > 1) // if v out of range, return directly
		{
			return false;
		}

		return u + v <= 1;
	}

	public Point[] getAngles() {
		return vertices;
	}

	@Override
	public String toString(){
		return aliasName+": triangle with 3 vertices at "+vertices[0].toString()+" "+vertices[1].toString()+" "+vertices[2].toString();
	}
	
	public static String buildInstructions(){
		return "triangle <vertice1.x> <vertice1.y> <vertice2.x> <vertice2.y> <vertice3.x> <vertice3.y>";
	}
}
