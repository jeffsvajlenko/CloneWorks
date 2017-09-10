import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class CloneShuffle {

	public static void main(String args[]) throws IOException {
		HashSet<XMLClone> clones = new HashSet<XMLClone>();
		
		CloneSubtract.readClones(new BufferedReader(new InputStreamReader(System.in)), clones);
		
		List<XMLClone> lclones = new ArrayList<XMLClone>(clones);
		Collections.shuffle(lclones);
		
		// Output
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		for(XMLClone clone : lclones) {
			System.out.println(clone.text);
		}
		
	}
	
}