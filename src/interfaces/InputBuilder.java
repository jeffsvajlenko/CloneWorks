package interfaces;

import java.io.IOException;

import org.apache.commons.lang3.time.DurationFormatUtils;

import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;

public class InputBuilder {

	public static void main(String args[]) throws InterruptedException, IOException {
		if (args.length != 6 && args.length != 7) {
			System.out.println("Usage: system fileids blocks language granularity configurationFile [numthreads]");
			System.out.println("         system: Path to input system.");
			System.out.println("       filesids: File to track file ids.");
			System.out.println("         blocks: File to write tokenized blocks to.");
			System.out.println("       language: Language of input system.  One of: {java,c,cs,py}.");
			System.out.println("    granularity: The granularity of the blocks.  One of: {file,function,block}.");
			System.out.println("  configuration: The name of the configuration file in 'config/'.");
			System.out.println("     numthreads: Number of threads to use per parallelized task.  Optional, default is number of cores.");
			System.exit(-1);
			return;
		}
		
		String system = args[0];
		String fileids = args[1];
		String blocks = args[2];
		String language = args[3];
		String granularity = args[4];
		String configfile = args[5];
		String numthreads = null;
		if(args.length == 7) {
			numthreads = args[6];
		}
		
		InputBuilderConfiguration config;
		try {
			config = new InputBuilderConfiguration(system, fileids, blocks, language, granularity, configfile, numthreads);
		} catch(ConfigurationException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(-1);
			return;
		}
		
		// Echo Config
		System.out.println();
		System.out.println("ThriftyClone Version 1.0");
		System.out.println();
		System.out.println("Input Builder");
		System.out.println();
		System.out.println("System=" + config.getSystem().toAbsolutePath());
		System.out.println("FileIDs=" + config.getFileids().toAbsolutePath());
		System.out.println("Blocks=" + config.getBlocks().toAbsolutePath());
		System.out.println("Language=" + config.getLanguage());
		System.out.println("Granularity=" + config.getBlock_granularity());
		System.out.println("Configuration=" + configfile);
		
		if(config.getTxl_commands().size() > 0) {
			System.out.println();
			int i = 1;
			for(ITXLCommand txlc : config.getTxl_commands()) {
				System.out.println("txl[" + (i++) + "]=" + txlc);
			}
		}
		
		if(config.getToken_processors().size() > 0) {
			System.out.println();
			int i = 1;
			for(ITokenProcessor tokproc : config.getToken_processors()) {
				System.out.println("tokproc[" + (i++) + "]=" + tokproc);
			}
		}
		
		System.out.println("");
		System.out.println("Parsing....");
		System.out.println("");
		
		long time = System.currentTimeMillis();
		try {
			input.logic.InputBuilder.parse(config);
		} catch(Exception e) {
			System.out.println("ERROR: Failed with exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
			return;
		}
		time = System.currentTimeMillis() - time;
		System.out.println("");
		System.out.println("Elapsed Time: " + DurationFormatUtils.formatDurationHMS(time));
	}
	
}
