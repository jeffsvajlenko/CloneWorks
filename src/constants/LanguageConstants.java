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
	public static boolean checkValidLanguage(String language) {
		if(language.equals(JAVA) || language.equals(C) || language.equals(CS) || language.equals(CPP) || language.equals(PYTHON))
			return true;
		else
			return false;
	}

	/**
	 * Throws IllegalArgumentException if language is not supported.
	 * @param language
	 */
	public static void checkValidLanguageThrowException(String language) {
		if(!checkValidLanguage(language))
			throw new IllegalArgumentException("Language '" + language + "' is not a valid language.");
	}
	
}
