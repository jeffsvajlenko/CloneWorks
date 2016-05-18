package detection.DF;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import input.block.InputBlock;
import input.block.TermFrequency;

public class DFHashMap implements IDocumentFrequency {

	private Map<String,AtomicLong> map;
	int numdocs;
	
	public DFHashMap(int initialcap) {
		this.map = new ConcurrentHashMap<String,AtomicLong>(initialcap);
		this.numdocs = 0;
	}
	
	private void add(String term) {
		AtomicLong val = map.putIfAbsent(term, new AtomicLong(1L));
		if(val != null) {
			val.incrementAndGet();
		}
	}

	@Override
	public long num(String term) {
		AtomicLong val = map.get(term);
		if(val != null)
			return val.get();
		else
			return 0L;
	}

	@Override
	public void add(InputBlock block) {
		numdocs++;
		for(TermFrequency tf : block.getTokens()) {
			add(tf.getTerm());
		}
	}

	@Override
	public long numDocs() {
		return numdocs;
	}
	
}
