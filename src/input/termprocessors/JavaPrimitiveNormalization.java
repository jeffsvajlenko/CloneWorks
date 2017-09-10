package input.termprocessors;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import constants.LanguageConstants;

/**
 * 
 * Replaces all primitives tokens with int.
 *
 */
public class JavaPrimitiveNormalization implements ITermProcessor {
	
	public String toString() {
		return this.getClass().getName();
	}
	
	public JavaPrimitiveNormalization(String init) {
		this();
	}
	
	Set<String> primitives = new HashSet<String>();
	
	public JavaPrimitiveNormalization() {
		this.primitives.add("boolean");
		this.primitives.add("byte");
		this.primitives.add("char");
		this.primitives.add("short");
		this.primitives.add("int");
		this.primitives.add("long");
		this.primitives.add("float");
		this.primitives.add("double");
	}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		List<String> retval = new LinkedList<String>();
		for(String token : tokens) {
			if(primitives.contains(token)) {
				retval.add("int");
			} else {
				retval.add(token);
			}
		}
		return retval;
	}

}
