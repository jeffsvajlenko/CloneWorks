package input.logic;

import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.block.InputBlock;
import input.file.InputFile;
import input.tokenprocessors.FilterOperators;
import input.tokenprocessors.FilterSeperators;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;
import input.worker.BlockConsumer_BlockWriter;
import input.worker.FileConsumer_BlockProducer;
import input.worker.FileProducer_FromRoot;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class InputBuilder {
	
	
	// root fileids blocks language granularity
	public static void main(String args[]) throws InterruptedException, IOException {
		Path root = Paths.get(args[0]);
		Path fileids = Paths.get(args[1]);
		Path blocks = Paths.get(args[2]);
		String language = args[3].toLowerCase();
		FileFilter filter = LanguageConstants.getFileFilter(language);
		String granularity = args[4].toLowerCase();
		int numthreads = Integer.parseInt(args[5]);
		InputBuilder.parse(root, fileids, blocks, filter, numthreads, granularity);
	}
	
	public static void parse(Path root, Path fileids, Path blocks, FileFilter filter, int numthreads, String block_granularity) throws InterruptedException, IOException  {
	
		long time = System.currentTimeMillis();
		
	// Extract Properties
		String language = LanguageConstants.JAVA;
		String token_granularity = TokenGranularityConstants.TOKEN;
		
	// TXL Normalizations
		List<ITXLCommand> txl_normalizations = new ArrayList<ITXLCommand>(0);
		//txl_normalizations.add("rename-blind");
		
	// Token Processors
		List<ITokenProcessor> token_processors = new ArrayList<ITokenProcessor>(0);
		//token_processors.add(new FilterOperators(language));
		//token_processors.add(new FilterSeperators(language));
		//token_processors.add(new NormalizeStrings());
		//token_processors.add(new SplitStrings());
		//token_processors.add(new ToLowerCase());
		//token_processors.add(new RemoveEmpty());
		//token_processors.add(new Stemmer());
		
	// Output
		Writer fileids_writer = new FileWriter(fileids.toFile());
		Writer block_writer = new FileWriter(blocks.toFile());
		
	// Queues
		IQueue<InputFile> file_queue = QueueBuilder.<InputFile>groupQueue_arrayBacked(50, 20);
		IQueue<InputBlock> block_queue = QueueBuilder.<InputBlock>groupQueue_arrayBacked(500000, 20);
		
	// Initialize Workers
		FileProducer_FromRoot fp = new FileProducer_FromRoot(root, filter, file_queue.getEmitter(), fileids_writer);
		FileConsumer_BlockProducer fc_bp[] = new FileConsumer_BlockProducer[numthreads];
		for(int i = 0; i < numthreads; i++)
			fc_bp[i] = new FileConsumer_BlockProducer(file_queue.getReceiver(), block_queue.getEmitter(),
													  language, block_granularity, token_granularity, txl_normalizations,
													  token_processors);
		BlockConsumer_BlockWriter bc_bw = new BlockConsumer_BlockWriter(block_writer, block_queue.getReceiver());
		
	// Execute
		// Start
		fp.start();
		for(int i = 0; i < numthreads; i++)
			fc_bp[i].start();
		bc_bw.start();
		
		// Wait-Poison
		fp.join();
		System.out.println("FileProducer:" + fp.getExitStatus() + " - " + fp.getExitMessage());
		file_queue.poisonReceivers();
		System.out.println("Files Queue Poisoned");
		for(int i = 0; i < numthreads; i++) {
			fc_bp[i].join();
			System.out.println("FileConsumer/BlockProducer[" + i + "] - " + fp.getExitStatus() + " - " + fp.getExitMessage());
		}
		block_queue.poisonReceivers();
		bc_bw.join();
		System.out.println("BlockConsumer/BlockWriter - " + bc_bw.getExitStatus() + " - " + bc_bw.getExitMessage());
		
	// Flush Output
		fileids_writer.flush();
		fileids_writer.close();
		block_writer.flush();
		block_writer.close();
		
	// End
		time = System.currentTimeMillis() - time;
		System.out.println(time/1000.0 + " seconds.");
	}
	
}
