package input.termprocessors;

import java.util.LinkedList;
import java.util.List;

public class ToLowerCase implements ITermProcessor {

	public ToLowerCase(String init) {
		this();
	}
	
	public ToLowerCase() {}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> retval = new LinkedList<String>();
		
		for(String str : tokens) {
			retval.add(str.toLowerCase());
		}
		
		return retval;
	}
	
	public String toString() {
		return this.getClass().getName();
	}

}
