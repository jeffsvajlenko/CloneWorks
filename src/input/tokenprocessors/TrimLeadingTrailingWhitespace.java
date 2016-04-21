package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

public class TrimLeadingTrailingWhitespace implements ITokenProcessor {

	public String toString() {
		return this.getClass().getName();
	}
	
	@Override
	public List<String> process(List<String> tokens) {
		List<String> retval = new LinkedList<String>();
		
		for(String token : tokens) {
			retval.add(token.trim());
		}
		
		return retval;
	}

}
