package input.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import constants.BlockGranularityConstants;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.block.InputBlock;
import input.file.InputFile;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;
import input.utils.FilePathStreamUtil;
import input.worker.BlockConsumer_BlockWriter;
import input.worker.FileConsumer_BlockProducer;
import input.worker.FileProducer;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class InputBuilder {
	
	public static void main(String args[]) throws InterruptedException, IOException {
		long time = System.currentTimeMillis();
		
		//Path root = Paths.get("/home/jeff/Applications/NiCad-4.0/examples/JHotDraw54b1/");
		Path root = Paths.get("C:/Users/jeffs/Desktop/NiCad-4.0/examples/JHotDraw54b1/");
		IOFileFilter filter = new SuffixFileFilter(".java");
		
		Path ffileids = Paths.get("files.ids");
		Path fblocks = Paths.get("blocks");
		
	// Extract Properties
		String language = LanguageConstants.JAVA;
		String block_granularity = BlockGranularityConstants.FUNCTION;
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
		
	// Properties
		int numthreads = 1;
		
	// Output
		Writer fileids_writer = new FileWriter(ffileids.toFile());
		Writer block_writer = new FileWriter(fblocks.toFile());
		
	// Queues
		IQueue<InputFile> file_queue = QueueBuilder.<InputFile>groupQueue_arrayBacked(50, 20);
		IQueue<InputBlock> block_queue = QueueBuilder.<InputBlock>groupQueue_arrayBacked(500000, 20);
		
	// Initialize Workers
		FileProducer fp = new FileProducer(FilePathStreamUtil.createReadAtOnceFilePathStream(root, filter), file_queue.getEmitter(), fileids_writer);
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
