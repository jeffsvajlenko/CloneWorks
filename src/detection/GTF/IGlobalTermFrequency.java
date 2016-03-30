package detection.GTF;


public interface IGlobalTermFrequency {
	public void add(String term);
	public void add(String term, long num);
	public long getFrequency(String term);
}
