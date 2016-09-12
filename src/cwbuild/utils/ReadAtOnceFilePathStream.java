package cwbuild.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class ReadAtOnceFilePathStream implements FilePathStream {

	private List<File> files;
	
	public ReadAtOnceFilePathStream(Path root, IOFileFilter filter) {
		files = new LinkedList<File>();
		files.addAll(FileUtils.listFiles(root.toFile(), filter, TrueFileFilter.INSTANCE));
	}
	
	@Override
	public Path next() throws IOException, InvalidPathException {
		if(files.size() > 0) {
			return files.remove(0).toPath();
		} else {
			return null;
		}
	}

	@Override
	public void close() throws IOException, InvalidPathException {
		files.clear();
	}

}
