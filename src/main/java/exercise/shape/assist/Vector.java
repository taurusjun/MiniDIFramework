package exercise.shape.assist;

public class Vector {
	private final double x;
	private final double y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector(Point point) {
		this.x = point.getX();
		this.y = point.getY();
	}

	public double dot(Vector v) {
		return x * v.x + y * v.y;
	}

	public Vector minus(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}

}
