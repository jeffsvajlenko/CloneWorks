package cwdetect.util;

import java.io.BufferedReader;
import java.io.IOException;

public class BlockFileReader {

	private BufferedReader reader;
	
	public BlockFileReader(BufferedReader reader) {
		this.reader = reader;
	}
	
	public void skipBlock() throws IOException {
		String line;
		while((line = reader.readLine()) != null) {
			if(line.equals(">"))
				break;
		}
	}
	
	public void skipBlocks(long num) throws IOException {
		for(long i = 0; i < num; i++)
			skipBlock();
	}
	
	public String nextInputBlockString() throws IOException {
		String retval = "";
		
		String line;
		while(true) {
			line = reader.readLine();
			if(line == null)
				return null;
			if(line.equals(">"))
				break;
			if(line.startsWith("#"))
				continue;
			retval += line + "\n";
		}
		return retval;
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
}
