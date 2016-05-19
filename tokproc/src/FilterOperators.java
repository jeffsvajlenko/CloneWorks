package input.tokenprocessors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import constants.LanguageConstants;

public class FilterOperators implements ITokenProcessor {
	
	public String toString() {
		return this.getClass().getName();
	}
	
	Set<String> java_filter = new HashSet<String>();
	Set<String> c_filter = new HashSet<String>();
	Set<String> cs_filter = new HashSet<String>();
	Set<String> cpp_filter = new HashSet<String>();
	Set<String> python_filter = new HashSet<String>();
	
	public FilterOperators(String init) {
		this();
	}
	
	public FilterOperators() {
		Set<String> filter;
		
		filter = java_filter;
		{	
			// Unary Operators
			filter.add("=");   filter.add(">");  filter.add("<");   filter.add("!");  filter.add("~");  filter.add("?");
			filter.add(":");
			
			filter.add("->"); filter.add("==");  filter.add(">="); filter.add("<="); filter.add("!=");
			filter.add("&&");  filter.add("||"); filter.add("++");  filter.add("--"); filter.add("+");  filter.add("-");
			filter.add("*");   filter.add("/");  filter.add("&");   filter.add("|");  filter.add("^");  filter.add("%");
			filter.add("<<");  filter.add(">>"); filter.add(">>>"); filter.add("+="); filter.add("-="); filter.add("*=");
			filter.add("/=");  filter.add("&="); filter.add("|=");  filter.add("^="); filter.add("%="); filter.add("<<=");
			filter.add(">>="); filter.add(">>>=");
		}
		
		filter = c_filter;
		{
			filter.add("&");  filter.add("*");  filter.add("+");  filter.add("-");  filter.add("~");   filter.add("!");
			filter.add("->"); filter.add("++"); filter.add("--"); filter.add("<<"); filter.add(">>");  filter.add("<=");
			filter.add(">="); filter.add("=="); filter.add("!="); filter.add("&&"); filter.add("||");  filter.add("*=");
			filter.add("/="); filter.add("%="); filter.add("+="); filter.add("-="); filter.add("<<="); filter.add(">>=");
			filter.add("&="); filter.add("^="); filter.add("|=");
		}
		
		filter = cpp_filter;
		{
			// non_gt_binary_operator
			filter.add("||"); filter.add("&&"); filter.add("|");  filter.add("^");  filter.add("&");   filter.add("=="); filter.add("!=");
			filter.add("<");  filter.add("<="); filter.add(">="); filter.add("<<"); filter.add(">>");  filter.add("+");  filter.add("-");
			filter.add("*");  filter.add("/");  filter.add("%");  filter.add(".*"); filter.add("->*");
			
		    // operator
			filter.add("+");   filter.add("-");  filter.add("*");  filter.add("/");   filter.add("%");   filter.add("^");  filter.add("&");
			filter.add("|");   filter.add("~");  filter.add("!");  filter.add("=");   filter.add("<");   filter.add(">");  filter.add("+=");
			filter.add("-=");  filter.add("-+"); filter.add("*="); filter.add("/=");  filter.add("%=");  filter.add("^="); filter.add("&=");
			filter.add("|=");  filter.add("<<"); filter.add(">>"); filter.add(">>="); filter.add("<<="); filter.add("=="); filter.add("!=");
			filter.add("<=");  filter.add(">="); filter.add("&&"); filter.add("||");  filter.add("++");  filter.add("--"); filter.add(",");
			filter.add("->*"); filter.add("->");
			
			// assignment_operator
			filter.add("=");   filter.add("*="); filter.add("/="); filter.add("%="); filter.add("+="); filter.add("-="); filter.add(">>=");
			filter.add("<<="); filter.add("&="); filter.add("^="); filter.add("|=");
			
	        // binary operator
			filter.add("||"); filter.add("&&"); filter.add("|");  filter.add("^");  filter.add("&");  filter.add("=="); filter.add("!="); filter.add("<");
			filter.add(">");  filter.add("<="); filter.add(">="); filter.add("<<"); filter.add(">>"); filter.add("+");  filter.add("-");  filter.add("*");
			filter.add("/");  filter.add("%");  filter.add(".*"); filter.add("->*");
			// unary operator
			filter.add("**"); filter.add("*"); filter.add("&"); filter.add("+"); filter.add("-"); filter.add("!"); filter.add("~");
			filter.add("&");  filter.add("*");  filter.add("+");  filter.add("-");  filter.add("~");   filter.add("!");
			filter.add("->"); filter.add("++"); filter.add("--"); filter.add("<<"); filter.add(">>");  filter.add("<=");
			filter.add(">="); filter.add("=="); filter.add("!="); filter.add("&&"); filter.add("||");  filter.add("*=");
			filter.add("/="); filter.add("%="); filter.add("+="); filter.add("-="); filter.add("<<="); filter.add(">>=");
			filter.add("&="); filter.add("^="); filter.add("|="); filter.add("->*"); filter.add(".*"); filter.add("::");
			filter.add("**");
		}
		
		filter = cs_filter;
		{
			filter.add("+");  filter.add("-");  filter.add("*");   filter.add("/");   filter.add("%");   filter.add("&");  
			filter.add("|");  filter.add("^");  filter.add("!");   filter.add("~");   filter.add("=");   filter.add("<");  
			filter.add(">");  filter.add("?");  filter.add("??");  filter.add("::");  filter.add("++");  filter.add("--");
			filter.add("&&"); filter.add("||"); filter.add("->");  filter.add("==");  filter.add("!=");  filter.add("<=");
			filter.add(">="); filter.add("+="); filter.add("-=");  filter.add("*=");  filter.add("/=");  filter.add("%=");
			filter.add("&="); filter.add("|="); filter.add("^=");  filter.add("<<");  filter.add("<<="); filter.add("=>");
			filter.add(">>"); filter.add(">>=");  
		}
		
		filter = python_filter;
		{
			filter.add("="); filter.add("+="); filter.add("-="); filter.add("*="); filter.add("/="); filter.add("//="); 
			filter.add("%="); filter.add("&="); filter.add("|="); filter.add("^="); filter.add(">>="); filter.add("<<="); 
			filter.add("**=");
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
