package exercise.shape.InjectObjects.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import exercise.shape.annotation.Injector;
import exercise.shape.assist.Point;
import exercise.shape.def.IShape;

@Injector
public class InMemoryStorage implements IStorage {

	Logger logger=Logger.getLogger(InMemoryStorage.class);
	private final Map<String,IShape> shapes;

	public InMemoryStorage() {
		shapes = new HashMap<String, IShape>();
	}

	public void save(IShape shape) {
		String aliasName=shape.getAliasName();
		if(shapes.containsKey(aliasName)){
			logger.warn("Shape name:"+aliasName+" already exist.");
		}
		shapes.put(aliasName,shape);
	}

	public List<IShape> findAll() {
		return new ArrayList<IShape>(shapes.values());
	}

	public List<IShape> findShapeContainsPoint(Point point) {
		final Vector<IShape> shapeVector = new Vector<IShape>(shapes.values());
		final Vector<IShape> foundShapeVector = new Vector<IShape>();

		logger.debug("Prepare to search point "+point.toString()+"in all "+shapeVector.size()+" shapes.");
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			Runnable runnable = new FindShapeTask(point, shapeVector,
					foundShapeVector);
			logger.debug("Initilize find shape task "+i);
			executorService.submit(runnable);
		}
		logger.debug("All 10 shape-found task submitted, waiting search result.");

		while (shapeVector.size() > 0) {
			try {
				logger.debug("Sleep 1s and wait for tasks done. ");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.debug("Searching finished, try to shutdown multi-thread service and return.");
		executorService.shutdown();
		try {
			executorService.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return foundShapeVector;
	}

	public IShape find(String shapeAliasName) {
		return shapes.get(shapeAliasName);
	}

	public IShape delete(String shapeAliasName) {
		return shapes.remove(shapeAliasName);
	}

}
