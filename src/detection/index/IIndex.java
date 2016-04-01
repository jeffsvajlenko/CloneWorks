package detection.index;

import java.util.Queue;
import java.util.Set;

import detection.block.Block;

public interface IIndex {
	public void put(Block block);
	public void put(String term, Block block);
	public void clear();
	public Queue<Block> get(String term);
	public Set<String> getTerms();
}
