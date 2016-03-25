package input.txl;

public class TXLTokenize implements ITXLCommand {

	private String language;
	private String block_granularity;
	
	public TXLTokenize(String language, String block_granularity) {
		this.language = language;
		this.block_granularity = block_granularity;
	}
	
	@Override
	public String getCommand() {
		return TXLUtil.getTXLRoot() + "/" + language + "-tokenize-" + block_granularity + "s.x stdin";
	}

}
