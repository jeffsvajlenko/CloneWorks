package input.transformations;

public class TransformException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Transform transformation;
	private String message;
	
	public TransformException(Transform transformation, String message) {
		super(message);
		this.transformation = transformation;
		this.message = message;
	}
	
	public String message() {
		return this.message;
	}
	
	public Transform transformation() {
		return this.transformation;
	}
	
}
