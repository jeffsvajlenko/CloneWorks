package cwbuild.termprocessors;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Detects tokens with format ^".*"$ and replaces them with ^""$.
 *
 */
public class NormalizeStrings implements ITermProcessor {
	
	public NormalizeStrings(String init) {
	}
	
	public NormalizeStrings() {
	}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> retval = new LinkedList<String>();
		
		for(String str : tokens) {
			if(str.startsWith("\"") && str.endsWith("\"")) {
				retval.add("\"\"");
			} else {
				retval.add(str);
			}
		}
		
		return retval;
	}
	
	public String toString() {
		return this.getClass().getName();
	}

}
