package exercise.shape.def;

import org.apache.log4j.Logger;

import exercise.shape.assist.Point;
import exercise.shape.exception.InvalidShapeInitParamException;

public final class Donut implements IShape {
	Logger logger = Logger.getLogger(Donut.class);
	private final int MAX_PARAMS_LEN = 4;

	private final String aliasName;
	private final Point centre;
	private final double outerRadius;
	private final double innerRadius;

	public Donut(double[] params,String aliasName) throws InvalidShapeInitParamException {
		if (params.length < MAX_PARAMS_LEN) {
			String msg="Must initialize donut with " + MAX_PARAMS_LEN
					+ " parameters.";
			logger.error(msg);
			throw new InvalidShapeInitParamException(msg);
		} else if(params[2]<=0){
			String msg="Inner radius must be greater than zero.";
			logger.error(msg);
			throw new InvalidShapeInitParamException(new double[]{params[2]},msg);
		} else if(params[3]<=0){
			String msg="Outer radius must be greater than zero.";
			logger.error(msg);
			throw new InvalidShapeInitParamException(new double[]{params[3]},msg);
		} else if(params[3]<=params[2]){
			String msg="Outer radius must be greater than Inner radius.";
			logger.error(msg);
			throw new InvalidShapeInitParamException(new double[]{params[2],params[3]},msg);
		} else {
			centre = new Point(params[0], params[1]);
			innerRadius = params[2];
			outerRadius = params[3];
			this.aliasName=aliasName;

			logger.debug("Donut is initialized with centre:"
					+ centre.toString() + " outer radius:" + outerRadius
					+ " inner radius:" + innerRadius);
		}
	}

	public String getAliasName() {
		return this.aliasName;
	}

	public double getArea() {
		return Math.PI * outerRadius * outerRadius - Math.PI * innerRadius
				* innerRadius;
	}

	public boolean containPoint(Point point) {
		double d = point.distance(centre);
		return d < outerRadius && d > innerRadius;
	}

	public Point getCentre() {
		return centre;
	}

	public double getOutRadius() {
		return outerRadius;
	}

	public double getInnerRadius() {
		return innerRadius;
	}
	
	@Override
	public String toString(){
		return aliasName+": donut with centre at "+centre.toString()+" and inner radius "+innerRadius+" outer radius "+outerRadius;
	}

	public static String buildInstructions(){
		return "donut <centr.x> <centr.y> <inner radius> <outer radius>";
	}
}
