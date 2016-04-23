package input.tokenprocessors;

import java.util.List;

public interface ITokenProcessor {
	
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType);
	
}
