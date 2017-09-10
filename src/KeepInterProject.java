import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class KeepInterProject {

	public static void main(String args[]) throws IOException {
		Path root = Paths.get(args[0]);
		HashSet<XMLClone> clones = new HashSet<XMLClone>();
		
		CloneSubtract.readClones(new BufferedReader(new InputStreamReader(System.in)), clones);
		
		// Output
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		for(XMLClone clone : clones) {
			Path p1 = root.relativize(Paths.get(clone.getF1().getFile()));
			Path p2 = root.relativize(Paths.get(clone.getF2().getFile()));
			
			String sys1 = p1.getName(0).toString();
			String sys2 = p2.getName(0).toString();
			
			if(!sys1.equals(sys2)) {
				System.out.println(clone.text);
			}
		}
		
	}
	
}
