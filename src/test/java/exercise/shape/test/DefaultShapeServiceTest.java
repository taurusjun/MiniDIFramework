package exercise.shape.test;

import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import exercise.shape.InjectObjects.service.IService;
import exercise.shape.InjectObjects.service.ShapeServiceResponse;
import exercise.shape.assist.Point;
import exercise.shape.context.DefaultContext;
import exercise.shape.context.IContext;
import exercise.shape.def.Circle;
import exercise.shape.def.IShape;
import exercise.shape.def.Triangle;

public class DefaultShapeServiceTest {

	IContext context;
	IService service;
	final String basePackageName = "exercise.shape";

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
		context = new DefaultContext(basePackageName);
		service=context.getInjectObject(IService.class);
	}

	@Test
	public void testSaveNewShape() {
		ShapeServiceResponse response=service.saveNewShape("circle", new double[]{1.1,1.2,2.3});
		Assert.assertTrue(response.isSuccess());
		Circle circle=(Circle)response.getObject();
		Assert.assertEquals(1.1, circle.getCentre().getX(),0);
		Assert.assertEquals(1.2, circle.getCentre().getY(),0);
		Assert.assertEquals(2.3, circle.getRadius(),0);
		response=service.saveNewShape("circle", new double[]{1.1,1.2,-2.3});
		Assert.assertFalse(response.isSuccess());
		Assert.assertNull(response.getObject());
		String exp="InvalidParams:-2.3  Radius must be greater than zero.";
		String act=response.getMessage();
		Assert.assertEquals(exp,act);
	}

	@Test
	public void testFindShapeContainsPoint() {
		ShapeServiceResponse response=service.saveNewShape("circle", new double[]{1,-2,3});
		Circle circle=(Circle)response.getObject();
		response=service.saveNewShape("Triangle", new double[]{1,1,1,-1,-1,-1});
		Triangle triangle =(Triangle)response.getObject();
		
		Point p=new Point(0.5, 0);
		response=service.findShapeContainsPoint(p);
		Collection<IShape> shapes=(Collection<IShape>)response.getObject();
		Assert.assertTrue(response.isSuccess());
		Assert.assertEquals(2, shapes.size());
		Assert.assertTrue(shapes.contains(circle));
		Assert.assertTrue(shapes.contains(triangle));
	}

	@Test
	public void testFindShape() {
		ShapeServiceResponse response=service.saveNewShape("circle", new double[]{1,-2,3});
		Circle circle=(Circle)response.getObject();
		Circle circle2=(Circle)service.findShape(circle.getAliasName()).getObject();
		Assert.assertEquals(circle, circle2);
	}

}
