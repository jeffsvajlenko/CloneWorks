package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

public class Stemmer implements ITokenProcessor {

	@Override
	public List<String> process(List<String> tokens) {
		List<String> retval = new LinkedList<String>();
		for(String str : tokens) {
			util.Stemmer stemmer = new util.Stemmer();
			
			stemmer.add(str.toCharArray(), str.length());
			stemmer.stem();
			retval.add(stemmer.toString());
		}
		return retval;
	}

}
