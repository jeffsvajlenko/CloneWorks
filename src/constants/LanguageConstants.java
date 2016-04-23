package constants;

import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class LanguageConstants {
	
	public final static int JAVA = 1;
	public final static String STR_JAVA= "java";
	
	public final static int C = 2;
	public final static String STR_C = "c";
	
	public final static int CS = 3;
	public final static String STR_CS = "cs";
	
	public final static int CPP = 4;
	public final static String STR_CPP = "cpp";
	
	public final static int PYTHON = 5;
	public final static String STR_PYTHON = "python";
	
	public static int getCanonical(String language) {
		language = language.toLowerCase();
		if(language.equals(STR_JAVA))
			return JAVA;
		else if (language.equals(STR_C))
			return C;
		else if (language.equals(STR_CS))
			return CS;
		else if (language.equals(STR_CPP))
			return CPP;
		else if (language.equals(STR_PYTHON))
			return PYTHON;
		else
			throw new IllegalArgumentException("Language '" + language + "' is not a valid language.");
	}
	
	public static String getString(int language) {
		switch(language) {
			case JAVA:
				return STR_JAVA;
			case C:
				return STR_C;
			case CS:
				return STR_CS;
			case CPP:
				return STR_CPP;
			case PYTHON:
				return STR_PYTHON;
			default:
				throw new IllegalArgumentException("No language with constant value: " + language + ".");	
		
		}
	}
	
	/**
	 * Checks if language is supported.
	 * @param language
	 * @return
	 */
	public static boolean checkValid(int language) {
		if(language == JAVA || language == C || language == CS || language == CPP || language == PYTHON)
			return true;
		else
			return false;
	}

	/**
	 * Throws IllegalArgumentException if language is not supported.
	 * @param language
	 */
	public static void ifInvalidThrowException(int language) {
		if(!checkValid(language))
			throw new IllegalArgumentException("Language '" + language + "' is not a valid language.");
	}
	
	public static FileFilter getFileFilter(int language) {
		FileFilter retval;
		switch(language) {
			case JAVA:
				retval = new SuffixFileFilter(".java", IOCase.INSENSITIVE);
				break;
			case C:
				retval = new OrFileFilter(new SuffixFileFilter(".c", IOCase.INSENSITIVE), new SuffixFileFilter(".h", IOCase.INSENSITIVE));
				break;
			case CS:
				retval = new SuffixFileFilter(".cs", IOCase.INSENSITIVE);
				break;
			case CPP:
				List<IOFileFilter> filters = new LinkedList<IOFileFilter>();
				filters.add(new SuffixFileFilter(".C"));
				filters.add(new SuffixFileFilter(".cpp", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".cc", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".c++", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".cxx", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".H"));
				filters.add(new SuffixFileFilter(".h", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".hh", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".hpp", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".h++", IOCase.INSENSITIVE));
				filters.add(new SuffixFileFilter(".hxx", IOCase.INSENSITIVE));
				retval = new OrFileFilter(filters);
				break;
			case PYTHON:
				retval = new SuffixFileFilter(".py", IOCase.INSENSITIVE);
				break;
			default:
				throw new IllegalArgumentException("Language '" + language + "' is not a valid language.");
		}
		return retval;
	}
	
	public static boolean isIfDefLanguage(int language) {
		if(language == C || language == CPP)
			return true;
		else
			return false;
	}
}
