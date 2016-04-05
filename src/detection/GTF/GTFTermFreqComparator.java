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
		int lcomp = Long.compare(gtf.getFrequency(tf1.getTerm()), gtf.getFrequency(tf2.getTerm()));
		if(lcomp == 0) { // Global frequency is the same, order by term (to ensure definite order)
			return tf1.getTerm().compareTo(tf2.getTerm());
		} else {
			return lcomp;
		}
	}

}
