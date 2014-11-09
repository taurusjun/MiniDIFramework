package exercise.shape.context;

import java.util.Map;

import exercise.shape.InjectObjects.InjectableObject;
import exercise.shape.def.IShape;

public interface IContext{
	<T extends InjectableObject> T getInjectObject(String name);

	<T extends InjectableObject> T getInjectObject(Class<T> clazz);

	Map<String, Class<? extends IShape>> getShapeMap();
}
