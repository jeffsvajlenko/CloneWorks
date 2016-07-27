package input.termprocessors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import constants.LanguageConstants;

public class FilterSeperators implements ITermProcessor {

	public String toString() {
		return this.getClass().getName();
	}
	
	Set<String> java_filter = new HashSet<String>();
	Set<String> c_filter = new HashSet<String>();
	Set<String> cs_filter = new HashSet<String>();
	Set<String> cpp_filter = new HashSet<String>();
	Set<String> python_filter = new HashSet<String>();
	
	public FilterSeperators(String init) {
		this();
	}
	
	public FilterSeperators() {
		Set<String> filter;
		
		filter = new HashSet<String>();
		
		filter = java_filter; {
			filter.add("("); filter.add(")"); filter.add("{"); filter.add("}");   filter.add("["); filter.add("]"); 
			filter.add(";"); filter.add(","); filter.add("."); filter.add("..."); filter.add("@"); filter.add("::");
		}
		
		filter = c_filter; {
			filter.add("("); filter.add(")"); filter.add("{"); filter.add("}");  filter.add("["); filter.add("]"); 
			filter.add(";"); filter.add(","); filter.add("."); filter.add(".."); filter.add("..."); 
		}
		
		filter = cs_filter; {
			filter.add("{");  filter.add("}");  filter.add("[");  filter.add("]");  filter.add("(");  filter.add(")");
			filter.add(".");  filter.add(",");  filter.add(":");  filter.add(";");
		}
		
		filter = cpp_filter; {
			filter.add("("); filter.add(")"); filter.add("{"); filter.add("}");  filter.add("["); filter.add("]"); 
			filter.add(";"); filter.add(","); filter.add("."); filter.add(".."); filter.add("..."); 
		}
		
		filter = python_filter; {
			filter.add("("); filter.add(")"); filter.add("["); filter.add("]"); filter.add("{"); filter.add("}"); 
			filter.add(","); filter.add(":"); filter.add("."); filter.add(";"); filter.add("@"); filter.add("->"); 
		}
	}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		LanguageConstants.ifInvalidThrowException(language);
		
		Set<String> filter;
		switch(language) {
		case LanguageConstants.JAVA:
			filter = java_filter;
			break;
		case LanguageConstants.C:
			filter = c_filter;
			break;
		case LanguageConstants.CPP:
			filter = cpp_filter;
			break;
		case LanguageConstants.CS:
			filter = cs_filter;
			break;
		case LanguageConstants.PYTHON:
			filter = python_filter;
			break;
		default:
			throw new IllegalArgumentException("Invalid language.  How did this happen?  It was checked!  Code bug!");
		}
		
		List<String> retval = new LinkedList<String>();
		for(String str : tokens) {
			if (!filter.contains(str))
				retval.add(str);
		}
		return retval;
	}

}
