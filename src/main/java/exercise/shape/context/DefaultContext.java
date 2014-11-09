package exercise.shape.context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.reflections.Reflections;

import exercise.shape.InjectObjects.InjectableObject;
import exercise.shape.InjectObjects.factory.IFactory;
import exercise.shape.InjectObjects.service.IService;
import exercise.shape.InjectObjects.service.ShapeServiceResponse;
import exercise.shape.InjectObjects.storage.IStorage;
import exercise.shape.annotation.Injectee;
import exercise.shape.annotation.Injectee.InjectMethods;
import exercise.shape.annotation.Injector;
import exercise.shape.assist.Point;
import exercise.shape.def.IShape;

public class DefaultContext implements IContext {

	private final Map<String, Class<? extends IShape>> shapeMap;
	private final Map<Class<? extends InjectableObject>, Class> injectableClassMap;
	private final Map<String, Class> injectableAliasNameMap;
	private final Map<String, InjectableObject> injectableObjectByStringMap;
	private final Map<Class<? extends InjectableObject>, InjectableObject> injectableObjectByTypeMap;
	private final Reflections reflections;
	static final Logger logger = Logger.getLogger(DefaultContext.class);

	public DefaultContext(String basePackageName) {
		reflections = new Reflections(basePackageName);

		injectableClassMap = new HashMap<Class<? extends InjectableObject>, Class>();
		injectableAliasNameMap = new HashMap<String, Class>();
		injectableObjectByStringMap = new HashMap<String, InjectableObject>();
		injectableObjectByTypeMap = new HashMap<Class<? extends InjectableObject>, InjectableObject>();
		// scan for interfaces derived from InjectableObject
		Set<Class<? extends InjectableObject>> injectClasses = reflections.getSubTypesOf(InjectableObject.class);
		logger.debug("Starting injectable objects...");
		for (Class<? extends InjectableObject> clazz : injectClasses) {
			if(clazz.isInterface()){
				this.scanForInjectableObjects(clazz);
			}
		}
		//Init injectable objects
		this.initInjectableObjects();
		logger.debug("Init injectable objects completed.");

		// Collect all shapes into a map
		logger.debug("Scanning shapes in system...");
		Set<Class<? extends IShape>> shapes = reflections.getSubTypesOf(IShape.class);

		shapeMap = new HashMap<String, Class<? extends IShape>>();
		for (Class<? extends IShape> shp : shapes) {
			shapeMap.put(shp.getSimpleName().toLowerCase(), shp);
			logger.debug("Find Shape: " + shp.getSimpleName());
		}
		logger.debug("All shapes are found.");
	}

	public <T extends InjectableObject> T getInjectObject(String name) {
		return (T) injectableObjectByStringMap.get(name);
	}

	public <T extends InjectableObject> T getInjectObject(Class<T> clazz) {

		return (T) injectableObjectByTypeMap.get(clazz);
	}

	public Map<String, Class<? extends IShape>> getShapeMap() {
		return shapeMap;
	}

	private void scanForInjectableObjects(
			Class<? extends InjectableObject> injectableClass) {
		logger.debug("Scanning info for:"+injectableClass.getName());
		Set<Class<?>> injectObjectClasses = reflections
				.getTypesAnnotatedWith(Injector.class);
		for (Class<?> clazz : injectObjectClasses) {
			if (injectableClass.isAssignableFrom(clazz)) {
				injectableClassMap.put(injectableClass, clazz);
				Injector injectorAnno = clazz.getAnnotation(Injector.class);
				if (injectorAnno.name().equals("")) {
					injectableAliasNameMap.put(clazz.getSimpleName(), clazz);
				} else {
					injectableAliasNameMap.put(injectorAnno.name(), clazz);
				}
			}
		}
	}

	private void initInjectableObjects() {
		Set<Class<? extends InjectableObject>> classSet = injectableClassMap
				.keySet();
		for (Class<? extends InjectableObject> clazz : classSet) {
			logger.debug("Init injectable object:"
					+ injectableClassMap.get(clazz).getName());
			initSingleInjectableObject(clazz);
		}
	}

	private InjectableObject initSingleInjectableObject(
			Class<? extends InjectableObject> clazz) {
		InjectableObject injectableObject = injectableObjectByTypeMap
				.get(clazz);
		if (injectableObject != null) {
			return injectableObject;
		}
		Class implClazz = injectableClassMap.get(clazz);
		try {
			InjectableObject object = (InjectableObject) implClazz
					.newInstance();
			injectableObjectByTypeMap.put(clazz, object);
			initSingleInjectableObjectFileds(implClazz, object);
			return object;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private InjectableObject initSingleInjectableObject(String injectorAliasName) {
		InjectableObject injectableObject = injectableObjectByStringMap
				.get(injectorAliasName);
		if (injectableObject != null) {
			return injectableObject;
		}
		Class implClazz = injectableAliasNameMap.get(injectorAliasName);
		try {
			InjectableObject object = (InjectableObject) implClazz
					.newInstance();
			// injectableObjectByTypeMap.put(implClazz, object);
			injectableObjectByStringMap.put(injectorAliasName, object);
			initSingleInjectableObjectFileds(implClazz, object);
			return object;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void initSingleInjectableObjectFileds(Class implClazz,
			InjectableObject object) {
		try {
			Field[] fields = implClazz.getDeclaredFields();
			for (Field field : fields) {
				Injectee injectAnno = field.getAnnotation(Injectee.class);
				if (injectAnno != null) {
					// Context is special handled
					if (field.getType().equals(IContext.class)) {
						field.setAccessible(true);
						field.set(object, this);
						logger.debug("Class "+implClazz.getSimpleName()+": Inject context");
					}
					if (injectAnno.wireMethod() == InjectMethods.Type) {
						if (injectableClassMap.keySet().contains(
								field.getType())) {
							Class<?> fieldType = field.getType();
							InjectableObject fieldValue = initSingleInjectableObject(fieldType
									.asSubclass(InjectableObject.class));
							// TODO if fieldvalue==null handle exception
							field.setAccessible(true);
							field.set(object, fieldValue);
							logger.debug("Class "+implClazz.getSimpleName()+": Inject value for field by type: "
									+ fieldType.getSimpleName());
						}
					} else if (injectAnno.wireMethod() == InjectMethods.Name) {
						InjectableObject fieldValue = initSingleInjectableObject(field
								.getName());
						// TODO if fieldvalue==null handle exception
						field.setAccessible(true);
						field.set(object, fieldValue);
						logger.debug("Class "+implClazz.getSimpleName()+": Inject value for field by name: "
								+ field.getName());

					}
				}
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
