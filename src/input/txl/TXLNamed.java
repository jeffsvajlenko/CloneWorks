package input.txl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TXLNamed implements ITXLCommand {

	@Override
	public String toString() {
		return script;
	}
	
	String name;
	
	private String script;
	private String executable;
	
	private boolean existsExec;
	private boolean existsScript;
	
	public TXLNamed(String name) {
		script = name + ".txl";
		executable = name + ".x";
		existsExec = Files.exists(Paths.get(TXLUtil.getTXLRoot() + "/" + executable));
		existsScript = Files.exists(Paths.get(TXLUtil.getTXLRoot() + "/" + script));
	}
	
	@Override
	public String getCommandExec() {
		return TXLUtil.getTXLRoot() + "/" + executable + " stdin ";
	}

	@Override
	public String getCommandScript() {
		return "txl stdin " + TXLUtil.getTXLRoot() + "/" + script;
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
