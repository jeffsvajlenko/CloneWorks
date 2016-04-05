package detection.prefixer;

import java.util.ArrayList;

import detection.block.Block;
import detection.block.BlockElement;
import input.block.InputBlock;
import input.block.TermFrequency;

public class MyPrefixer implements Prefixer {

	private double sim;
	
	public MyPrefixer(double sim) {
		this.sim = sim;
	}

	@Override
	public Block prefix(InputBlock block) {
		int numTokens = block.numTokens();
		int prefixLength = numTokens - (int) Math.ceil(sim * numTokens) + 1;
		
		ArrayList<BlockElement> tfs = new ArrayList<BlockElement>(block.getTokens().size());
		
		int num = 0;
		boolean isPrefixTerm = true;
		for(TermFrequency tf : block.getTokens()) {
			if(num >= prefixLength)
				isPrefixTerm = false;
			num = num + tf.getFrequency();
			tfs.add(new BlockElement(tf.getTerm(), tf.getFrequency(), isPrefixTerm));
		}
		return new Block(block.getId(), block.getFileid(), block.getStartline(), block.getEndline(), tfs);
	}
	
}
