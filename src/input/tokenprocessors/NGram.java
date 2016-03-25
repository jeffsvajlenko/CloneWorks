package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

public class NGram implements ITokenProcessor {

	private int n;
	
	public NGram(int n) {
		this.n = n;
	}
	
	@Override
	public List<String> process(List<String> tokens) {
		List<String> retval = new LinkedList<String>();
		
		String gram = "";
		int count = 0;
		
		for(String str : tokens) {
			if(count == n) {
				retval.add(gram);
				gram = "";
				count = 0;
			} else {
				gram += str + "\t";
				count++;
			}
		}
		
		return retval;
	}

}
