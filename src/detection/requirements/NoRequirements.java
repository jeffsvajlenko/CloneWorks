package detection.requirements;

import input.block.InputBlock;

public class NoRequirements implements Requirements {

	@Override
	public boolean approve(InputBlock block) {
		return true;
	}

}
