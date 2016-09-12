package cwbuild.txl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import constants.InstallDir;
import constants.LanguageConstants;
import util.StreamGobbler;

public class TXLUtil {
	
	public static Path getTXLRoot() {
		return InstallDir.getInstallDir().resolve("txl");
	}
	
	public static ITXLCommand getIfDef() {
		return new TXLOptional(new TXLNamed("ifdef"), new int[] {LanguageConstants.C, LanguageConstants.CPP});
	}
	
	public static ITXLCommand getPythonPreprocess() {
		return new TXLOptional(new TXLNamed("pyindent"), new int[]{LanguageConstants.PYTHON});
	}
	
	public static List<String> run(List<ITXLCommand> commands, byte[] file, int language) {
		//long time = System.currentTimeMillis();
		
	// Build Command
		String chain = "";
		
		//if(OS.isFamilyWindows()) {
		//	chain += "cat `cygpath -up '" + file.toString() + "'`";
		//} else {
		//	chain += "cat " + file.toString();
		//}
		
		Iterator<ITXLCommand> iter = commands.iterator();
		
		while(iter.hasNext()) {
			ITXLCommand command = iter.next();
			if(command.forLanguage(language)) {
				if(command.existsExec(language)) {
					chain += command.getCommandExec(language);
				} else if (command.existsScript(language)) {
					chain += command.getCommandScript(language);
				} else {
					System.err.println("One of the TXL commands is impossible to execute (does not exist in script or compiled): " + command.toString());
					System.exit(-1);
				}
				if(iter.hasNext())
					chain += " | ";
			}
		}
		
		List<String> exec = new LinkedList<String>();
		
		//System.out.println(chain);
		
		exec.add("bash");
		exec.add("-c");
		
		exec.add("" + chain + "");
		
		//for(String s : exec) {
		//	System.out.print(s + " ");
		//}
		//System.out.println("");
		
		
	// Execute Process and Collect Output
		int retval = 0;
		List<String> lines = null;
		ProcessBuilder pb = new ProcessBuilder(exec);
		Process p = null;
		try {
			p = pb.start();
			new StreamGobbler(p.getErrorStream()).start();
			
			// Write
			OutputStream out = p.getOutputStream();
			IOUtils.write(file, out);
			out.close();
			
			// Read
			lines = IOUtils.readLines(new InputStreamReader(p.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		
		// Wait for completion
		boolean success = false;
		do {
		try {
			retval = p.waitFor();
			success = true;
		} catch (InterruptedException e1) {
		}
		} while(!success);
		p.destroy();
		
		if(retval != 0) {
			//System.out.println("	FAILED: " + file + " Code: " + retval);
			return null;
		}
		
		//System.out.println(System.currentTimeMillis() - time);
		
		return lines;
	}
}
