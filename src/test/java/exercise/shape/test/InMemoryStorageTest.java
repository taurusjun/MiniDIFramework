package exercise.shape.test;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Store;

import exercise.shape.InjectObjects.factory.IFactory;
import exercise.shape.InjectObjects.storage.IStorage;
import exercise.shape.InjectObjects.storage.InMemoryStorage;
import exercise.shape.assist.Point;
import exercise.shape.context.DefaultContext;
import exercise.shape.context.IContext;
import exercise.shape.def.IShape;
import exercise.shape.exception.InvalidShapeInitParamException;

public class InMemoryStorageTest {
	IContext context;
	IStorage storage;
	IFactory factory;
	final String basePackageName = "exercise.shape";

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
		context = new DefaultContext(basePackageName);
		storage=context.getInjectObject(IStorage.class);
		factory=context.getInjectObject(IFactory.class);
	}

	@Test
	public void testSave() throws InvalidShapeInitParamException {
		IShape object= factory.newShape("circle", new double[]{0.2,-0.9,1});
		storage.save(object);
		Assert.assertEquals(object, storage.find(object.getAliasName()));
	}

	@Test
	public void testFind() {
		IShape shape=storage.find(null);
		Assert.assertNull(shape);
	}

	@Test
	public void testFindAll() throws InvalidShapeInitParamException {
		IShape object1= factory.newShape("circle", new double[]{0.2,-0.9,1});
		storage.save(object1);
		IShape object2= factory.newShape("circle", new double[]{0.3,4.9,2});
		storage.save(object2);
		IShape object3= factory.newShape("circle", new double[]{0.4,42,2});
		storage.save(object3);
		List<IShape> shapes=storage.findAll();
		Assert.assertEquals(3, shapes.size());
		Assert.assertTrue(shapes.contains(object1));
		Assert.assertTrue(shapes.contains(object2));
		Assert.assertTrue(shapes.contains(object3));
	}

	@Test
	public void testFindShapeContainsPoint() throws InvalidShapeInitParamException {
		IShape circle= factory.newShape("circle", new double[] { 1, -2, 3 });
		IShape triangle= factory.newShape("Triangle", new double[] { 1, 1, 1, -1, -1, -1 });
		IShape donut= factory.newShape("donut", new double[] { 0, 0, 1, 2});
		storage.save(circle);
		storage.save(triangle);
		Point point = new Point(0.5, 0);
		List<IShape> shapes = storage.findShapeContainsPoint(point);
		Assert.assertTrue(shapes.contains(circle));
		Assert.assertTrue(shapes.contains(triangle));
		Assert.assertFalse(shapes.contains(donut));
	}

}
