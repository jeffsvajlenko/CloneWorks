package input.txl;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import constants.BlockGranularityConstants;
import constants.LanguageConstants;

public class TXLTokenize implements ITXLCommand {
	
	public String toString() {
		return "tokenize " + block_granularity;
	}
	
	private String block_granularity;
	private Map<Integer,String> m_script;
	private Map<Integer,String> m_executable;
	
	public TXLTokenize(int block_granularity) {
		this.block_granularity = BlockGranularityConstants.getString(block_granularity);
		this.m_script = new HashMap<Integer,String>();
		this.m_executable = new HashMap<Integer,String>();
		
		for(int lang : LanguageConstants.getSupportedLanguages()) {
			String language = LanguageConstants.getString(lang);
			
			// Script
			String script = language + "-tokenize-" + this.block_granularity + "s.txl";
			if(Files.exists(TXLUtil.getTXLRoot().resolve(script)))
				this.m_script.put(lang, script);
			
			// Executable
			String exec = language + "-tokenize-" + this.block_granularity + "s.x";
			if(Files.exists(TXLUtil.getTXLRoot().resolve(exec)))
				this.m_executable.put(lang, exec);
		}
	}
	
	@Override
	public boolean existsExec(int language) {
		return this.m_executable.containsKey(language);
	}
	
	@Override
	public boolean existsScript(int language) {
		return this.m_script.containsKey(language);
	}
	
	@Override
	public String getCommandExec(int language) {
		String exec = this.m_executable.get(language);
		if(exec == null)
			throw new IllegalArgumentException("Language not supported/available.");
		return TXLUtil.getTXLRoot() + "/" + exec + " stdin";
	}

	@Override
	public String getCommandScript(int language) {
		String script = this.m_script.get(language);
		if(script == null)
			throw new IllegalArgumentException("Language not supported/available.");
		return "txl stdin " + TXLUtil.getTXLRoot() + "/" + script;
	}

	@Override
	public boolean forLanguage(int language) {
		return true;
	}
	

}
