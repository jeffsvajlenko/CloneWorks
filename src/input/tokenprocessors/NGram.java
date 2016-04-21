package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

public class NGram implements ITokenProcessor {

	public String toString() {
		return this.getClass().getName() + " " + n;
	}
	
	private int n;
	
	public NGram(int n) {
		this.n = n;
	}
	
	@Override
	public List<String> process(List<String> tokens) {
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
	
	public static void main(String args[]) {
		LinkedList<String> tokens = new LinkedList<String>();
		tokens.add("a");
		tokens.add("b");
		tokens.add("c");
		tokens.add("d");
		tokens.add("e");
		tokens.add("f");
		tokens.add("g");
		tokens.add("h");
		tokens.add("i");
		tokens.add("j");
		tokens.add("k");
		tokens.add("l");
		tokens.add("m");
		tokens.add("n");
		tokens.add("o");
		
		NGram pngram = new NGram(4);
		for(String token : pngram.process(tokens)) {
			System.out.println("|" + token + "|");
		}
	}

}
