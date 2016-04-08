package constants;

public class TokenGranularityConstants {
	
	public static final String TOKEN = "token";
	public static final String LINE = "line";
	
	public static String getCanonized(String token_granularity) {
		if(token_granularity.toLowerCase().equals(TOKEN))
			return TOKEN;
		else if (token_granularity.toLowerCase().equals(LINE))
			return LINE;
		else
			throw new IllegalArgumentException("Token granularity '" + token_granularity + "' is not valid.");
	}
	
	public static boolean checkValid(String token_granularity) {
		if(token_granularity == TOKEN || token_granularity == LINE)
			return true;
		else
			return false;
	}
	
	public static void ifInvalidThrowException(String token_granularity) {
		if(!checkValid(token_granularity))
			throw new IllegalArgumentException("Token granularity '" + token_granularity + "' is not valid.");
	}
	
}
