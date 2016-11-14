package cwformat.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import cwformat.objects.Clone;

/**
 * 
 * @author jeff
 *
 */
public class ReportReader {
	
	private boolean closed = false;
	private Map<Long,Path> files;
	private BufferedReader br;
	
	/**
	 * 
	 * @param files
	 * @param clones
	 * @throws IOException
	 */
	public ReportReader(Path files, Path clones) throws IOException {
		this.files = ReportReader.parseFiles(files);
		this.br = new BufferedReader(new FileReader(clones.toFile()));
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Clone nextClone() throws IOException {
		if(closed)
			throw new IOException("Stream closed.");
		String line;
		
		while(true) {
			line = br.readLine();
			if(line == null) {
				break;
			}
			
			line = line.trim();
			if(line.startsWith("#")) {
				continue;
			} else {
				String [] parts = line.split(",");
				
				long id1 = Long.parseLong(parts[0]);
				Path path1 = files.get(id1);
				int start1 = Integer.parseInt(parts[1]);
				int end1 = Integer.parseInt(parts[2]);
				
				long id2 = Long.parseLong(parts[3]);
				Path path2 = files.get(id2);
				int start2 = Integer.parseInt(parts[4]);
				int end2 = Integer.parseInt(parts[5]);
				
				Clone clone = new Clone(id1,path1,start1,end1,id2,path2,start2,end2);
				return clone;
			}
		}
		br.close();
		this.closed = true;
		return null;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		br.close();
		this.closed = true;
	}
	
	public boolean isClosed() {
		return this.closed;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Map<Long,Path> parseFiles(Path path) throws IOException {
		Map<Long,Path> retval = new HashMap<Long,Path>();
		
		String line;
		BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
		while((line = br.readLine()) != null) {
			line = line.trim();
			if(!line.startsWith("#")) {
				String [] parts = line.split("\t");
				long id = Long.parseLong(parts[0]);
				Path file = Paths.get(parts[1]);
				retval.put(id, file);
			}
		}
		br.close();
		
		return retval;
	}
	
}
