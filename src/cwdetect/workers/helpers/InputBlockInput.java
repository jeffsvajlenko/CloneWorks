package cwdetect.workers.helpers;

import cwbuild.block.InputBlock;

public interface InputBlockInput {

	public InputBlock take();
	public boolean isPoisoned();
	
}
