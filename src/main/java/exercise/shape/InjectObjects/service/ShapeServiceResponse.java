package exercise.shape.InjectObjects.service;

public final class ShapeServiceResponse {
	
	final boolean success;
	final Object object;
	final String message;

	public ShapeServiceResponse(boolean rslt, Object object, String message) {
		this.success = rslt;
		this.object = object;
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public Object getObject() {
		return object;
	}
	public String getMessage() {
		return message;
	}
	

}
