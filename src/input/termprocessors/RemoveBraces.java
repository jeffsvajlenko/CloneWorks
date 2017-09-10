package input.termprocessors;

import java.util.ArrayList;
import java.util.List;

public class RemoveBraces implements ITermProcessor {

	public RemoveBraces(String init) {
		
	}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> newTokens = new ArrayList<String>(tokens.size());
		
		for(String token : tokens) {
			String test = token;
			test = token.replace("{", "");
			test = test.replace("}", "");
			test = test.trim();
			if(test.length() > 0) {
				newTokens.add(token);
			}
		}
		
		return newTokens;
	}

}
