package cwdetect.requirements;

import cwbuild.block.InputBlock;

public class NoRequirements implements Requirements {

	@Override
	public boolean approve(InputBlock block) {
		return true;
	}

}
