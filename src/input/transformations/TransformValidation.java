package input.transformations;

public class TransformValidation {

	private boolean valid;
	private String errormsg;
	
	public TransformValidation(boolean valid, String errormsg) {
		this.valid = valid;
		this.errormsg = errormsg;
	}
	
	public boolean isvalid() {
		return this.valid;
	}
	
	public String getErrorMsg() {
		return this.errormsg;
	}
	
}
