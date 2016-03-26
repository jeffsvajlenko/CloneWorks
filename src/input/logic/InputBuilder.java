package input.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import constants.BlockGranularityConstants;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.block.InputBlock;
import input.file.InputFile;
import input.tokenprocessors.FilterOperators;
import input.tokenprocessors.FilterSeperators;
import input.tokenprocessors.ITokenProcessor;
import input.tokenprocessors.RemoveEmpty;
import input.tokenprocessors.SplitStrings;
import input.tokenprocessors.Stemmer;
import input.tokenprocessors.ToLowerCase;
import input.txl.ITXLCommand;
import input.utils.FilePathStreamUtil;
import input.worker.BlockConsumer_BlockWriter;
import input.worker.FileConsumer_BlockProducer;
import input.worker.FileProducer;

public class InputBuilder {
	
	public static void main(String args[]) throws InterruptedException, IOException {
		long time = System.currentTimeMillis();
		
		Path root = Paths.get("C:/Users/jeffs/Desktop/NiCad-4.0/examples/JHotDraw54b1/");
		IOFileFilter filter = new SuffixFileFilter(".java");
		
		Path ffileids = Paths.get("files.ids");
		Path fblocks = Paths.get("blocks");
		int numthreads = 4;
		
		String language = LanguageConstants.JAVA;
		String block_granularity = BlockGranularityConstants.FUNCTION;
		String token_granularity = TokenGranularityConstants.TOKEN;
		List<ITXLCommand> txl_normalizations = new ArrayList<ITXLCommand>(0);
		List<ITokenProcessor> token_processors = new ArrayList<ITokenProcessor>(0);
		
		BlockingQueue<List<InputFile>> qfiles = new ArrayBlockingQueue<List<InputFile>>(50);
		BlockingQueue<List<InputBlock>> qblocks = new ArrayBlockingQueue<List<InputBlock>>(50);
		
		Writer fileids_writer = new FileWriter(ffileids.toFile());
		Writer block_writer = new FileWriter(fblocks.toFile());
		
		//txl_normalizations.add("rename-blind");
		
		//token_processors.add(new FilterOperators(language));
		//token_processors.add(new FilterSeperators(language));
		//token_processors.add(new NormalizeStrings());
		//token_processors.add(new SplitStrings());
		//token_processors.add(new ToLowerCase());
		//token_processors.add(new RemoveEmpty());
		//token_processors.add(new Stemmer());
		
	// Initialize Workers
		FileProducer fp = new FileProducer(FilePathStreamUtil.createReadAtOnceFilePathStream(root, filter), qfiles, fileids_writer, numthreads*5);
		FileConsumer_BlockProducer fc_bp[] = new FileConsumer_BlockProducer[numthreads];
		for(int i = 0; i < numthreads; i++)
			fc_bp[i] = new FileConsumer_BlockProducer(qfiles, qblocks,
													  language, block_granularity, token_granularity, txl_normalizations,
													  token_processors);
		BlockConsumer_BlockWriter bc_bw = new BlockConsumer_BlockWriter(block_writer, qblocks);
		
	// Execute
		fp.start();
		for(int i = 0; i < numthreads; i++)
			fc_bp[i].start();
		bc_bw.start();
		
		fp.join();										// Wait for all files to be queued
		System.out.println("FileProducer:" + fp.getExitStatus() + " - " + fp.getExitMessage());
		for(int i = 0; i < numthreads; i++)
			qfiles.put(new ArrayList<InputFile>(0));	// Poison file consumers
		for(int i = 0; i < numthreads; i++) {			// Wait for FileConsumer/BlockProducers to complete
			fc_bp[i].join();
			System.out.println("FileConsumer/BlockProducer[" + i + "] - " + fp.getExitStatus() + " - " + fp.getExitMessage());
		}
		qblocks.put(new ArrayList<InputBlock>(0));
		bc_bw.join();
		System.out.println("BlockConsumer/BlockWriter - " + bc_bw.getExitStatus() + " - " + bc_bw.getExitMessage());
		
		fileids_writer.flush();
		fileids_writer.close();
		block_writer.flush();
		block_writer.close();
		
		time = System.currentTimeMillis() - time;
		System.out.println(time/1000.0 + " seconds.");
	}
	
}
