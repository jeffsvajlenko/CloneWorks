package cwbuild.termprocessors;

import java.util.LinkedList;
import java.util.List;

public class RemoveEmpty implements ITermProcessor {

	public RemoveEmpty(String init) {
		this();
	}
	
	public RemoveEmpty() {}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> retval = new LinkedList<String>();
		for(String str : tokens) {
			if(!str.equals("")) {
				retval.add(str);
			}
		}
		return retval;
	}
	
	public String toString() {
		return this.getClass().getName();
	}

}
