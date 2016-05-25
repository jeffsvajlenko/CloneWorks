package input.transformations;

public class TransformChain {
	
	private String chain;
	
	protected TransformChain(String chain) {
		this.chain = chain;
	}
	
	public String get() {
		return chain;
	}
	
}
