package exercise.shape.test;

import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import exercise.shape.InjectObjects.InjectableObject;
import exercise.shape.InjectObjects.factory.DefaultShapeFactory;
import exercise.shape.InjectObjects.factory.IFactory;
import exercise.shape.InjectObjects.service.DefaultShapeService;
import exercise.shape.InjectObjects.service.IService;
import exercise.shape.InjectObjects.storage.IStorage;
import exercise.shape.InjectObjects.storage.InMemoryStorage;
import exercise.shape.context.DefaultContext;
import exercise.shape.context.IContext;
import exercise.shape.def.Circle;
import exercise.shape.def.Donut;
import exercise.shape.def.IShape;
import exercise.shape.def.Triangle;

public class ContextTest {

	IContext context;
	final String basePackageName = "exercise.shape";

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
		context = new DefaultContext(basePackageName);
	}
	
	@Test
	public void testGetInjectObjectByName(){
		InjectableObject object=context.getInjectObject("shapeFactory");
		Assert.assertEquals(DefaultShapeFactory.class, object.getClass());
		object=context.getInjectObject("DefaultShapeFactory");
		Assert.assertNull(object);
		object=context.getInjectObject("IFactory");
		Assert.assertNull(object);
	}

	@Test
	public void testGetInjectObjectByType(){
		InjectableObject object=context.getInjectObject(IFactory.class);
		Assert.assertEquals(DefaultShapeFactory.class, object.getClass());
		object=context.getInjectObject(IService.class);
		Assert.assertEquals(DefaultShapeService.class, object.getClass());
		object=context.getInjectObject(IStorage.class);
		Assert.assertEquals(InMemoryStorage.class, object.getClass());
	}
	
	@Test
	public void testGetShapeMap() {
		Map<String, Class<? extends IShape>> map = context.getShapeMap();
		String[] testkeys = new String[] { "circle", "donut","triangle" };
		Class[] testClasses = new Class[] { Circle.class, Donut.class,Triangle.class };
		for (int i = 0; i < testkeys.length; i++) {
			Assert.assertEquals(testClasses[i], map.get(testkeys[i]));
		}
	}
}
