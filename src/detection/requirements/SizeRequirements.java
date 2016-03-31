package detection.requirements;

import input.block.InputBlock;

public class SizeRequirements implements Requirements {

	private int minLines;
	private int maxLines;
	private int minTokens;
	private int maxTokens;
	
	public SizeRequirements(int minLines, int maxLines, int minTokens, int maxTokens) {
		this.minLines = minLines;
		this.maxLines = maxLines;
		this.minTokens = minTokens;
		this.maxTokens = maxTokens;
	}
	
	@Override
	public boolean approve(InputBlock block) {
		if(block.numTokens() > maxTokens) return false;
		else if (block.numTokens() < minTokens) return false;
		else if (block.numLines() > maxLines) return false;
		else if (block.numLines() < minLines) return false;
		else return true;
	}

}
