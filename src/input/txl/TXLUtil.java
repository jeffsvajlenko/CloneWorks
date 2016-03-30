package input.txl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.StreamPumper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

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
			
			if(command.existsExec()) {
				chain += command.getCommandExec();
				
			} else if (command.existsScript()) {
				chain += command.getCommandScript();
			} else {
				System.err.println("One of the TXL commands is impossible to execute (does not exist in script or compiled).");
				System.exit(-1);
			}
			
			if(iter.hasNext())
				chain += " | ";
		}
		
		List<String> exec = new LinkedList<String>();
		
		if(SystemUtils.IS_OS_WINDOWS) {
			exec.add("powershell.exe");
		} else {
			exec.add("sh");
			exec.add("-c");
		}
		
		exec.add(chain);
		
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
			return null;
		}
		
		return lines;
	}
	
	public static List<String> run2(List<ITXLCommand> commands, Path file) {
		//long time = System.currentTimeMillis();
		
		List<ProcessBuilder> pbs = new ArrayList<ProcessBuilder>(commands.size()+1);
		List<Process> processes = new ArrayList<Process>(commands.size()+1);
		
		List<String> retval = new LinkedList<String>();
		
		pbs.add(new ProcessBuilder("cmd","/c","type " + file.toString()));
			
		for(ITXLCommand cmd : commands) {
			String command;
			if(cmd.existsExec())
				command = cmd.getCommandExec();
			else if(cmd.existsScript())
				command = cmd.getCommandScript();
			else {
				return null;
			}
			pbs.add(new ProcessBuilder(command.split("\\s+")));
		}
			
		try {
			for(int i = 0; i < pbs.size(); i++) {
				Process p = pbs.get(i).start();
				processes.add(p);
				new StreamGobbler(p.getErrorStream()).start();
			}
			
			for(int i = 1; i < pbs.size(); i++) {
				StreamPumper pipe = new StreamPumper(processes.get(i-1).getInputStream(), processes.get(i).getOutputStream(), true);
				Thread thread = new Thread(pipe);
				thread.start();
			}
			
			retval = IOUtils.readLines(processes.get(processes.size()-1).getInputStream());
			
			for(Process p : processes) {
				p.waitFor();
				p.destroy();
			}
			
		} catch(Exception e) {
			for(Process p : processes) {
				try {
					p.destroy();
				} catch(Exception ee) {
					ee.printStackTrace();
				}
			}
			e.printStackTrace();
			return null;
		}
		//System.out.println(System.currentTimeMillis() - time);
		return retval;
	}
}
