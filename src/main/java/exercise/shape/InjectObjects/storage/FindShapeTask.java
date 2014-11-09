package exercise.shape.InjectObjects.storage;

import java.util.Vector;

import org.apache.log4j.Logger;

import exercise.shape.assist.Point;
import exercise.shape.def.IShape;

public class FindShapeTask implements Runnable {

	final Point point;
	final private Vector<IShape> allShapes;
	final private Vector<IShape> rsltShapes;

	Logger logger = Logger.getLogger(FindShapeTask.class);

	public FindShapeTask(Point point, Vector<IShape> allShapes,
			Vector<IShape> rsltShapes) {
		this.point = point;
		this.allShapes = allShapes;
		this.rsltShapes = rsltShapes;
	}

	public void run() {
		while (allShapes.size() > 0) {
			IShape shape = allShapes.remove(0);
			if (shape.containPoint(point)) {
				rsltShapes.add(shape);
				logger.debug("Shape: " + shape.toString()
						+ " contains this point.");
			}
		}
	}
}
