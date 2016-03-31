package detection.index;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import detection.block.Block;


public class ConcurrentHashMapIndex extends AbstractIndex {

	private ConcurrentHashMap<String,Queue<Block>> index;
	
	public ConcurrentHashMapIndex(int initialsize) {
		this.index = new ConcurrentHashMap<String,Queue<Block>>(initialsize);
	}
	
	@Override
	public void put(String term, Block block) {
		index.putIfAbsent(term, new ConcurrentLinkedQueue<Block>());
		index.get(term).add(block);
	}

	@Override
	public Queue<Block> get(String term) {
		return index.get(term);
	}

	@Override
	public void clear() {
		index.clear();
	}
	
}
