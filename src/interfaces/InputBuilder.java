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
import input.tokenprocessors.FilterOperators;
import input.tokenprocessors.FilterSeperators;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;

public class InputBuilder {

	public static void main(String args[]) throws InterruptedException, IOException {
		Path root = Paths.get(args[0]);
		Path fileids = Paths.get(args[1]);
		Path blocks = Paths.get(args[2]);
		String language = LanguageConstants.getCanonical(args[3]);
		String block_granularity = BlockGranularityConstants.getCanonical(args[4]);
		String token_granularity = TokenGranularityConstants.getCanonized(args[5]);
		int numthreads = Integer.parseInt(args[6]);
		
		FileFilter filter = LanguageConstants.getFileFilter(language);
		
		// Token Processors
		List<ITokenProcessor> token_processors = new ArrayList<ITokenProcessor>(0);
		token_processors.add(new FilterOperators(language));
		token_processors.add(new FilterSeperators(language));
		//token_processors.add(new NormalizeStrings());
		//token_processors.add(new SplitStrings());
		//token_processors.add(new ToLowerCase());
		//token_processors.add(new RemoveEmpty());
		//token_processors.add(new Stemmer());
		
		// TXL Normalizations
		List<ITXLCommand> txl_normalizations = new ArrayList<ITXLCommand>(0);
		//txl_normalizations.add("rename-blind");
		
		
		
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
