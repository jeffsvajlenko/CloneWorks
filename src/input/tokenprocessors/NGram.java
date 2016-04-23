package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

public class NGram implements ITokenProcessor {

	private int n;
	
	public NGram(String init) {
		this.n = Integer.parseInt(init);
	}
	
	public NGram(int n) {
		this.n = n;
	}	
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		String[] toks = new String[tokens.size()];
		toks = tokens.toArray(toks);
		
		List<String> retval = new LinkedList<String>();
		
		for(int i = 0; i <= toks.length-n; i++) {
			String ngram = toks[i];
			for(int j = 1; j < n; j++) {
				ngram += "\t" + toks[i+j];
			}
			retval.add(ngram);
		}
		
		return retval;
	}

	public String toString() {
		return this.getClass().getName() + " " + n;
	}
	
}
