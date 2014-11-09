package exercise.shape.InjectObjects.factory;

import exercise.shape.InjectObjects.InjectableObject;
import exercise.shape.def.IShape;
import exercise.shape.exception.InvalidShapeInitParamException;

public interface IFactory extends InjectableObject {
	IShape newShape(String shapeName, double[] params) throws InvalidShapeInitParamException;
}
