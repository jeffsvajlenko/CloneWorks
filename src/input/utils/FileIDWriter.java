package input.utils;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.sql.Timestamp;

import constants.LanguageConstants;
import input.termprocessors.ITermProcessor;
import input.txl.ITXLCommand;
import interfaces.InputBuilderConfiguration;

public class FileIDWriter {

	private Writer out;
	
	public FileIDWriter(Writer out, InputBuilderConfiguration config, Timestamp time) throws IOException {
		this.out = out;
		
		// Write Header
		this.out.write("#timestamp=" + time + "\n");
		this.out.write("#system=" + config.getSystem() + "\n");
		this.out.write("#fileids=" + config.getFileids() + "\n");
		this.out.write("#blocks=" + config.getBlocks() + "\n");
		this.out.write("#languages=");
		for(int language : config.getLanguages()) {
			this.out.write(LanguageConstants.getString(language));
		}
		this.out.write("\n");
		this.out.write("#granularity=" + config.getBlock_granularity() + "\n");
		this.out.write("#configuration=" + config.getConfigFile() + "\n");
		int i = 1;
		for(ITXLCommand txlc : config.getTxl_commands()) {
			this.out.write("#txl[" + (i++) + "]=" + txlc + "\n");
		}
		i = 1;
		for(ITermProcessor tokproc : config.getToken_processors()) {
			this.out.write("#tokproc[" + (i++) + "]=" + tokproc + "\n");
		}
	}
	
	public void write(long id, Path path) throws IOException {
		out.write(id + "\t" + path + "\n");
	}
	
	public void close() throws IOException {
		out.close();
	}
	
}
