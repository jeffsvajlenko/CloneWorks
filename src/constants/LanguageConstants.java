package constants;

public class LanguageConstants {
	public final static String JAVA = "java";
	public final static String C = "c";
	public final static String CS = "cs";
	public final static String CPP = "cpp";
	public final static String PYTHON = "python";
	
	public static boolean checkValidLanguage(String language) {
		if(language.equals(JAVA) || language.equals(C) || language.equals(CS) || language.equals(CPP) || language.equals(PYTHON))
			return true;
		else
			return false;
	}
	
}
