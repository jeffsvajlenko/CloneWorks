package mf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class Convert {
	public static void main(String args[]) throws IOException {
		Path files  = Paths.get(args[0]);
		Path clones = Paths.get(args[1]);
		Path output = Paths.get(args[2]);
		
		Convert.convert(clones, files, output);
	}
	
	public static void convert(Path clones, Path files, Path output) throws IOException {
		Map<Long, Path> mfiles = new HashMap<Long,Path>();
		
		String line;
		BufferedReader in_files = new BufferedReader(new FileReader(files.toFile()));
		while((line = in_files.readLine()) != null) {
			String [] parts = line.split("\t",2);
			Long id = Long.parseLong(parts[0]);
			Path path = Paths.get(parts[1]);
			mfiles.put(id, path);
		}
		in_files.close();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(output.toFile()));
		BufferedReader in_clones = new BufferedReader(new FileReader(clones.toFile()));
		while((line = in_clones.readLine()) != null) {
			String [] parts = line.split(",");
			Path file1 = mfiles.get(Long.parseLong(parts[0]));
			Path file2 = mfiles.get(Long.parseLong(parts[3]));
			out.write(file1.toString() + "," + parts[1] + "," + parts[2] + "," + file2.toString() + "," + parts[4] + "," + parts[5] + "\n");
		}
		in_clones.close();
		out.close();
	}
	
}
