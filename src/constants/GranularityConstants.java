package constants;

public class GranularityConstants {
	public static final String FILE = "file";
	public static final String BLOCK = "block";
	public static final String FUNCTION = "function";
	
	public static boolean checkValidGranularity(String granularity) {
		if(granularity.equals(FILE) || granularity.equals(BLOCK) || granularity.equals(FUNCTION))
			return true;
		else
			return false;
	}
	
}
