import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class CloneMerge {

	public static void main(String args[]) throws IOException {
		HashSet<XMLClone> clones = new HashSet<XMLClone>();
		
		CloneSubtract.readClones(new BufferedReader(new InputStreamReader(System.in)), clones);
		
		// Output
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		for(XMLClone clone : clones) {
			System.out.println(clone.text);
		}
		
	}
	
}
