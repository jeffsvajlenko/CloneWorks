package cwdetect.index;

public class BlockFrequency {
	private long id;
	private int frequency;
	public BlockFrequency(long id, int frequency) {
		super();
		
		if(frequency <= 0)
			throw new IllegalArgumentException("Frequency must be > 0.");
		
		this.id = id;
		this.frequency = frequency;
	}
	
	public long getId() {
		return id;
	}
	
	public int getFrequency() {
		return frequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + frequency;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		BlockFrequency other = (BlockFrequency) obj;
		if (frequency != other.frequency)
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BlockFrequency [id=" + id + ", frequency=" + frequency + "]";
	}
	
}
