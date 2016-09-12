package cwdetect.block;

import java.io.Serializable;

public class BlockElement implements Serializable {
	
	private static final long serialVersionUID = -6670844813776664586L;
	
	private String term;
	private int frequency;
	private boolean prefixTerm;
	
	public BlockElement(String term, int frequency, boolean prefixTerm) {
		super();
		this.term = term;
		this.frequency = frequency;
		this.prefixTerm = prefixTerm;
	}

	public String getTerm() {
		return term;
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean isPrefixTerm() {
		return prefixTerm;
	}
}
