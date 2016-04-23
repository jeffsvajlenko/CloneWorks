package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

public class Stemmer implements ITokenProcessor {

	public Stemmer(String init) {
		this();
	}
	
	public Stemmer() {}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> retval = new LinkedList<String>();
		for(String str : tokens) {
			util.Stemmer stemmer = new util.Stemmer();
			
			stemmer.add(str.toCharArray(), str.length());
			stemmer.stem();
			retval.add(stemmer.toString());
		}
		return retval;
	}
	
	public String toString() {
		return this.getClass().getName();
	}

}
