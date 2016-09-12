package cwbuild.transformations;

import java.nio.file.Files;

public class Transform implements ITransform {

	private String exec;
	private String params;
	private boolean exists;
	
	public Transform(String exec, String params) {
		this.exec = exec;
		this.params = params;
		this.exists = Files.exists(TransformUtil.getTransformationRoot().resolve(exec));
	}
	
	/* (non-Javadoc)
	 * @see input.transformations.ITransformation#getExec()
	 */
	@Override
	public String getExec() {
		return this.exec;
	}
	
	/* (non-Javadoc)
	 * @see input.transformations.ITransformation#params()
	 */
	@Override
	public String params() {
		return this.params;
	}
	
	/* (non-Javadoc)
	 * @see input.transformations.ITransformation#exists()
	 */
	@Override
	public boolean exists() {
		return this.exists;
	}
	
	@Override
	public String toString() {
		return "Transform[" + exec + "," + params + "]";
	}
	
}
