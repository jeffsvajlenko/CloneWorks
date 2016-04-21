package input.txl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TXLNormalization implements ITXLCommand {

	@Override
	public String toString() {
		String retval = script;
		for(String arg : arguments) {
			retval += " " + arg;
		}
		return retval;
	}
	
	String[] arguments;
	
	private String script;
	private String executable;
	
	private boolean existsExec;
	private boolean existsScript;
	
	public TXLNormalization(String name, String[] arguments, String language, String block_granularity) {
		script = language + "-" + name + "-" + block_granularity + "s.txl";
		executable = language + "-" + name + "-" + block_granularity + "s.x";
		this.arguments = arguments;
		
		existsExec = Files.exists(Paths.get(TXLUtil.getTXLRoot() + "/" + executable));
		existsScript = Files.exists(Paths.get(TXLUtil.getTXLRoot() + "/" + script));
	}

	@Override
	public String getCommandExec() {
		String command = TXLUtil.getTXLRoot() + "/" + executable + " stdin - ";
		for(String str : arguments) {
			command += str + " ";
		}
		return command;
	}

	@Override
	public String getCommandScript() {
		String command;
		command = "txl stdin " + TXLUtil.getTXLRoot() + "/" + script + " - ";
		for(String str : arguments) {
			command += str + " ";
		}
		return command;
	}

	@Override
	public boolean existsExec() {
		return this.existsExec;
	}

	@Override
	public boolean existsScript() {
		return this.existsScript;
	}
}
