package detection.workers.helpers;

import input.block.InputBlock;
import util.blockingqueue.IReceiver;

public class ObjectInputBlockInput implements InputBlockInput {

	private IReceiver<InputBlock> input;
	
	public ObjectInputBlockInput(IReceiver<InputBlock> input) {
		this.input = input;
	}
	
	@Override
	public InputBlock take() {
		while(true) {
			try {
				return input.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isPoisoned() {
		return input.isPoisoned();
	}

}
