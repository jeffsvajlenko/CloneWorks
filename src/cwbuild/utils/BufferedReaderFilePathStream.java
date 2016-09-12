package cwbuild.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

class BufferedReaderFilePathStream implements FilePathStream {

	private BufferedReader in;
	
	public BufferedReaderFilePathStream(BufferedReader in) {
		this.in = in;
	}
	
	@Override
	public Path next() throws IOException, InvalidPathException {
		String sPath = in.readLine();
		Path retval;
		if(sPath == null)
			retval = null;
		else
			retval = Paths.get(sPath);
		return retval;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
