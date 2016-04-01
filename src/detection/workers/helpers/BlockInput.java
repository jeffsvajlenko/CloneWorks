package detection.workers.helpers;

import detection.block.Block;

public interface BlockInput {

	public Block take();
	public boolean isPoisoned();
	
}
