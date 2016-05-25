package input.utils;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;

import input.block.InputBlock;
import input.termprocessors.ITermProcessor;
import input.transformations.ITransform;
import interfaces.InputBuilderConfiguration;

public class BlockWriter {

	private Writer out;
	
	public BlockWriter(Writer out, InputBuilderConfiguration config, Timestamp time) throws IOException {
		this.out = out;
		
		// Write Header
		this.out.write("#timestamp=" + time + "\n");
		this.out.write("#system=" + config.getSystem() + "\n");
		this.out.write("#fileids=" + config.getFileids() + "\n");
		this.out.write("#blocks=" + config.getBlocks() + "\n");
		this.out.write("#language=" + config.getLanguages() + "\n");
		this.out.write("#granularity=" + config.getBlock_granularity() + "\n");
		this.out.write("#configuration=" + config.getConfigFile() + "\n");
		int i = 1;
		for(ITransform txlc : config.getTransforms()) {
			this.out.write("#txl[" + (i++) + "]=" + txlc + "\n");
		}
		i = 1;
		for(ITermProcessor tokproc : config.getToken_processors()) {
			this.out.write("#tokproc[" + (i++) + "]=" + tokproc + "\n");
		}
	}
	
	public void write(InputBlock block) throws IOException {
		out.write(block.getBlockString() + ">\n");
	}
	
	public void close() throws IOException {
		out.close();
	}
	
}
