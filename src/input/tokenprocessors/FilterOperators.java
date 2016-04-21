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
	
	Set<String> filter;
	
	public FilterOperators(String language) {
		LanguageConstants.ifInvalidThrowException(language);
		
		filter = new HashSet<String>();
		
		if(language.equals(LanguageConstants.JAVA)) {
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
		if(language.equals(LanguageConstants.C)) {
			filter.add("&");  filter.add("*");  filter.add("+");  filter.add("-");  filter.add("~");   filter.add("!");
			filter.add("->"); filter.add("++"); filter.add("--"); filter.add("<<"); filter.add(">>");  filter.add("<=");
			filter.add(">="); filter.add("=="); filter.add("!="); filter.add("&&"); filter.add("||");  filter.add("*=");
			filter.add("/="); filter.add("%="); filter.add("+="); filter.add("-="); filter.add("<<="); filter.add(">>=");
			filter.add("&="); filter.add("^="); filter.add("|=");
		}
		if(language.equals(LanguageConstants.CPP)) {
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
		if(language.equals(LanguageConstants.CS)) {
			filter.add("+");  filter.add("-");  filter.add("*");   filter.add("/");   filter.add("%");   filter.add("&");  
			filter.add("|");  filter.add("^");  filter.add("!");   filter.add("~");   filter.add("=");   filter.add("<");  
			filter.add(">");  filter.add("?");  filter.add("??");  filter.add("::");  filter.add("++");  filter.add("--");
			filter.add("&&"); filter.add("||"); filter.add("->");  filter.add("==");  filter.add("!=");  filter.add("<=");
			filter.add(">="); filter.add("+="); filter.add("-=");  filter.add("*=");  filter.add("/=");  filter.add("%=");
			filter.add("&="); filter.add("|="); filter.add("^=");  filter.add("<<");  filter.add("<<="); filter.add("=>");
			filter.add(">>"); filter.add(">>=");  
		}
		if(language.equals(LanguageConstants.PYTHON)) {
			filter.add("="); filter.add("+="); filter.add("-="); filter.add("*="); filter.add("/="); filter.add("//="); 
			filter.add("%="); filter.add("&="); filter.add("|="); filter.add("^="); filter.add(">>="); filter.add("<<="); 
			filter.add("**=");
		}
		
	}
	
	@Override
	public List<String> process(List<String> tokens) {
		List<String> retval = new LinkedList<String>();
		for(String str : tokens) {
			if (!filter.contains(str))
				retval.add(str);
		}
		return retval;
	}

}
