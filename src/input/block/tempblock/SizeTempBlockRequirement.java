package input.block.tempblock;

public class SizeTempBlockRequirement implements TempBlockRequirements {

	private int minlines;
	private int maxlines;
	private int mintokens;
	private int maxtokens;
	
	public SizeTempBlockRequirement(int minlines, int maxlines, int mintokens, int maxtokens) {
		this.minlines = minlines;
		this.maxlines = maxlines;
		this.mintokens = mintokens;
		this.maxtokens = maxtokens;
	}
	
	@Override
	public boolean approve(TempBlock block) {
		if(block.numLines() < this.minlines)
			return false;
		if(block.numLines() > this.maxlines)
			return false;
		if(block.numOriginalTokens() < this.mintokens)
			return false;
		if(block.numOriginalTokens() > this.maxtokens)
			return false;
		return true;
	}

	public int getMinlines() {
		return minlines;
	}

	public int getMaxlines() {
		return maxlines;
	}

	public int getMintokens() {
		return mintokens;
	}

	public int getMaxtokens() {
		return maxtokens;
	}
	
}
