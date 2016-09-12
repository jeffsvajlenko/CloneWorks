package cwdetect.workers.helpers;

import java.util.List;

import cwbuild.block.InputBlock;

public class MultSourceInputBlockInput implements InputBlockInput {

	private List<InputBlockInput> inputs;
	private InputBlockInput currentInput;
	private boolean isPoison;
	
	public MultSourceInputBlockInput(List<InputBlockInput> inputs) {
		this.inputs = inputs;
		currentInput = inputs.remove(0);
		this.isPoison = false;
	}
	
	@Override
	public InputBlock take() {
		while(true) {
			InputBlock block = currentInput.take();
			// If not poisoned, return block
			if (!currentInput.isPoisoned()) {
				return block;
				
			// If poisoned
			} else {
				// If inputs remaining, update current input, and switch to it
				if(inputs.size() > 0) {
					currentInput = inputs.remove(0);
					return take();
					
				// Else return the final poison
				} else {
					isPoison = true;
					return block;
				}
			}
		}
	}

	@Override
	public boolean isPoisoned() {
		return this.isPoison;
	}

}
