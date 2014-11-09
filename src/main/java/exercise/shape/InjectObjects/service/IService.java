package exercise.shape.InjectObjects.service;

import exercise.shape.InjectObjects.InjectableObject;
import exercise.shape.assist.Point;

public interface IService extends InjectableObject {
	ShapeServiceResponse saveNewShape(String shapeName, double[] params);
	ShapeServiceResponse findShape(String shapeName);
	ShapeServiceResponse deleteShape(String shapeName);
	ShapeServiceResponse findShapeContainsPoint(Point point);
	ShapeServiceResponse getBuidShapeInstructions(String shapeName);
	ShapeServiceResponse getBuidShapeInstructions();
	ShapeServiceResponse getAvailableShapes();
}
