package input.txl;

public interface ITXLCommand {

	public String getCommandExec(int language);
	public String getCommandScript(int language);
	public boolean existsExec(int language);
	public boolean existsScript(int language);
	public boolean forLanguage(int language);
	
}
