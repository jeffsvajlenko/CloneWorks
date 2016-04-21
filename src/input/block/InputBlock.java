package input.block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputBlock implements Serializable {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endline;
		result = prime * result + (int) (fileid ^ (fileid >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + numtokens;
		result = prime * result + startline;
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InputBlock other = (InputBlock) obj;
		if (endline != other.endline)
			return false;
		if (fileid != other.fileid)
			return false;
		if (id != other.id)
			return false;
		if (numtokens != other.numtokens)
			return false;
		if (startline != other.startline)
			return false;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
	
	private long id;
	private long fileid;
	private int startline;
	private int endline;
	private int numtokens;
	private List<TermFrequency> tokens;
	
	public InputBlock(long fileid, int startline, int endline, ArrayList<TermFrequency> tokens, int numtokens) {
		this.id = -1;
		
		this.fileid = fileid;
		this.startline = startline;
		this.endline = endline;
		this.tokens = tokens;
		this.numtokens = numtokens;
	}
	
	public InputBlock(long fileid, int startline, int endline, List<String> tokens)  {
		this.id = -1;
		
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

	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		if(id < 0)
			throw new IllegalArgumentException("id must be >= 0.");
		this.id = id;
	}
	
	public void unsetId() {
		this.id = -1;
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
	
	public void sort(Comparator<TermFrequency> comparator) {
		Collections.sort(this.tokens, comparator);
	}

	public static String getInputBlockString(InputBlock block) {
		String retval = block.getId() + "," + block.getFileid() + "," + block.getStartline() + "," + block.getEndline() + "," + block.numTokens() + "\n";
		for(TermFrequency tf : block.getTokens()) {
			retval += "\t" + tf.getFrequency() + ":" + tf.getTerm() + "\n";
		}
		return retval;
	}
	
	public static InputBlock readDetectionBlock(String block) {
		String[] parts = block.split("\n",2);
		String[] header = parts[0].split(",");
		String[] tfs = parts[1].split("\n");
		
		long id = Long.parseLong(header[0]);
		long fileid = Long.parseLong(header[1]);
		int startline = Integer.parseInt(header[2]);
		int endline = Integer.parseInt(header[3]);
		int numtokens = Integer.parseInt(header[4]);
		
		ArrayList<TermFrequency> tokens = new ArrayList<TermFrequency>(tfs.length);
		for(String tf : tfs) {
			tf = tf.substring(1);
			String[] term_freq = tf.split(":",2);
			int frequency = Integer.parseInt(term_freq[0]);
			String term = term_freq[1];
			tokens.add(new TermFrequency(term,frequency));
		}
		
		InputBlock iblock = new InputBlock(fileid, startline, endline, tokens, numtokens);
		iblock.setId(id);
		return iblock;
	}
	
}
