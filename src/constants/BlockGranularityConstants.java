package constants;

public class BlockGranularityConstants {
	
	public static final int FUNCTION = 1;
	public static final String STR_FUNCTION = "function";
	
	public static final int BLOCK = 2;
	public static final String STR_BLOCK = "block";
	
	public static final int FILE = 3;
	public static final String STR_FILE = "file";
	
	public static int getCanonical(String granularity) {
		granularity = granularity.toLowerCase();
		if(granularity.equals(STR_FILE))
			return FILE;
		else if (granularity.equals(STR_BLOCK))
			return BLOCK;
		else if (granularity.equals(STR_FUNCTION))
			return FUNCTION;
		else
			throw new IllegalArgumentException("Block granularity '" + granularity + "' is not valid.");
	}
	
	public static String getString(int granularity) {
		switch(granularity) {
			case FUNCTION:
				return STR_FUNCTION;
			case BLOCK:
				return STR_BLOCK;
			case FILE:
				return STR_FILE;
			default:
				throw new IllegalArgumentException("Block granularity constant " + granularity + " is not valid.");	
		}
	}
	
	public static boolean checkValid(int granularity) {
		if(granularity == FILE || granularity == BLOCK || granularity == FUNCTION)
			return true;
		else
			return false;
	}
	
	public static void ifInvalidThrowException(int granularity) {
		if(!checkValid(granularity))
			throw new IllegalArgumentException("Block granularity " + granularity + " is not valid.");
	}
	
}
