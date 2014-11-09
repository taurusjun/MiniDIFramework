package exercise.shape.InjectObjects.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import exercise.shape.annotation.Injectee;
import exercise.shape.annotation.Injector;
import exercise.shape.context.IContext;
import exercise.shape.def.IShape;
import exercise.shape.exception.InvalidShapeInitParamException;

@Injector(name="shapeFactory")
public class DefaultShapeFactory implements IFactory {
	@Injectee
	IContext context;
	private volatile int index=0;
	Logger logger=Logger.getLogger(DefaultShapeFactory.class);

	public IShape newShape(String shapeName, double[] params) throws InvalidShapeInitParamException {
		Class<? extends IShape> shapeClass = context.getShapeMap().get(
				shapeName.toLowerCase());
		double[] cls = new double[] {};
		try {

			Constructor<? extends IShape> constructor = shapeClass
					.getConstructor(cls.getClass(),String.class);
			index++;
			String aliasName="shape "+index;
			IShape shape=constructor.newInstance(params,aliasName);
			logger.debug("Shape "+shapeName+" created, unique name:"+aliasName);
			return shape;
		} catch (InvocationTargetException e) {
			if(e.getCause().getClass().equals(InvalidShapeInitParamException.class)){
				throw (InvalidShapeInitParamException)(e.getCause());
			}
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
