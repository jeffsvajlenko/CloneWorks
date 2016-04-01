package detection.util;

import java.io.BufferedReader;
import java.io.IOException;

public class BlockFileReader {

	private BufferedReader reader;
	
	public BlockFileReader(BufferedReader reader) {
		this.reader = reader;
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
			retval += line + "\n";
		}
		return retval;
	}
	
}
