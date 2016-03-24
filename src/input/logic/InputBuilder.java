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

import constants.BlockGranularityConstants;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.block.InputBlock;
import input.file.InputFile;
import input.tokenprocessors.ITokenProcessor;
import input.utils.FilePathStreamUtil;
import input.worker.BlockConsumer_BlockWriter;
import input.worker.FileConsumer_BlockProducer;
import input.worker.FileProducer;

public class InputBuilder {
	
	public static void main(String args[]) throws InterruptedException, IOException {
		Path finput = Paths.get("/home/jeff/files");
		Path ffileids = Paths.get("/home/jeff/files.ids");
		Path fblocks = Paths.get("/home/jeff/blocks");
		int numthreads = 1;
		
		String language = LanguageConstants.JAVA;
		String block_granularity = BlockGranularityConstants.FUNCTION;
		String token_granularity = TokenGranularityConstants.TOKEN;
		List<String> txl_normalizations = new ArrayList<String>(0);
		List<ITokenProcessor> token_processors = new ArrayList<ITokenProcessor>(0);
		
		BlockingQueue<List<InputFile>> qfiles = new ArrayBlockingQueue<List<InputFile>>(50);
		BlockingQueue<List<InputBlock>> qblocks = new ArrayBlockingQueue<List<InputBlock>>(50);
		
		Writer fileids_writer = new FileWriter(ffileids.toFile());
		Writer block_writer = new FileWriter(fblocks.toFile());
		
	// Initialize Workers
		FileProducer fp = new FileProducer(FilePathStreamUtil.createFilePathStream(finput), qfiles, fileids_writer, numthreads*5);
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
		
	}
	
}
