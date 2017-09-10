package input.termprocessors;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Concatenates the tokens (space delimiter) and hashes.
 *
 */
public class CombineAndHash implements ITermProcessor {

	public String toString() {
		return this.getClass().getName() + " " + algorithm;
	}
	
	private String algorithm;
	
	public CombineAndHash(String algorithm) throws NoSuchAlgorithmException {
		this.algorithm = algorithm;
		MessageDigest.getInstance(algorithm);
	}
	
	@Override
	public List<String> process(List<String> tokens, int language, int granularity, int tokenType) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			return new ArrayList<String>();
		}
		// Create String
		String combine = "";
		for(String token : tokens)
			combine += token + " ";
		
		// Hash
		//System.out.println(combine);
		md.update(combine.getBytes());
		byte[] bytes = md.digest();
		md.reset(); // for re-use

		// Create String Representation
		StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        List<String> retval = new ArrayList<String>(1);
        retval.add(result.toString());
    
        return retval;
	}
	
}
