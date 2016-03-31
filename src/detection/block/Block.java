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
	
	public Block(long fileid, int startline, int endline, ArrayList<BlockElement> block, double sim) {
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
