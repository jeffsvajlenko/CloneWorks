package interfaces;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.time.DurationFormatUtils;

import constants.BlockGranularityConstants;
import constants.InstallDir;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;

public class InputBuilder {

	private static Options options;
	private static HelpFormatter formatter;
	
	private static void panic() {
		formatter.printHelp(200, "cwbuild", "CloneWorks-InputBuilder - CloneWorks input builder.", options, "", true);
		System.exit(-1);
		return;
	}
	
	public static void main(String args[]) throws InterruptedException, IOException {
		options = new Options();
		
		// System
		options.addOption(Option.builder("i")
								.longOpt("input")
								.required()
							    .hasArg()
							    .argName("path")
							    .desc("Input subject system.  Either the root directory of the system, or a file containing"
							      + "\na list of source file paths.")
						        .build()
		);
		
		// FileIDs
		options.addOption(Option.builder("f")
				                .longOpt("fileids")
				                .required()
				                .hasArg()
				                .argName("path")
				                .desc("File to write the FileID<->FilePath mapping to.")
				                .build()
		);
		
		// Blocks
		options.addOption(Option.builder("b")
								.longOpt("blocks")
								.required()
								.hasArg()
								.argName("path")
								.desc("File to write the parsed blocks to.")
				                .build()
		);
		
		// Language
		options.addOption(Option.builder("l")
								.longOpt("language")
								.required()
								.hasArgs()
								.argName("str [str2 st3 ...]")
								.desc("The language of the input system.  One of: {java,c,cpp,cs,python}.")
				                .build()
		);
		
		// Granularity
		options.addOption(Option.builder("g")
								.longOpt("granularity")
								.required()
								.hasArg()
								.argName("str")
								.desc("The block granularity.  One of: {block,function,file}.")
				                .build()
		);
		
		// Config File
		options.addOption(Option.builder("c")
								.longOpt("configuration")
								.required()
								.hasArg()
								.argName("config")
								.desc("The configuration file.  Name of a file in INSTALL_DIR/config/.")
				                .build()
		);
		
		// Num Threads
		options.addOption(Option.builder("t")
								.longOpt("num-threads")
								.hasArg()
								.argName("num")
								.desc("The number of threads to use per execution task.  Default is number of available cores.")
				                .build()
		);
		
		// Minimum Clone Size - Original Lines		
		options.addOption(Option.builder("mil")
				                .longOpt("min-lines")
				                .hasArg()
				                .argName("num")
				                .desc("Minimum code fragment size in (original) source lines.")
				                .build()
		);
				
		// Maximum Clone Size - Original Lines
		options.addOption(Option.builder("mal")
				                .longOpt("max-lines")
				                .hasArg()
				                .argName("num")
				                .desc("Maximum code fragment size in (original) source lines.")
				                .build()
		);
				
		// Minimum Tokens - Original Tokens
		options.addOption(Option.builder("mit")
				                .longOpt("min-tokens")
				                .hasArg()
				                .argName("num")
				                .desc("Minimum code fragment size in (original, pre-processing) parsed tokens.")
				                .build()
		);
				
		// Maximum Tokens - Original Tokens
		options.addOption(Option.builder("mat")
				                .longOpt("max-tokens")
				                .hasArg()
				                .argName("num")
				                .desc("Maximum code fragment size in (original, pre-processing) parsed tokens.")
				                .build()
		);
		
		String system;
		String fileids;
		String blocks;
		String [] language;
		String granularity;
		String configfile;
		String numthreads = null;
		String minlines = null;
		String maxlines = null;
		String mintokens = null;
		String maxtokens = null;
		
		formatter = new HelpFormatter();
		formatter.setOptionComparator(null);	
		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch (Exception e) {
			System.out.println("\n" + e.getMessage() + "\n");
			panic();
			return;
		}
		
		
		InstallDir.setInstallDir(Paths.get(line.getArgList().get(0)));
		
		system = line.getOptionValue("i");
		fileids = line.getOptionValue("f");
		blocks = line.getOptionValue("b");
		language = line.getOptionValues("l");
		granularity = line.getOptionValue("g");
		configfile = line.getOptionValue("c");
		if(line.hasOption("t"))
			numthreads = line.getOptionValue("t");
		minlines = line.getOptionValue("mil");
		maxlines = line.getOptionValue("mal");
		mintokens = line.getOptionValue("mit");
		maxtokens = line.getOptionValue("mat");
		
		InputBuilderConfiguration config;
		try {
			config = new InputBuilderConfiguration(system, fileids, blocks, language, granularity, configfile, numthreads, minlines, maxlines, mintokens, maxtokens);
		} catch(ConfigurationException e) {
			System.err.println("Error: " + e.getMessage());
			System.out.println("");
			panic();
			return;
		}
		
		// Echo Config
		System.out.println();
		System.out.println("CloneWorks Version 1.0");
		System.out.println();
		System.out.println("Input Builder");
		System.out.println();
		System.out.println("System=" + config.getSystem().toAbsolutePath());
		System.out.println("FileIDs=" + config.getFileids().toAbsolutePath());
		System.out.println("Blocks=" + config.getBlocks().toAbsolutePath());
		System.out.println("Configuration=" + configfile);
		System.out.print("Languages=");
		for(int lang : config.getLanguages()) {
			System.out.print(LanguageConstants.getString(lang) + ",");
		}
		System.out.println("");
		
		
		//LanguageConstants.getString(config.getLanguages())
		
		
		System.out.println("Granularity=" + BlockGranularityConstants.getString(config.getBlock_granularity()));
		System.out.println("TokenType=" + TokenGranularityConstants.getString(config.getTokenType()));
		
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
