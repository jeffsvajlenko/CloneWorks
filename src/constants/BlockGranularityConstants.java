package constants;

public class BlockGranularityConstants {
	public static final String FILE = "file";
	public static final String BLOCK = "block";
	public static final String FUNCTION = "function";
	
	public static String getCanonical(String granularity) {
		if(granularity.toLowerCase().equals(FILE))
			return FILE;
		else if (granularity.toLowerCase().equals(BLOCK))
			return BLOCK;
		else if (granularity.toLowerCase().equals(FUNCTION))
			return FUNCTION;
		else
			throw new IllegalArgumentException("Block granularity '" + granularity + "' is not valid.");
			
	}
	
	public static boolean checkValid(String granularity) {
		if(granularity.equals(FILE) || granularity.equals(BLOCK) || granularity.equals(FUNCTION))
			return true;
		else
			return false;
	}
	
	public static void ifInvalidThrowException(String granularity) {
		if(!checkValid(granularity))
			throw new IllegalArgumentException("Block granularity '" + granularity + "' is not valid.");
	}
	
}
