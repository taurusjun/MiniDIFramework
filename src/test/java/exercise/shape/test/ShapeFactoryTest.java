package exercise.shape.test;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import exercise.shape.InjectObjects.factory.IFactory;
import exercise.shape.assist.Point;
import exercise.shape.context.DefaultContext;
import exercise.shape.context.IContext;
import exercise.shape.def.Circle;
import exercise.shape.def.Donut;
import exercise.shape.def.IShape;
import exercise.shape.def.Triangle;
import exercise.shape.exception.InvalidShapeInitParamException;

public class ShapeFactoryTest {

	IContext context;
	IFactory factory;
	final String basePackageName = "exercise.shape";

	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();

		context = new DefaultContext(basePackageName);
		factory=context.getInjectObject(IFactory.class);
	}
	
	//-------Circle test--------------		
	@Test
	public void testCreatCircle() throws InvalidShapeInitParamException {
		IShape object= factory.newShape("circle", new double[]{0.2,-0.9,1});
		Assert.assertEquals(Circle.class, object.getClass());
		Circle circle=(Circle)object;
		Assert.assertEquals(0.2, circle.getCentre().getX(),0);
		Assert.assertEquals(-0.9, circle.getCentre().getY(),0);
		Assert.assertEquals(1, circle.getRadius(),0);
	}

	@Rule
	public ExpectedException thown= ExpectedException.none();
	
	@Test
	public void testCreatCircleExceptions1() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Circle circle;
		//negitive radius
		circle= (Circle)factory.newShape("circle", new double[]{0.2,-0.9,-1});
	}
	
	@Test
	public void testCreatCircleExceptions2() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Circle circle;
		//zero radius
		circle= (Circle)factory.newShape("circle", new double[]{0.2,-0.9,-1});
	}
	
	//-------Donut test--------------		
	@Test
	public void testCreatDonut() throws InvalidShapeInitParamException {
		IShape object= factory.newShape("Donut", new double[]{0.2,-0.9,1.3,2.1});
		Assert.assertEquals(Donut.class, object.getClass());
		Donut donut=(Donut)object;
		Assert.assertEquals(0.2, donut.getCentre().getX(),0);
		Assert.assertEquals(-0.9, donut.getCentre().getY(),0);
		Assert.assertEquals(2.1, donut.getOutRadius(),0);
		Assert.assertEquals(1.3, donut.getInnerRadius(),0);
	}

	@Test
	public void testCreatDonutExceptions1() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Donut donut;
		//negitive outer radius
		donut= (Donut)factory.newShape("donut", new double[]{0.2,-0.9,-1.3,2.1});
	}
	
	@Test
	public void testCreatDonutExceptions2() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Donut donut;
		//zero outer radius
		donut= (Donut)factory.newShape("donut", new double[]{0.2,-0.9,0,2.1});
	}

	@Test
	public void testCreatDonutExceptions3() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Donut donut;
		//negitive inner radius
		donut= (Donut)factory.newShape("donut", new double[]{0.2,-0.9,1.3,-2.1});
	}
	
	@Test
	public void testCreatDonutExceptions4() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Donut donut;
		//zero inner radius
		donut= (Donut)factory.newShape("donut", new double[]{0.2,-0.9,1.3,0});
	}

	@Test
	public void testCreatDonutExceptions5() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Donut donut;
		//outer radius less than inner radius
		donut= (Donut)factory.newShape("donut", new double[]{0.2,-0.9,3.3,2.1});
	}

	@Test
	public void testCreatDonutExceptions6() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Donut donut;
		//outer radius equal with inner radius
		donut= (Donut)factory.newShape("donut", new double[]{0.2,-0.9,2.1,2.1});
	}

	//-------Triangle test--------------		
	@Test
	public void testCreatTriangle() throws InvalidShapeInitParamException {
		IShape object= factory.newShape("Triangle", new double[]{0.2,-0.9,2.1,1.3,2.6,-4});
		Assert.assertEquals(Triangle.class, object.getClass());
		Triangle triangle=(Triangle)object;
		Point[] points=triangle.getAngles();
		Assert.assertEquals(0.2, points[0].getX(),0);
		Assert.assertEquals(-0.9, points[0].getY(),0);
		Assert.assertEquals(2.1, points[1].getX(),0);
		Assert.assertEquals(1.3, points[1].getY(),0);
		Assert.assertEquals(2.6, points[2].getX(),0);
		Assert.assertEquals(-4, points[2].getY(),0);
	}

	@Test
	public void testCreatTriangleExceptions() throws InvalidShapeInitParamException {
		thown.expect(InvalidShapeInitParamException.class);
		Triangle triangle;
		triangle= (Triangle)factory.newShape("triangle", new double[]{0.2,-0.9,-2.1,1.3,2.6});
	}
}
