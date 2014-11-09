package exercise.shape.InjectObjects.storage;

import java.util.List;

import exercise.shape.InjectObjects.InjectableObject;
import exercise.shape.assist.Point;
import exercise.shape.def.IShape;

public interface IStorage extends InjectableObject {
	void save(IShape shape);

	IShape find(String shapeAliasName);
	IShape delete(String shapeAliasName);
	List<IShape> findAll();
	List<IShape> findShapeContainsPoint(Point point);
}
