package input.txl;

public class TXLExtract implements ITXLCommand {

	private String language;
	private String block_granularity;
	
	public TXLExtract(String language, String block_granularity) {
		this.language = language;
		this.block_granularity = block_granularity;
	}
	
	@Override
	public String getCommand() {
		return TXLUtil.getTXLRoot() + "/" + language + "-extract-" + block_granularity + "s.x stdin";
	}

}
