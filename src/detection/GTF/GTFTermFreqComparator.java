package detection.GTF;
import java.util.Comparator;

import input.block.TermFrequency;

/**
 * 
 * Only use on a GTF no longer being modified.
 *
 */
public class GTFTermFreqComparator implements Comparator<TermFrequency> {

	private IGlobalTermFrequency gtf;
	
	public GTFTermFreqComparator(IGlobalTermFrequency gtf) {
		this.gtf = gtf;
	}
	
	@Override
	public int compare(TermFrequency tf1, TermFrequency tf2) {
		if(tf1.getFrequency() != tf2.getFrequency()) // Compare by Frequency
			return Long.compare(gtf.getFrequency(tf1.getTerm()), gtf.getFrequency(tf2.getTerm()));
		else
			return tf1.getTerm().compareTo(tf2.getTerm()); // If same frequency, compare terms for definite order
	}

}
