package cwdetect.workers.helpers;

import cwdetect.block.Block;

public interface BlockInput {

	public Block take();
	public boolean isPoisoned();
	
}
