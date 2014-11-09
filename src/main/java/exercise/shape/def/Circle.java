package exercise.shape.def;

import org.apache.log4j.Logger;

import exercise.shape.assist.Point;
import exercise.shape.exception.InvalidShapeInitParamException;

public final class Circle implements IShape {
	Logger logger = Logger.getLogger(Circle.class);
	private final int MAX_PARAMS_LEN = 3;

	private final String aliasName;
	private final Point centre;
	private final double radius;

	public Circle(double[] params,String aliasName) throws InvalidShapeInitParamException  {
		if (params.length < MAX_PARAMS_LEN) {
			String msg="Must initialize circle with " + MAX_PARAMS_LEN
					+ " parameters.";
			logger.error(msg);
			throw new InvalidShapeInitParamException(msg);
		} else if(params[2]<=0){
			String msg="Radius must be greater than zero.";
			logger.error(msg);
			throw new InvalidShapeInitParamException(new double[]{params[2]},msg);
		}else {
			centre = new Point(params[0], params[1]);
			radius = params[2];
			this.aliasName=aliasName;

			logger.debug("Circle is initialized with centre:"
					+ centre.toString() + " radius:" + radius);
		}
	}

	public String getAliasName() {
		return this.aliasName;
	}

	public double getArea() {
		return Math.PI * radius * radius;
	}

	public boolean containPoint(Point point) {
		double d = point.distance(centre);
		return d < radius;
	}

	public Point getCentre() {
		return centre;
	}

	public double getRadius() {
		return radius;
	}
	
	@Override
	public String toString(){
		return aliasName+": circle with centre at "+centre.toString()+" and radius "+radius;
	}
	
	public static String buildInstructions(){
		return "circle <centr.x> <centr.y> <radius>";
	}
}
