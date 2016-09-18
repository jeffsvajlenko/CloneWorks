package cwformat.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import cwformat.formatter.*;

public class Format {

	public static void format(Path files, Path clones, Path output, Formatter formatter) throws Exception {
		ReportReader reader = new ReportReader(files,clones);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output.toFile()));
		formatter.format(reader, writer);
		writer.flush();
		writer.close();
		if(!reader.isClosed())
			reader.close();
	}
	
	
	
}
