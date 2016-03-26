package input.txl;

public interface ITXLCommand {

	public String getCommandExec();
	public String getCommandScript();
	public boolean existsExec();
	public boolean existsScript();
	
}
