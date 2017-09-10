package cwbuild.cfprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class RetainUniqueLines {

	public static void main(String args[]) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		
		Set<String> set = new HashSet<String>();
		String open = "";
		
		while((line = br.readLine()) != null) {
			if(line.startsWith("<source")) {
				open = line;
				set.clear();
			} else if (line.startsWith("</source>")) {
				System.out.println(open);
				for(String str : set) {
					System.out.println(str);
				}
				System.out.println(line);
			} else {
				line = line.trim();
				set.add(line);
			}
		}
	}
	
}
