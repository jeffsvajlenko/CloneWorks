package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Splits the contents of strings into tokens.  So [{"Hello, World!"}] -> [{Hello,},{World!}] 
 *
 */
public class SplitStrings implements ITokenProcessor {

	public SplitStrings(String init) {
		this();
	}
	
	public SplitStrings() {}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> retval = new LinkedList<String>();
		for(String str : tokens) {
			if(str.startsWith("\"") && str.endsWith("\"") && str.length() > 2) {
				str = str.substring(1, str.length()-1);
				for(String str2 : str.split("\\s+")) {
					retval.add(str2);
				}
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
