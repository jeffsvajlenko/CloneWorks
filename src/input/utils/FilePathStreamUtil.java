package input.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

public class FilePathStreamUtil {
	
	public static FilePathStream createFilePathStream(BufferedReader in) {
		return new BufferedReaderFilePathStream(in);
	}
	
	public static FilePathStream createFilePathStream(Path file) throws FileNotFoundException {
		return new BufferedReaderFilePathStream(new BufferedReader(new FileReader(file.toFile())));
	}
	
}
