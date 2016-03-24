package constants;

public class LanguageConstants {
	public final static String JAVA = "java";
	public final static String C = "c";
	public final static String CS = "cs";
	public final static String CPP = "cpp";
	public final static String PYTHON = "python";
	
	/**
	 * Checks if language is supported.
	 * @param language
	 * @return
	 */
	public static boolean checkValid(String language) {
		if(language.equals(JAVA) || language.equals(C) || language.equals(CS) || language.equals(CPP) || language.equals(PYTHON))
			return true;
		else
			return false;
	}

	/**
	 * Throws IllegalArgumentException if language is not supported.
	 * @param language
	 */
	public static void ifInvalidThrowException(String language) {
		if(!checkValid(language))
			throw new IllegalArgumentException("Language '" + language + "' is not a valid language.");
	}
	
	public static boolean isIfDefLanguage(String language) {
		if(language.equals(C) || language.equals(CPP))
			return true;
		else
			return false;
	}
}
