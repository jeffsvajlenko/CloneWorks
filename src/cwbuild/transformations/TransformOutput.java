package cwbuild.transformations;

import java.util.List;

public class TransformOutput {

	private boolean success;
	private List<String> output;
	private Exception exception;
	
	public TransformOutput(boolean success, List<String> output, Exception exception) {
		this.success = success;
		this.output = output;
		this.exception = exception;
	}
	
	public boolean success() {
		return this.success;
	}
	
	public List<String> output() {
		return this.output;
	}
	
	public Exception exception() {
		return this.exception;
	}
	
}
