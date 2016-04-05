package detection.workers.helpers;

import input.block.InputBlock;
import util.blockingqueue.IReceiver;

public class StringInputBlockInput implements InputBlockInput {

	private IReceiver<String> input;
	
	public StringInputBlockInput(IReceiver<String> input) {
		this.input = input;
	}
	
	@Override
	public InputBlock take() {
		if(input.isPoisoned()) return null;
		while(true) {
			try {
				String sblock = input.take(); 
				if(input.isPoisoned()) {
					return null;
				}
				return InputBlock.readDetectionBlock(sblock);
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
