package detection.block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block implements Serializable {
	
	private static final long serialVersionUID = 8197034157690373765L;
	
	private long id;
	private long fileid;
	private int startline;
	private int endline;
	
	private Map<String,Integer> blockAsMap;
	private List<BlockElement> blockAsList;
	private int numTokens;
	
	public Block(long id, long fileid, int startline, int endline, ArrayList<BlockElement> block) {
		this.id = id;
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
	
	public long getID() {
		return this.id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endline;
		result = prime * result + (int) (fileid ^ (fileid >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + startline;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (endline != other.endline)
			return false;
		if (fileid != other.fileid)
			return false;
		if (id != other.id)
			return false;
		if (startline != other.startline)
			return false;
		return true;
	}
	
}
