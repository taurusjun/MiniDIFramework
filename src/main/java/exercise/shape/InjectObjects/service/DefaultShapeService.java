package exercise.shape.InjectObjects.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import exercise.shape.InjectObjects.factory.IFactory;
import exercise.shape.InjectObjects.storage.IStorage;
import exercise.shape.annotation.Injectee;
import exercise.shape.annotation.Injectee.InjectMethods;
import exercise.shape.annotation.Injector;
import exercise.shape.assist.Point;
import exercise.shape.context.IContext;
import exercise.shape.def.IShape;
import exercise.shape.exception.InvalidShapeInitParamException;

@Injector
public class DefaultShapeService implements IService {
	@Injectee(wireMethod = InjectMethods.Name)
	IFactory shapeFactory;
	@Injectee
	IStorage storage;
	@Injectee
	IContext context;

	final String Build_Instruction_Method_Name = "buildInstructions";

	public ShapeServiceResponse saveNewShape(String shapeName, double[] params) {
		ShapeServiceResponse response;
		IShape newShape;
		try {
			newShape = shapeFactory.newShape(shapeName, params);
			storage.save(newShape);
			response = new ShapeServiceResponse(true, newShape, "");
		} catch (InvalidShapeInitParamException e) {
			response = new ShapeServiceResponse(false, null, e.getDetailMsg());
		}
		return response;
	}

	@Override
	public ShapeServiceResponse findShape(String shapeName) {
		ShapeServiceResponse response;
		IShape shape=storage.find(shapeName);
		if(shape==null){
			response = new ShapeServiceResponse(false, null, "No such shape with the name:"+shapeName);
		}else{
			response = new ShapeServiceResponse(true, shape, "");
		}
		return response;
	}

	@Override
	public ShapeServiceResponse deleteShape(String shapeName) {
		ShapeServiceResponse response;
		IShape shape=storage.delete(shapeName);
		if(shape==null){
			response = new ShapeServiceResponse(false, null, "No such shape with the name:"+shapeName);
		}else{
			response = new ShapeServiceResponse(true, shape, "");
		}
		return response;
	}

	public ShapeServiceResponse findShapeContainsPoint(Point point) {
		List<IShape> shapes = storage.findShapeContainsPoint(point);
		ShapeServiceResponse response = new ShapeServiceResponse(true, shapes,
				"");
		return response;
	}

	public ShapeServiceResponse getBuidShapeInstructions(String shapeName) {
		ShapeServiceResponse response;
		Map<String, Class<? extends IShape>> shapes = context.getShapeMap();
		Class<? extends IShape> shape = shapes.get(shapeName);
		if (shape == null) {
			return new ShapeServiceResponse(false, null,
					"No buid instrction for the shape " + shapeName);
		}
		String inst;
		try {
			Method buildInstructions = shape
					.getMethod(Build_Instruction_Method_Name);
			inst = (String) buildInstructions.invoke(null, null);
			response = new ShapeServiceResponse(true, inst, "");
		} catch (Exception e) {
			e.printStackTrace();
			response = new ShapeServiceResponse(false, null,
					"No buid instrction for the shape " + shapeName);
		}
		return response;
	}

	public ShapeServiceResponse getBuidShapeInstructions() {
		ShapeServiceResponse response;
		Map<String, Class<? extends IShape>> shapes = context.getShapeMap();
		Collection<Class<? extends IShape>> shapeClasses = shapes.values();
		boolean hasInst = false;

		StringBuilder instBuilder = new StringBuilder();
		for (Class<? extends IShape> clazz : shapeClasses) {
			try {
				Method buildInstructions = clazz
						.getMethod(Build_Instruction_Method_Name);
				String inst = (String) buildInstructions.invoke(null, null);
				instBuilder.append(inst);
				instBuilder.append("\n");
				hasInst = true;
			} catch (Exception e) {
			}
		}
		if (hasInst) {
			response = new ShapeServiceResponse(true, instBuilder.toString(),
					"");
		} else {
			response = new ShapeServiceResponse(false, null, "No instrctions ");
		}
		return response;
	}

	public ShapeServiceResponse getAvailableShapes() {
		ShapeServiceResponse response;
		Map<String, Class<? extends IShape>> shapes = context.getShapeMap();
		Set<String> shapeNames = shapes.keySet();
		if (shapeNames == null || shapeNames.size() == 0) {
			response = new ShapeServiceResponse(false, null,
					"No shapes defined");
		} else {
			response = new ShapeServiceResponse(true, shapeNames, "");
		}

		return response;
	}

}
