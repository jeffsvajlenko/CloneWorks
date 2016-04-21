package input.logic;

import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

import input.block.InputBlock;
import input.file.InputFile;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;
import input.worker.BlockConsumer_BlockWriter;
import input.worker.FileConsumer_BlockProducer;
import input.worker.FileProducer_FromRoot;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class InputBuilder {
	
	public static void parse(Path root,
						     Path fileids,
						     Path blocks,
						     String language,
						     String block_granularity,
						     String token_granularity,
						     FileFilter filter,
						     List<ITokenProcessor> token_processors,
						     List<ITXLCommand> txl_normalizations,
						     int numthreads
						    ) throws InterruptedException, IOException  {
		
		Thread.UncaughtExceptionHandler exception_handler = new Thread.UncaughtExceptionHandler() {
		    public void uncaughtException(Thread th, Throwable ex) {
		        System.out.println("Failed with exception: " + ex);
		        System.exit(-1);
		    }
		};
		
	// Echo Config
		System.out.println("ThriftyClone - InputBuilder");
		System.out.println("Input = " + root);
		System.out.println("Files = " + fileids);
		System.out.println("Blocks = " + blocks);
		System.out.println("Language = " + language);
		System.out.println("Block Granularity = " + block_granularity);
		System.out.println("Token Granularity = " + token_granularity);
		System.out.println("TXL Normalizations:");
		for(ITXLCommand command : txl_normalizations) {
			System.out.println("\t" + command);
		}
		System.out.println("Token Processors:");
		for(ITokenProcessor processor : token_processors) {
			System.out.println("\t" + processor);
		}
		
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
		fp.setUncaughtExceptionHandler(exception_handler);
		fp.start();
		for(int i = 0; i < numthreads; i++) {
			fc_bp[i].setUncaughtExceptionHandler(exception_handler);
			fc_bp[i].start();
		}
		bc_bw.setUncaughtExceptionHandler(exception_handler);
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
		
	}
	
}
