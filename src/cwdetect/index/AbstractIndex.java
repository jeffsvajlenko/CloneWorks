package cwdetect.index;

import cwdetect.block.Block;
import cwdetect.block.BlockElement;

public abstract class AbstractIndex implements IIndex {

	public void put(Block block) {
		for(BlockElement be : block.getBlockAsList()) {
			if(be.isPrefixTerm())
				this.put(be.getTerm(), block);
		}
	}

}
