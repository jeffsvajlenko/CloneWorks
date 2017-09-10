package input.termprocessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class API implements ITermProcessor {
	public API(String config) {
		
	}

	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		HashSet<String> uterms = new HashSet<String>();
		
		for(String term : tokens) {
			uterms.add(term);
		}
		
		ArrayList<String> ntokens = new ArrayList<String>(uterms);
		
		return ntokens;
	}
	
	
	
}
