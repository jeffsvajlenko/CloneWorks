package input.txl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import util.StreamGobbler;

public class TXLUtil {
	
	public static String getTXLRoot() {
		return "./txl";
	}
	
	public static String getPrintFileCommand(Path p) {
		return "cat " + p.toString();
	}
	
	public static List<String> run(List<ITXLCommand> commands, Path file) {
		
	// Build Command
		String chain = "";
		chain += TXLUtil.getPrintFileCommand(file) + " | ";
		Iterator<ITXLCommand> iter = commands.iterator();
		
		while(iter.hasNext()) {
			ITXLCommand command = iter.next();
			chain += command.getCommand();
			if(iter.hasNext())
				chain += " | ";
		}
		
		List<String> exec = new LinkedList<String>();
		exec.add("/bin/sh");
		exec.add("-c");
		exec.add(chain);
		
		System.out.println(chain);
		
	// Execute Process and Collect Output
		int retval = 0;
		List<String> lines = null;
		ProcessBuilder pb = new ProcessBuilder(exec);
		Process p = null;
		try {
			p = pb.start();
			new StreamGobbler(p.getErrorStream()).start();
			lines = IOUtils.readLines(new InputStreamReader(p.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if(retval != 0) {
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
		
		
		return lines;
	}
	
}