package detection.workers;

import java.util.List;
import java.util.Objects;

import detection.block.Block;
import detection.detection.Clone;
import detection.detection.CloneDetector;
import detection.index.IIndex;
import detection.workers.helpers.BlockInput;
import util.blockingqueue.IEmitter;

public class CloneDetection extends Thread {

	private BlockInput input;
	private IEmitter<Clone> output;
	
	private IIndex index;
	private CloneDetector detector;
	
	public CloneDetection(BlockInput input, IEmitter<Clone> output, IIndex index, CloneDetector detector) {
		Objects.requireNonNull(input);
		Objects.requireNonNull(output);
		Objects.requireNonNull(index);
		
		this.input = input;
		this.output = output;
		
		this.index = index;
		this.detector = detector;
	}

	@Override
	public void run() {
		while(true) {
			Block block;
			
			// Get Block
			block = input.take();
			
			// Check for Poison
			if(input.isPoisoned())
				break;
			
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
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
