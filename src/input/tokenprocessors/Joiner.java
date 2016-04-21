package input.tokenprocessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Joins tokens into format: token1 token2 token3 .
 *
 */
public class Joiner implements ITokenProcessor {

	public String toString() {
		return this.getClass().getName();
	}
	
	@Override
	public List<String> process(List<String> tokens) {
		List<String> retval = new ArrayList<String>(1);
		String join = "";
		for(String token : tokens) {
			join += token + " ";
		}
		retval.add(join);
		return retval;
	}

}
