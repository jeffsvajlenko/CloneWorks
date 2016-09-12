package cwbuild.utils;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public interface FilePathStream {
	
	/**
	 * 
	 * @return
	 * @throws IOException If IOException.  Probably unrecoverable.
	 * @throws InvalidPathException If the path is invalid.  Can continue.
	 */
	public Path next() throws IOException, InvalidPathException;
	
	public void close() throws IOException, InvalidPathException;
	
}
