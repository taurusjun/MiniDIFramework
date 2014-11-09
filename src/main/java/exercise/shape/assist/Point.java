package exercise.shape.assist;

public class Point {
	private final double x;
	private final double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double distance(Point p) {
		double dX = x - p.x;
		double dY = y - p.y;
		return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
