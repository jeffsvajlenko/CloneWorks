package detection.GTF;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GTFHashMap implements IGlobalTermFrequency {

	private Map<String, AtomicLong> map;
	
	public GTFHashMap(int initialCapacity) {
		this.map = new ConcurrentHashMap<String, AtomicLong>(initialCapacity);
	}
	
	@Override
	public void add(String term) {
		AtomicLong value = map.putIfAbsent(term, new AtomicLong(1));
		if(value != null)
			value.incrementAndGet();
	}

	@Override
	public long getFrequency(String term) {
		AtomicLong value = map.get(term);
		if(value == null)
			return 0;
		else
			return value.get();
	}

	@Override
	public void add(String term, long num) {
		AtomicLong value = map.putIfAbsent(term, new AtomicLong(num));
		if(value != null)
			value.addAndGet(num);
	}
	
	public Set<String> getKeySet() {
		return map.keySet();
	}
	
}
