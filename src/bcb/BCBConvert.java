package bcb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class BCBConvert {
	public static void main(String args[]) throws IOException {
		Path files  = Paths.get(args[0]);
		Path clones = Paths.get(args[1]);
		Path output = Paths.get(args[2]);
		
		BCBConvert.convert(clones, files, output);
	}
	
	public static void convert(Path clones, Path files, Path output) throws IOException {
		Map<Long, String> types = new HashMap<Long, String>();
		Map<Long, String> names = new HashMap<Long, String>();
		
		
		String line;
		BufferedReader in_files = new BufferedReader(new FileReader(files.toFile()));
		while((line = in_files.readLine()) != null) {
			String [] parts = line.split("\t",2);
			Long fileid = Long.parseLong(parts[0]);
			Path path = Paths.get(parts[1]);
			types.put(fileid, path.getParent().getFileName().toString());
			names.put(fileid, path.getFileName().toString());
		}
		in_files.close();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(output.toFile()));
		BufferedReader in_clones = new BufferedReader(new FileReader(clones.toFile()));
		while((line = in_clones.readLine()) != null) {
			String [] parts = line.split(",");
			long file1 = Long.parseLong(parts[0]);
			long file2 = Long.parseLong(parts[3]);
			
			String type1 = types.get(file1);
			String name1 = names.get(file1);
			String type2 = types.get(file2);
			String name2 = names.get(file2);
			
			out.write(type1 + "," + name1 + "," + parts[1] + "," + parts[2] + "," + type2 + "," + name2 + "," + parts[4] + "," + parts[5] + "\n");
		}
		in_clones.close();
		out.close();
	}
	
}
