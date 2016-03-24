package util;

import input.block.InputBlock;
import input.block.TermFrequency;

/**
 * 
 * This is used to translate between InputBuilder and CloneDetection for InputBlock->String->DetectionBlock
 * 
 * In future can replace by InputBlock->DetectionBlock and use write object directly
 *
 */
public class SerializeBlocks {
	
	public static String getInputBlockString(InputBlock block) {
		String retval = ">" + block.getFileid() + "," + block.getStartline() + "," + block.getEndline() + "\n";
		for(TermFrequency tf : block.getTokens()) {
			retval += "\t" + tf.getFrequency() + ":" + tf.getTerm() + "\n";
		}
		return retval;
	}
	
	// public static DetectionBlock readDetectionBlock(String block, Long orderid, double sim) {
	//}
	
}
