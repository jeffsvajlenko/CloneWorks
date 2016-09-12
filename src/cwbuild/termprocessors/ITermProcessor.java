package cwbuild.termprocessors;

import java.util.List;

public interface ITermProcessor {
	
	// Language Constants
	public final static int UNKOWN = 0;
	public final static int JAVA = 1;
	public final static int C = 2;
	public final static int CS = 3;
	public final static int CPP = 4;
	public final static int PYTHON = 5;
	
	// Granularity Constants
	public static final int FUNCTION = 1;
	public static final int BLOCK = 2;
	public static final int FILE = 3;

	// Token Type
	public static final int TOKEN = 1;
	public static final int LINE = 2;
	
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType);
	
}
