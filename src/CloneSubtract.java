import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class CloneSubtract {
	
	public static void main(String args[]) throws IOException {
		
		
		// Get Input Clones
		HashSet<XMLClone> input = new HashSet<XMLClone>();
		readClones(new BufferedReader(new InputStreamReader(System.in)), input);
		
		// Remove clones
		remove(input, args);
		
		// Output
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		for(XMLClone clone : input) {
			System.out.println(clone.text);
		}
	}
	
	
	public static void remove(HashSet<XMLClone> input, String...files) throws FileNotFoundException, IOException {
		
		for(String file : files) {
			Path premove = Paths.get(file);
			
			// Get Remove Clones
			HashSet<XMLClone> remove = new HashSet<XMLClone>();
			readClones(new BufferedReader(new FileReader(premove.toFile())), remove);
			
			// Filter
			for(XMLClone clone : remove) {
				input.remove(clone);
			}
		}
		
	}
	
	public static void readClones(BufferedReader br, HashSet<XMLClone> set) throws IOException {
		
		String line;
		String fragment = "";
		
		boolean inClone = false;
		boolean inFirst = false;
		
		XMLCodeFragment f1 = null;
		XMLCodeFragment f2 = null;
		
		while((line = br.readLine()) != null) {
			if(line.startsWith("<clone id=") || line.startsWith("<clone nlines=")) {
				inClone = true;
				inFirst = true;
				fragment = "";
			}
			
			if(inClone) {
				fragment += line + "\n";
				
				if(line.startsWith("<source file=")) {
					String parts[] = line.split("\"");
					String file = parts[1];
					int startline = Integer.parseInt(parts[3]);
					int endline = Integer.parseInt(parts[5]);
					
					if(inFirst) {
						f1 = new XMLCodeFragment(file, startline, endline);
						inFirst = false;
					} else {
						f2 = new XMLCodeFragment(file, startline, endline);
					}
				}
				
				if(line.startsWith("</clone>")) {
					set.add(new XMLClone(f1,f2,fragment));
					inClone = false;
				}
			}
			
		}
		br.close();
	}
	
}
