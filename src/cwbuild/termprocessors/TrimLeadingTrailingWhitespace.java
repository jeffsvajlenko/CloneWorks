package cwbuild.termprocessors;

import java.util.LinkedList;
import java.util.List;

public class TrimLeadingTrailingWhitespace implements ITermProcessor {

	public TrimLeadingTrailingWhitespace(String init) {
		this();
	}
	
	public TrimLeadingTrailingWhitespace() {}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> retval = new LinkedList<String>();
		
		for(String token : tokens) {
			retval.add(token.trim());
		}
		
		return retval;
	}
	
	public String toString() {
		return this.getClass().getName();
	}

}
