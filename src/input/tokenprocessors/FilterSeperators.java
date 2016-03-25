package input.tokenprocessors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import constants.LanguageConstants;

public class FilterSeperators implements ITokenProcessor {

	Set<String> filter;
	
	public FilterSeperators(String language) {
		LanguageConstants.ifInvalidThrowException(language);
		
		filter = new HashSet<String>();
		
		if(language == LanguageConstants.JAVA) {
			filter.add("("); filter.add(")"); filter.add("{"); filter.add("}");   filter.add("["); filter.add("]"); 
			filter.add(";"); filter.add(","); filter.add("."); filter.add("..."); filter.add("@"); filter.add("::");
		}
		if(language == LanguageConstants.C) {
			filter.add("("); filter.add(")"); filter.add("{"); filter.add("}");  filter.add("["); filter.add("]"); 
			filter.add(";"); filter.add(","); filter.add("."); filter.add(".."); filter.add("..."); 
		}
		if(language == LanguageConstants.CS) {
			filter.add("{");  filter.add("}");  filter.add("[");  filter.add("]");  filter.add("(");  filter.add(")");
			filter.add(".");  filter.add(",");  filter.add(":");  filter.add(";");
		}
		if(language == LanguageConstants.PYTHON) {
			filter.add("("); filter.add(")"); filter.add("["); filter.add("]"); filter.add("{"); filter.add("}"); 
			filter.add(","); filter.add(":"); filter.add("."); filter.add(";"); filter.add("@"); filter.add("->"); 
		}
		if(language == LanguageConstants.CPP) {
			filter.add("("); filter.add(")"); filter.add("{"); filter.add("}");  filter.add("["); filter.add("]"); 
			filter.add(";"); filter.add(","); filter.add("."); filter.add(".."); filter.add("..."); 
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
