package input.tokenprocessors;

import java.util.LinkedList;
import java.util.List;

public class RemoveEmpty implements ITokenProcessor {

	@Override
	public List<String> process(List<String> tokens) {
		List<String> retval = new LinkedList<String>();
		for(String str : tokens) {
			if(!str.equals("")) {
				retval.add(str);
			}
		}
		return retval;
	}

}
