package cwdetect.requirements;

import cwbuild.block.InputBlock;

public class SizeRequirements implements Requirements {

	private int minLines;
	private int maxLines;
	private int minTokens;
	private int maxTokens;
	
	public SizeRequirements(int minLines, int maxLines, int minTokens, int maxTokens) {
		this.minLines = minLines;
		this.minTokens = minTokens;
		
		if(maxLines != 0) {
			this.maxLines = maxLines;
		} else {
			this.maxLines = Integer.MAX_VALUE;
		}
		
		if(maxTokens != 0) {
			this.maxTokens = maxTokens;
		} else {
			this.maxTokens = Integer.MAX_VALUE;
		}
	}
	
	@Override
	public boolean approve(InputBlock block) {
		if(block.getNumOriginalTokens() > maxTokens) return false;
		else if (block.getNumOriginalTokens() < minTokens) return false;
		else if (block.numLines() > maxLines) return false;
		else if (block.numLines() < minLines) return false;
		else return true;
	}

}
