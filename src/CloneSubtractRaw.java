import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CloneSubtractRaw {
	
	public static void main(String args[]) throws IOException {	
		Path files1  = Paths.get(args[0]);
		Path clones1 = Paths.get(args[1]);
		Path files2  = Paths.get(args[2]);
		Path clones2 = Paths.get(args[3]);
		
		//Map<Long,String> fmap1 = getFileMap(files1);
		//Map<String,Long> ifmap1 = reverseMap(fmap1);
		//fmap1=null;
		//Map<Long,String> fmap2 = getFileMap(files2);
		Set<String> sclones2 = getClones(clones2);
		//fmap2=null;
		//ifmap1=null;
		Set<String> sclones1 = getClones(clones1);
		
		sclones1.removeAll(sclones2);
		
		for(String line : sclones1) {
			System.out.println(line);
		}
		
	}
	
	public static Set<String> getClones(Path clones) throws IOException {
		Set<String> retval = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(clones.toFile()));
		String line;
		while((line = br.readLine()) != null) {
			if(!line.startsWith("#")) {
				retval.add(line);
			}
		}
		br.close();
		return retval;
	}
	
	public static Set<String> getClones(Path clones, Map<Long,String> fmap, Map<String,Long> rebase) throws IOException {
		Set<String> retval = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(clones.toFile()));
		String line;
		while((line = br.readLine()) != null) {
			if(!line.startsWith("#")) {
				String parts[] = line.split(",");
				long id1 = Long.parseLong(parts[0]);
				long id2 = Long.parseLong(parts[3]);
				long nid1 = rebase.get(fmap.get(id1));
				long nid2 = rebase.get(fmap.get(id2));
				retval.add(nid1 + "," + parts[1] + "," + parts[2] + "," + nid2 + "," + parts[4] + "," + parts[5]);
			}
		}
		br.close();
		return retval;
	}
	
	public static Map<Long,String> getFileMap(Path files) throws IOException {
		Map<Long,String> retval = new HashMap<Long,String>();
		BufferedReader br = new BufferedReader(new FileReader(files.toFile()));
		String line;
		while((line = br.readLine()) != null) {
			if(!line.startsWith("#")) {
				String parts[] = line.split("\t");
				long id = Long.parseLong(parts[0]);
				retval.put(id, parts[1]);
			}
		}
		br.close();
		return retval;
	}
	
	public static Map<String,Long> reverseMap(Map<Long,String> map) {
		Map<String,Long> retval = new HashMap<String,Long>();
		for(long id : map.keySet()) {
			retval.put(map.get(id), id);
		}
		return retval;
	}
}
