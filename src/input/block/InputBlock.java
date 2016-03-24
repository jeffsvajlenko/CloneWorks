package input.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputBlock {
	
	private long fileid;
	private int startline;
	private int endline;
	private int numtokens;
	private List<TermFrequency> tokens;
	
	public InputBlock(long fileid, int startline, int endline, List<String> tokens) {
		this.fileid = fileid;
		this.startline = startline;
		this.endline = endline;
		
		// Convert to TermFrequency list
			// Count frequency of each term
		Map<String,Integer> map = new HashMap<String,Integer>(tokens.size());
		for(String token : tokens) {
			Integer val = map.get(token);
			if(val == null)
				map.put(token, 1);
			else
				map.put(token,val+1);
		}
			// Build term-frequency list
		this.tokens = new ArrayList<TermFrequency>(map.size());
		for(String token : map.keySet()) {
			this.tokens.add(new TermFrequency(token, map.get(token)));
		}
			// Make unmodifiable for immutability
		this.tokens = Collections.unmodifiableList(this.tokens);
		
		// Count number of tokens
		this.numtokens = 0;
		for(TermFrequency tf : this.tokens) {
			numtokens += tf.getFrequency();
		}
	}

	public long getFileid() {
		return fileid;
	}

	public int getStartline() {
		return startline;
	}

	public int getEndline() {
		return endline;
	}
	
	public int numLines() {
		return endline-startline+1;
	}

	public int numTokens() {
		return numtokens;
	}

	/**
	 * Immutable.  Not in any particular order.
	 * @return
	 */
	public List<TermFrequency> getTokens() {
		return tokens;
	}
	
	
	
}
