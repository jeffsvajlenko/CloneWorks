package detection.workers;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import detection.block.Block;
import detection.detection.Clone;
import detection.detection.CloneDetector;
import detection.index.IIndex;
import detection.prefixer.Prefixer;
import detection.requirements.Requirements;
import input.block.InputBlock;
import input.block.TermFrequency;
import util.blockingqueue.IEmitter;

public class CloneDetection implements Runnable {

	private InputBlockInput input;
	private IEmitter<Clone> output;
	
	private IIndex index;
	private Comparator<TermFrequency> sorter;
	private Prefixer prefixer;
	private Requirements requirements;
	private CloneDetector detector;
	
	public CloneDetection(InputBlockInput input, IEmitter<Clone> output, IIndex index, Comparator<TermFrequency> sorter, Prefixer prefixer, Requirements requirements, CloneDetector detector) {
		Objects.requireNonNull(input);
		Objects.requireNonNull(output);
		Objects.requireNonNull(index);
		//Objects.requireNonNull(sorter);
		Objects.requireNonNull(prefixer);
		Objects.requireNonNull(requirements);
		
		this.input = input;
		this.output = output;
		
		this.index = index;
		this.sorter = sorter;
		this.prefixer = prefixer;
		this.requirements = requirements;
		this.detector = detector;
	}

	@Override
	public void run() {
		while(true) {
			InputBlock iblock;
			Block block;
			
			// Get Block
			iblock = input.take();
			
			// Check for Poison
			if(input.isPoisoned())
				break;
			
			// Check Requirement
			if(requirements.approve(iblock))
				continue;
			
			// Sort Block
			if(sorter != null)
				iblock.sort(sorter);
			
			// Prefix
			block = prefixer.prefix(iblock);
			
			// Detect
			List<Clone> clones = detector.detectClones(block, index);
			
			// Output
			output(clones);
		}
	}
	
	public void output(List<Clone> clones) {
		for(Clone clone : clones) {
			while(true) {
				try {
					output.put(clone);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
