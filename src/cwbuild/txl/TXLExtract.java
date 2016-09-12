package cwbuild.txl;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import constants.BlockGranularityConstants;
import constants.LanguageConstants;

/**
 * 
 * 
 *
 */
public class TXLExtract implements ITXLCommand {

	@Override
	public String toString() {
		return "TXLExtract " + block_granularity;
	}
	
	private Map<Integer, String> m_script;
	private Map<Integer, String> m_executable;
	
	private String block_granularity;
	
	/**
	 * 
	 * @param block_granularity
	 * @throws IllegalArgumentException
	 */
	public TXLExtract(int block_granularity) throws IllegalArgumentException {
		this.block_granularity = BlockGranularityConstants.getString(block_granularity);
		this.m_script = new HashMap<Integer, String>();
		this.m_executable = new HashMap<Integer, String>();
		
		for(int lang : LanguageConstants.getSupportedLanguages()) {
			String language = LanguageConstants.getString(lang);
			
			// Script
			String script = language + "-extract-" + this.block_granularity + "s.txl";
			if(Files.exists(TXLUtil.getTXLRoot().resolve(script))) {
				this.m_script.put(lang, script);
			}
			
			// Executable
			String executable = language + "-extract-" + this.block_granularity + "s.x";
			if(Files.exists(TXLUtil.getTXLRoot().resolve(executable))) {
				this.m_executable.put(lang, executable);
			}
		}
	}
	
	@Override
	public String getCommandScript(int language) {
		String script = this.m_script.get(language);
		if(script == null)
			throw new IllegalArgumentException("Language not supported/available.");
		return "txl stdin " + TXLUtil.getTXLRoot() + "/" + script;
	}
	
	@Override
	public String getCommandExec(int language) {
		String exec = this.m_executable.get(language);
		if(exec == null)
			throw new IllegalArgumentException("Language not supported/available.");
		return TXLUtil.getTXLRoot() + "/" + exec + " stdin";
	}

	@Override
	public boolean existsExec(int language) {
		return m_executable.containsKey(language);
	}

	@Override
	public boolean existsScript(int language) {
		return m_script.containsKey(language);
	}

	@Override
	public boolean forLanguage(int language) {
		return true;
	}
	
}
