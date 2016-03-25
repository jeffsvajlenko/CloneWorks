package input.txl;

public class TXLNormalization implements ITXLCommand {

	private String name;
	private String[] arguments;
	private String language;
	private String block_granularity;
	
	public TXLNormalization(String name, String[] arguments, String language, String block_granularity) {
		this.name = name;
		this.arguments = arguments;
		this.language = language;
		this.block_granularity = block_granularity;
	}

	@Override
	public String getCommand() {
		String command = TXLUtil.getTXLRoot() + "/" + language + "-" + name + "-" + block_granularity + "s.x stdin";
		for(String str : arguments) {
			command += str + " ";
		}
		return command;
	}
}
