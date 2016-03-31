package detection.block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block implements Serializable {
	
	private static final long serialVersionUID = 8197034157690373765L;
	
	private long fileid;
	private int startline;
	private int endline;
	
	private Map<String,Integer> blockAsMap;
	private List<BlockElement> blockAsList;
	private int numTokens;
	
	public Block(long fileid, int startline, int endline, ArrayList<BlockElement> block) {
		this.fileid = fileid;
		this.startline = startline;
		this.endline = endline;
		
		this.blockAsList = Collections.unmodifiableList(block);
		
		//Populate Blocks, Calculate #Tokens
		this.blockAsMap = new HashMap<String,Integer>();
		numTokens = 0;
		for(BlockElement tf : block) {
			numTokens += tf.getFrequency();
			this.blockAsMap.put(tf.getTerm(), tf.getFrequency());
			
		}
		this.blockAsMap = Collections.unmodifiableMap(this.blockAsMap);
	}
	
	public boolean doesOverlap(Block block) {
		if(fileid == block.fileid)
			if(startline >= block.startline && startline <= block.endline)     // Case #1: This block starts within the other block (ends wherever).
				return true;
			else if (endline >= block.startline && endline <= block.endline)   // Case #2: This block ends within the other block (starts wherever).
				return true;
			else if (startline <= block.startline && endline >= block.endline) // Case #3: This block captures the other block.
				return true;
		return false;
	}
	
	public int numLines() {
		return this.endline - this.startline + 1;
	}
	
	public long getFileID() {
		return this.fileid;
	}
	
	public int getStartLine() {
		return this.startline;
	}
	
	public int getEndLine() {
		return this.endline;
	}
	
	public Map<String,Integer> getBlockAsMap() {
		return blockAsMap;
	}
	
	public List<BlockElement> getBlockAsList() {
		return blockAsList;
	}

	public int numTokens() {
		return numTokens;
	}
	
}
