package interfaces;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import constants.BlockGranularityConstants;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.tokenprocessors.ITokenProcessor;
import input.tokenprocessors.Joiner;
import input.tokenprocessors.RemoveEmpty;
import input.tokenprocessors.TrimLeadingTrailingWhitespace;
import input.txl.ITXLCommand;

public class Type1InputBuilder {
	public static void main(String args[]) throws InterruptedException, IOException {
		if(args.length != 6) {
			System.out.println("Usage: system fileids_out blocks_out language granularity token_type numthreads");
			System.out.println("               system: Path to input system.");
			System.out.println("          fileids_out: File to track file ids.");
			System.out.println("           blocks_out: File to write tokenized blocks to.");
			System.out.println("             language: Language of input system.  One of: {java,c,cs,py}.");
			System.out.println("    block_granularity: The granularity of the blocks.  One of: {file,function,block}.");
			System.out.println("           numthreads: Number of threads to use per parallelized task.");
			System.exit(-1);
		}
		
		Path root                = Paths.get(args[0]);
		Path fileids             = Paths.get(args[1]);
		Path blocks              = Paths.get(args[2]);
		String language          = LanguageConstants.getCanonical(args[3]);
		String block_granularity = BlockGranularityConstants.getCanonical(args[4]);
		String token_granularity = TokenGranularityConstants.LINE;
		int numthreads           = Integer.parseInt(args[5]);
		
		FileFilter filter = LanguageConstants.getFileFilter(language);
		
		// Token Processors
		List<ITokenProcessor> token_processors = new ArrayList<ITokenProcessor>(0);
		token_processors.add(new TrimLeadingTrailingWhitespace());
		token_processors.add(new RemoveEmpty());
		token_processors.add(new Joiner());
		//token_processors.add(new FilterOperators(language));
		//token_processors.add(new FilterSeperators(language));
		//token_processors.add(new NormalizeStrings());
		//token_processors.add(new SplitStrings());
		//token_processors.add(new ToLowerCase());
		//token_processors.add(new RemoveEmpty());
		//token_processors.add(new Stemmer());
		
		// TXL Normalizations
		List<ITXLCommand> txl_normalizations = new ArrayList<ITXLCommand>(0);
		
		
		
		// RUN
		long time = System.currentTimeMillis();
		input.logic.InputBuilder.parse( /*Input*/ root,
						          /*File Output*/ fileids,
						         /*Block Output*/ blocks,
						             /*Language*/ language,
						    /*Block Granularity*/ block_granularity,
						    /*Token Granularity*/ token_granularity,
						          /*File Filter*/ filter,
						     /*Token Processors*/ token_processors,
						   /*TXL Normalizations*/ txl_normalizations,
						    /*Number of Threads*/ numthreads
				          );
		time = System.currentTimeMillis() - time;
		System.out.println(time/1000.0 + " seconds.");
	}
}
