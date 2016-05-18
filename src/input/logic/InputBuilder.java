package input.logic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;

import constants.LanguageConstants;
import input.block.InputBlock;
import input.block.tempblock.SizeTempBlockRequirement;
import input.file.InputFile;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;
import input.utils.BlockWriter;
import input.utils.FileIDWriter;
import input.utils.FilePathStreamUtil;
import input.worker.BlockConsumer_BlockWriter;
import input.worker.FileConsumer_BlockProducer;
import input.worker.FileProducer_FromFileList;
import input.worker.FileProducer_FromRoot;
import interfaces.InputBuilderConfiguration;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class InputBuilder {
	
	public static void parse(InputBuilderConfiguration config) throws InterruptedException, IOException  {
		
		Thread.UncaughtExceptionHandler exception_handler = new Thread.UncaughtExceptionHandler() {
		    public void uncaughtException(Thread th, Throwable ex) {
		        System.err.println("Error: Failed with exception: " + ex);
		    }
		};
		
		
		Path root = config.getSystem();
		Path fileids = config.getFileids();
		Path blocks = config.getBlocks();
		int token_granularity = config.getTokenType();
		int block_granularity = config.getBlock_granularity();
		int numthreads = config.getNumThreads();
		
		//System.out.println(config.getMinLines() + " " + config.getMaxLines() + " " + config.getMinTokens() + " " + config.getMaxTokens());
		SizeTempBlockRequirement requirements = new SizeTempBlockRequirement(config.getMinLines(), config.getMaxLines(), config.getMinTokens(), config.getMaxTokens());
		//System.out.println(requirements.getMinlines() + " " + requirements.getMaxlines() + " " + requirements.getMintokens() + " " + requirements.getMaxtokens());
		
		List<ITokenProcessor> token_processors = config.getToken_processors();
		List<ITXLCommand> txl_normalizations = config.getTxl_commands();
		
		
	// Output
		Writer fileids_writer = new FileWriter(fileids.toFile());
		Writer block_writer = new FileWriter(blocks.toFile());
		
	// Write output headers
		Date date= new Date();
		Timestamp ts = new Timestamp(date.getTime());
		BlockWriter bw = new BlockWriter(block_writer, config, ts);
		FileIDWriter fidw = new FileIDWriter(fileids_writer, config, ts);
		
		
		
	// Queues
		IQueue<InputFile> file_queue = QueueBuilder.<InputFile>groupQueue_arrayBacked(50, 20);
		IQueue<InputBlock> block_queue = QueueBuilder.<InputBlock>groupQueue_arrayBacked(500000, 20);
		
	// Initialize Workers
		Thread fp;
		if(Files.isDirectory(root)) {
			List<IOFileFilter> filters = new LinkedList<IOFileFilter>();
			for(int lang : config.getLanguages()) {
				filters.add(LanguageConstants.getFileFilter(lang));
			}
			OrFileFilter filter = new OrFileFilter(filters);

			fp = new FileProducer_FromRoot(root, filter, file_queue.getEmitter(), fidw);
		} else {
			fp = new FileProducer_FromFileList(FilePathStreamUtil.createFilePathStream(root), file_queue.getEmitter(), fidw);
		}
		FileConsumer_BlockProducer fc_bp[] = new FileConsumer_BlockProducer[numthreads];
		for(int i = 0; i < numthreads; i++)
			fc_bp[i] = new FileConsumer_BlockProducer(file_queue.getReceiver(), block_queue.getEmitter(),
													  block_granularity, token_granularity, txl_normalizations,
													  token_processors, requirements);
		BlockConsumer_BlockWriter bc_bw = new BlockConsumer_BlockWriter(bw, block_queue.getReceiver());
		
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
		//System.out.println("FileProducer:" + fp.getExitStatus() + " - " + fp.getExitMessage());
		file_queue.poisonReceivers();
		//System.out.println("Files Queue Poisoned");
		for(int i = 0; i < numthreads; i++) {
			fc_bp[i].join();
			//System.out.println("FileConsumer/BlockProducer[" + i + "] - " + fp.getExitStatus() + " - " + fp.getExitMessage());
		}
		block_queue.poisonReceivers();
		bc_bw.join();
		//System.out.println("BlockConsumer/BlockWriter - " + bc_bw.getExitStatus() + " - " + bc_bw.getExitMessage());
		
	// Flush Output
		fileids_writer.flush();
		fileids_writer.close();
		block_writer.flush();
		block_writer.close();
	}

}
