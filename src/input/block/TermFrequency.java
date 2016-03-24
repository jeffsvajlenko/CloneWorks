package input.block;

import java.io.Serializable;

/**
 * 
 * Automatically interns the term to prevent memory duplication.
 * Equals and HashMap consider only the term, not the frequency.
 *
 */
public class TermFrequency implements Serializable {
	
	private static final long serialVersionUID = 5434346843350980013L;
	
	private String term;
	private int frequency;
	
	public TermFrequency(String term, int frequency) {
		super();
		this.term = term.intern();
		this.frequency = frequency;
	}

	public String getTerm() {
		return term;
	}

	public int getFrequency() {
		return frequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((term == null) ? 0 : term.hashCode());
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
		TermFrequency other = (TermFrequency) obj;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.term + ":" + this.frequency;
	}
	
}
