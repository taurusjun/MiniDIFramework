package exercise.shape.test;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import exercise.shape.InjectObjects.service.IService;
import exercise.shape.InjectObjects.service.ShapeServiceResponse;
import exercise.shape.InjectObjects.storage.IStorage;
import exercise.shape.context.DefaultContext;
import exercise.shape.context.IContext;
import exercise.shape.def.Circle;
import exercise.shape.user.UserInterface;

public class UserInterfaceTest {

	Method dispatch;
	UserInterface userInterface;
	IContext context;
	IService service;
	IStorage storage;
	
	@Before
	public void setUp() throws Exception {
		BasicConfigurator.configure();
		userInterface = new UserInterface();
		context = new DefaultContext("exercise.shape");
		service = context.getInjectObject(IService.class);
		storage = context.getInjectObject(IStorage.class);
		ShapeServiceResponse response = service.getAvailableShapes();
		Set<String> shapeNames = (Set<String>) response.getObject();
		
		Field ctxField = UserInterface.class.getDeclaredField("context");
		ctxField.setAccessible(true);
		ctxField.set(userInterface, context);

		Field svsField = UserInterface.class.getDeclaredField("service");
		svsField.setAccessible(true);
		svsField.set(userInterface, service);

		Field shapeNamesField = UserInterface.class.getDeclaredField("shapeNames");
		shapeNamesField.setAccessible(true);
		shapeNamesField.set(userInterface, shapeNames);

		dispatch = UserInterface.class.getDeclaredMethod("dispatch",
				String.class);
		dispatch.setAccessible(true);
	}

	@Test
	public void testHelp() {
		String cmd = "help";
		String returnMsg = getReturnMessage(cmd);
		String expMsg = getExpectedOutputFromFile("test_help1.txt");
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "help circle";
		returnMsg = getReturnMessage(cmd);
		expMsg = "circle <centr.x> <centr.y> <radius>";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "help donut";
		returnMsg = getReturnMessage(cmd);
		expMsg = "donut <centr.x> <centr.y> <inner radius> <outer radius>";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "help triangle";
		returnMsg = getReturnMessage(cmd);
		expMsg = "triangle <vertice1.x> <vertice1.y> <vertice2.x> <vertice2.y> <vertice3.x> <vertice3.y>";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "help AAA";
		returnMsg = getReturnMessage(cmd);
		expMsg = "No buid instrction for the shape aaa";
		Assert.assertEquals(expMsg, returnMsg);
	}

	@Test
	public void testCreatShapeCircle() {
		String cmd = "circle 1 1 2";
		String returnMsg = getReturnMessage(cmd);
		String expMsg = "shape 1: circle with centre at (1.0,1.0) and radius 2.0";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "circle 1 1 ";
		returnMsg = getReturnMessage(cmd);
		expMsg = "Must initialize circle with 3 parameters.";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "circle 1 1 -2";
		returnMsg = getReturnMessage(cmd);
		expMsg = "InvalidParams:-2.0  Radius must be greater than zero.";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "circle w 1 -2";
		returnMsg = getReturnMessage(cmd);
		expMsg = "Not valid parameter:w, must be a number.\nEnter 'help circle' to see more.\n";
		Assert.assertEquals(expMsg, returnMsg);
	}

	@Test
	public void testCreatShapeDonut() {
		String cmd = "donut 1 1 1 2";
		String returnMsg = getReturnMessage(cmd);
		String expMsg = "shape 1: donut with centre at (1.0,1.0) and inner radius 1.0 outer radius 2.0";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "donut 1 1 ";
		returnMsg = getReturnMessage(cmd);
		expMsg = "Must initialize donut with 4 parameters.";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "donut 1 1 1 -2";
		returnMsg = getReturnMessage(cmd);
		expMsg = "InvalidParams:-2.0  Outer radius must be greater than zero.";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "donut 1 1 -1 2";
		returnMsg = getReturnMessage(cmd);
		expMsg = "InvalidParams:-1.0  Inner radius must be greater than zero.";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "donut 1 1 2 1 ";
		returnMsg = getReturnMessage(cmd);
		expMsg = "InvalidParams:2.0 1.0  Outer radius must be greater than Inner radius.";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "donut 1 1 q 1 ";
		returnMsg = getReturnMessage(cmd);
		expMsg = "Not valid parameter:q, must be a number.\nEnter 'help donut' to see more.\n";
		Assert.assertEquals(expMsg, returnMsg);
	}

	@Test
	public void testCreatShapeTriangle() {
		String cmd = "Triangle 1 1 -1 2 -4.2 -1.3";
		String returnMsg = getReturnMessage(cmd);
		String expMsg = "shape 1: triangle with 3 vertices at (1.0,1.0) (-1.0,2.0) (-4.2,-1.3)";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "Triangle 1 1 ";
		returnMsg = getReturnMessage(cmd);
		expMsg = "Must initialize triangle with 6 parameters.";
		Assert.assertEquals(expMsg, returnMsg);
		cmd = "Triangle 1 1w -1 2 -4.2 -1.3";
		returnMsg = getReturnMessage(cmd);
		expMsg = "Not valid parameter:1w, must be a number.\nEnter 'help triangle' to see more.\n";
		Assert.assertEquals(expMsg, returnMsg);
	}
	@Test
	public void testLoadFile() {
		String cmd = "load";
		String returnMsg = getReturnMessage(cmd);
		Circle circle=(Circle)storage.find("shape 1");
		Assert.assertEquals(circle.getCentre().getX(), 1,0);
		Assert.assertEquals(circle.getCentre().getY(), -2,0);
		Assert.assertEquals(circle.getRadius(), 3,0);
		cmd = "load src\\test\\resources\\test_loader.txt";
		returnMsg = getReturnMessage(cmd);
		circle=(Circle)storage.find("shape 3");
		Assert.assertEquals(circle.getCentre().getX(), 2,0);
		Assert.assertEquals(circle.getCentre().getY(), -2,0);
		Assert.assertEquals(circle.getRadius(), 3,0);
	}

	@Test
	public void testFindContainedShapesByPointHandler() {
		String cmd = "load";
		getReturnMessage(cmd);
		cmd = "0.5 0";
		String returnMsg = getReturnMessage(cmd);
		String expMsg = getExpectedOutputFromFile("test_findContainerShapeByPoint.txt");
		Assert.assertEquals(expMsg, returnMsg);
	}

	@Test
	public void testExit() {
		String cmd = "exit";
		String returnMsg = getReturnMessage(cmd);
		String expMsg = "Shape service stoped.";
		Assert.assertEquals(expMsg, returnMsg);
	}

	private String getReturnMessage(String cmd) {
		try {
			return (String) dispatch.invoke(userInterface, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private String getExpectedOutputFromFile(String fileName) {
		String path = "src\\test\\resources\\";
		File file = new File(path + fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			String s = new String(data, "UTF-8");
			s = s.replaceAll("\r", "");
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";

	}
}
