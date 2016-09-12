package cwdetect.detection;

import java.util.List;

import cwdetect.block.Block;
import cwdetect.index.IIndex;

public interface CloneDetector {

		public List<Clone> detectClones(Block qBlock, IIndex index);
	
}
