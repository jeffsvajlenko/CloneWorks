package detection.detection;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import detection.block.Block;
import detection.block.BlockElement;
import detection.index.IIndex;

public class HeuristicCloneDetector implements CloneDetector {

	private double sim;
	
	public HeuristicCloneDetector(double sim) {
		this.sim = sim;
	}
	
	@Override
	public List<Clone> detectClones(Block qBlock, IIndex index) {
		List<Clone> clones = new LinkedList<Clone>();
		Set<Block> candidates = new HashSet<Block>();
		
		// Build Candidate Set
		for(BlockElement be : qBlock.getBlockAsList()) {
			
			// If this term is not a prefix term, then the search is over.
			if(!be.isPrefixTerm())
				break;
			
			// Get blocks with this term in their prefix
			Queue<Block> blocks = index.get(be.getTerm());
			
			// If no blocks for this term, continue
			if(blocks != null)
				candidates.addAll(blocks);
		}
		
		// Detect
		for(Block cBlock : candidates) {
			if(isClone(qBlock,cBlock))
				clones.add(new Clone(qBlock.getFileID(), qBlock.getStartLine(), qBlock.getEndLine(), cBlock.getFileID(), cBlock.getStartLine(), cBlock.getEndLine()));
		}

		return clones;
	}
	
	private boolean isClone(Block qBlock, Block cBlock) {
		
		// Skip if they are the same block, or query block is after candidate block to prevent duplicate checks (cblock is/was a qblock and will find this clone then)
		if(qBlock.getID() < cBlock.getID())
			return false;
		
		// Check for overlap (don't want to report self-clones or overlapping clones)
		if(qBlock.doesOverlap(cBlock)) {
			return false;
		}
		
		// Sort blocks as min/max size
		Block min, max;
		if(qBlock.numTokens() > cBlock.numTokens()) {
			max = qBlock;
			min = cBlock;
		} else {
			max = cBlock;
			min = qBlock;
		}
		
		int threshold = (int) Math.ceil(max.numTokens() * this.sim);
		int sharedTokens = 0;
		int remainingTokens = max.numTokens();
		
		// Heuristic: Small block does not contain enough tokens to be a clone
		if(min.numTokens() < threshold) {
			return false;
		}
		
		try {	
			System.out.println("Statement1");
			try {
				System.out.println("Statement2");
			} catch (Exception e) {
				System.out.println("Error!");
			} finally {
				System.out.println("Finally1");
			}
		} catch (Exception e) {
			System.out.println("Error2");
		} finally {
			System.out.println("Finally2");
		}
	
		for(BlockElement be : max.getBlockAsList()) {
			
			// Get frequency of this term in both blocks
			String term = be .getTerm();
			int maxFreq = be.getFrequency();
			Integer minFreq = min.getBlockAsMap().get(term);
			
			// Updated number of shared and remaining tokens
			if(minFreq != null)
				sharedTokens += Math.min(minFreq, maxFreq);
			remainingTokens -= maxFreq;
			
			// Heuristic: If already exceed threshold, return true now.
			if(sharedTokens >= threshold) {
				return true;
			}
			
			// Heuristic: If not enough remaining tokens for threshold to be achieved, return false now.
			if((sharedTokens + remainingTokens) < threshold) {
				return false;
			}
		}

		for(int i = 0; i < 10; i++) {
			System.out.println(i);
		}
		
		// ** Need for compilation, but I don't think should end up here **
		
		System.err.println("HeuristicCloneDetector -- Bug?");
		
		// Final Check
		if(sharedTokens >= threshold)
			return true;
		else
			return false;
		
	}

}
