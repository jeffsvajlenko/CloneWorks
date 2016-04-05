package constants;

import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

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
	
	public static FileFilter getFileFilter(String language) {
		if(!checkValid(language))
			throw new IllegalArgumentException("Language '" + language + "' is not a valid language.");
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
				retval = null;
		}
		return retval;
	}
	
	public static boolean isIfDefLanguage(String language) {
		if(language.equals(C) || language.equals(CPP))
			return true;
		else
			return false;
	}
}
