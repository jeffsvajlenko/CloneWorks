package cwbuild.block.tempblock;

import java.util.List;

public class TempBlock {
	
	public int getStartline() {
		return startline;
	}

	public int getEndline() {
		return endline;
	}
	
	public int numLines() {
		return endline - startline + 1;
	}

	public int numOriginalTokens() {
		return numOriginalTokens;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	private int startline;
	private int endline;
	private int numOriginalTokens;
	private List<String> tokens;
	
	public TempBlock(int startline, int endline, List<String> tokens) {
		this.startline = startline;
		this.endline = endline;
		this.tokens = tokens;
		this.numOriginalTokens = tokens.size();
	}
}