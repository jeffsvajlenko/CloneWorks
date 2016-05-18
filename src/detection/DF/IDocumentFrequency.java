package detection.DF;

import input.block.InputBlock;

public interface IDocumentFrequency {

	/**
	 * Add a document with the given term.
	 * @param block The term.
	 */
	public void add(InputBlock block);
	
	/**
	 * The number of documents with this term.
	 * @param term The term.
	 */
	public long num(String term);

	/**
	 * Number of documents.
	 * @return
	 */
	public long numDocs();
	
}
