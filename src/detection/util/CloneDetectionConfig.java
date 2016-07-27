package detection.util;

import java.nio.file.Path;

public class CloneDetectionConfig {

	private Path blocks;
	private Path clones;
	private double minSimilarity;
	private int minLines;
	private int maxLines;
	private int minTokens;
	private int maxTokens;
	private boolean presorted;
	private boolean partitioned;
	private boolean dfweight;
	private int maxPartitionSize;
	private int numThreads;
	
	public CloneDetectionConfig(Path blocks, Path clones, double minSimilarity, int minLines, int maxLines,
			int minTokens, int maxTokens, boolean presorted, boolean partitioned, boolean dfweight, int maxPartitionSize,
			int numThreads) {
		super();
		this.blocks = blocks;
		this.clones = clones;
		this.minSimilarity = minSimilarity;
		this.minLines = minLines;
		this.maxLines = maxLines;
		this.minTokens = minTokens;
		this.maxTokens = maxTokens;
		this.presorted = presorted;
		this.partitioned = partitioned;
		this.maxPartitionSize = maxPartitionSize;
		this.numThreads = numThreads;
		this.dfweight = dfweight;
	}

	public boolean isDF() {
		return dfweight;
	}
	
	public Path getBlocks() {
		return blocks;
	}

	public Path getClones() {
		return clones;
	}

	public double getMinSimilarity() {
		return minSimilarity;
	}

	public int getMinLines() {
		return minLines;
	}

	public int getMaxLines() {
		return maxLines;
	}

	public int getMinTokens() {
		return minTokens;
	}

	public int getMaxTokens() {
		return maxTokens;
	}

	public boolean isPresorted() {
		return presorted;
	}

	public boolean isPartitioned() {
		return partitioned;
	}

	public int getMaxPartitionSize() {
		return maxPartitionSize;
	}

	public int getNumThreads() {
		return numThreads;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
		result = prime * result + ((clones == null) ? 0 : clones.hashCode());
		result = prime * result + maxLines;
		result = prime * result + maxPartitionSize;
		result = prime * result + maxTokens;
		result = prime * result + minLines;
		long temp;
		temp = Double.doubleToLongBits(minSimilarity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + minTokens;
		result = prime * result + numThreads;
		result = prime * result + (partitioned ? 1231 : 1237);
		result = prime * result + (presorted ? 1231 : 1237);
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
		CloneDetectionConfig other = (CloneDetectionConfig) obj;
		if (blocks == null) {
			if (other.blocks != null)
				return false;
		} else if (!blocks.equals(other.blocks))
			return false;
		if (clones == null) {
			if (other.clones != null)
				return false;
		} else if (!clones.equals(other.clones))
			return false;
		if (maxLines != other.maxLines)
			return false;
		if (maxPartitionSize != other.maxPartitionSize)
			return false;
		if (maxTokens != other.maxTokens)
			return false;
		if (minLines != other.minLines)
			return false;
		if (Double.doubleToLongBits(minSimilarity) != Double.doubleToLongBits(other.minSimilarity))
			return false;
		if (minTokens != other.minTokens)
			return false;
		if (numThreads != other.numThreads)
			return false;
		if (partitioned != other.partitioned)
			return false;
		if (presorted != other.presorted)
			return false;
		return true;
	}
	
}
