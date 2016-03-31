package detection.detection;

import java.util.List;

import detection.block.Block;
import detection.index.IIndex;

public interface CloneDetector {

		public List<Clone> detectClones(Block qBlock, IIndex index);
	
}
