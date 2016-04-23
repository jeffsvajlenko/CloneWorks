package constants;

public class TokenGranularityConstants {
	
	public static final int TOKEN = 1;
	public static final String STR_TOKEN = "token";
	
	public static final int LINE = 2;
	public static final String STR_LINE = "line";
	
	public static int getCanonized(String tokenType) {
		tokenType = tokenType.toLowerCase();
		switch(tokenType) {
		case STR_TOKEN:
			return TOKEN;
		case STR_LINE:
			return LINE;
		default:
			throw new IllegalArgumentException("Token granularity '" + tokenType + "' is not valid.");
		}	
	}
	
	public static String getString(int tokenType) {
		switch(tokenType) {
		case TOKEN:
			return STR_TOKEN;
		case LINE:
			return STR_LINE;
		default:
			throw new IllegalArgumentException("Token granularity " + tokenType + " is not valid.");	
		}
	}
	
	public static boolean checkValid(int token_granularity) {
		if(token_granularity == TOKEN || token_granularity == LINE)
			return true;
		else
			return false;
	}
	
	public static void ifInvalidThrowException(int token_granularity) {
		if(!checkValid(token_granularity))
			throw new IllegalArgumentException("Token granularity " + token_granularity + " is not valid.");
	}
	
}
