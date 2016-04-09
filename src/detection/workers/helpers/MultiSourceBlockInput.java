package detection.workers.helpers;

import java.util.List;
import java.util.LinkedList;

import detection.block.Block;

public class MultiSourceBlockInput implements BlockInput {

	private List<BlockInput> inputs;
	private BlockInput currentInput;
	
	public MultiSourceBlockInput(BlockInput ... inputs) {
		this.inputs = new LinkedList<BlockInput>();
		for(BlockInput input : inputs)
			this.inputs.add(input);
		this.currentInput = this.inputs.remove(0);
	}
	
	@Override
	public Block take() {
		while(true) {
			Block block = currentInput.take();
			
			// If not poisoned, return block
			if(!currentInput.isPoisoned()) {
				return block;
			
			// If poisoned
			} else {
				// If inputs remaining, update current input, and switch to it
				if(inputs.size() > 0) {
					currentInput = inputs.remove(0);
					return take();
				
				// Else return the final poison
				} else {
					currentInput = null;
					return block;
				}
			}
		}
	}

	@Override
	public boolean isPoisoned() {
		if(currentInput == null)
			return true;
		else
			return false;
	}

	
	
}
