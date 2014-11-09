package exercise.shape.exception;

public class InvalidShapeInitParamException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String detailMsg;

	public InvalidShapeInitParamException(String message){
		this.detailMsg=message;
	}
	
	public InvalidShapeInitParamException(double[] params,String message){
		StringBuilder msg=new StringBuilder();
		msg.append("InvalidParams:");
		for (double i : params) {
			msg.append(i);
			msg.append(" ");
		}
		msg.append(" "+message);
		this.detailMsg=msg.toString();
	}

	public String getDetailMsg() {
		return detailMsg;
	}
	
}
