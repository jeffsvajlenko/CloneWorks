package input.txl;

public class TXLOptional implements ITXLCommand {

	private ITXLCommand command;
	private int [] languages;
	
	public TXLOptional(ITXLCommand command, int [] languages) {
		this.command = command;
		this.languages = languages;
	}
	
	@Override
	public String getCommandExec(int language) {
		return command.getCommandExec(language);
	}

	@Override
	public String getCommandScript(int language) {
		return command.getCommandScript(language);
	}

	@Override
	public boolean existsExec(int language) {
		return command.existsExec(language);
	}

	@Override
	public boolean existsScript(int language) {
		return command.existsScript(language);
	}

	@Override
	public boolean forLanguage(int language) {
		for(int lang : this.languages)
			if(lang == language)
				return true;
		return false;
	}

}
