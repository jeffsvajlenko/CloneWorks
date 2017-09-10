package input.termprocessors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Concatenates the tokens (space delimiter) and hashes.
 *
 */
public class Hasher implements ITermProcessor {

	public String toString() {
		return this.getClass().getName() + " " + algorithm;
	}
	
	private String algorithm;
	
	public Hasher(String algorithm) throws NoSuchAlgorithmException {
		this.algorithm = algorithm;
		MessageDigest.getInstance(algorithm);
	}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			return new LinkedList<String>();
		}
		
		List<String> retval = new ArrayList<String>(tokens.size());
		for(String token : tokens) {
			md.update(token.getBytes());
			byte[] bytes = md.digest();
			md.reset();
			StringBuffer result = new StringBuffer();
	        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
	        retval.add(result.toString());
		}
    
        return retval;
	}
	
}
