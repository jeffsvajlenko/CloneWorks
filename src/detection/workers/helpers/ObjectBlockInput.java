package detection.workers.helpers;

import detection.block.Block;
import util.blockingqueue.IReceiver;

public class ObjectBlockInput implements BlockInput {

	private IReceiver<Block> input;
	
	public ObjectBlockInput(IReceiver<Block> input) {
		this.input = input;
	}
	
	@Override
	public Block take() {
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
