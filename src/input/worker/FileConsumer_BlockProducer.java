package input.worker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import constants.BlockGranularityConstants;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.block.InputBlock;
import input.file.InputFile;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;
import input.txl.TXLExtract;
import input.txl.TXLTokenize;
import input.txl.TXLUtil;
import util.blockingqueue.IEmitter;
import util.blockingqueue.IReceiver;

public class FileConsumer_BlockProducer extends Thread {

	private IReceiver<InputFile> files_in;
	private IEmitter<InputBlock> blocks_out;
	
	private String language;
	private String block_granularity;
	private String token_granularity;
	private List<ITXLCommand> txl_normalizations;
	
	private List<ITokenProcessor> token_processors;
	
	private Integer exitStatus;
	private String exitMessage;
	
	public FileConsumer_BlockProducer(IReceiver<InputFile> files_in, IEmitter<InputBlock> blocks_out,
			                          String language, String block_granularity, String token_granularity, List<ITXLCommand> txl_normalizations,
			                          List<ITokenProcessor> token_processors) {
		
		LanguageConstants.ifInvalidThrowException(language);
		BlockGranularityConstants.ifInvalidThrowException(block_granularity);
		TokenGranularityConstants.ifInvalidThrowException(token_granularity);
		
		
		this.files_in = files_in;
		this.blocks_out = blocks_out;
		
		this.language = language;
		this.block_granularity = block_granularity;
		this.token_granularity = token_granularity;
		this.txl_normalizations = txl_normalizations;
		
		this.token_processors = token_processors;
	}
	
	@Override
	public void run() {
		InputFile file_buffer = null;
		boolean success;
		
		while(true) {
			// Get Files -- In case of interruption, retry.
			success = false;
			do {
				try {
					file_buffer = files_in.take();
					success = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			} while(!success);
			
			// Check for Poison (end-condition)
			if(files_in.isPoisoned()) {
				break;
			}
			
			// Get Blocks
			List<InputBlock> blocks = getBlocks(file_buffer);
			
			// Put Blocks -- In case of interruption, retry.
			for(InputBlock block : blocks) {
				success = false;
				do {
					try {
						blocks_out.put(block);
						success = true;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					success = true;
				} while(!success);
			}
		}
		
		// Flush Emitter.
		success = false;
		do {
			try {
				blocks_out.flush();
				success = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!success);
		
		if(exitStatus == null) {
			exitStatus = 0;
			exitMessage = "Success.";
		}
		
	}

	private List<InputBlock> getBlocks(InputFile file) {
		
	// 1 - Extract Blocks with TXL Normalizations		
		List<TempBlock> blocks = extractBlocks(file);
	
	// 2 - Token Processors
		for(ITokenProcessor processor : this.token_processors) {
			for(TempBlock block : blocks) {
				block.tokens = processor.process(block.tokens);
			}
		}
		
	// 3 - Build InputBlocks
		List<InputBlock> retval = new ArrayList<InputBlock>(blocks.size());
		for(TempBlock block : blocks) {
			InputBlock iblock = new InputBlock(file.getId(), block.startline, block.endline, block.tokens);
			retval.add(iblock);
		}
		
		return retval;
	}
	
	private List<TempBlock> extractBlocks(InputFile file) {
		List<TempBlock> ret = new LinkedList<TempBlock>();
			
	// Execute and Capture Output
		List<ITXLCommand> commands = new LinkedList<ITXLCommand>();
		commands.add(new TXLExtract(this.language, this.block_granularity));
		commands.addAll(this.txl_normalizations);
		if(this.token_granularity == TokenGranularityConstants.TOKEN)
			commands.add(new TXLTokenize(this.language, this.block_granularity));
		List<String> lines = TXLUtil.run2(commands, file.getPath());
		
	// Parse
		boolean inBlock = false;
		int startline=0, endline=0;
		List<String> terms = null;
		for(String line : lines) {
			if(line.startsWith("<source") && !inBlock) {
				inBlock = true;
				terms = new LinkedList<String>();
				String[] parts = line.split("\"");
				startline = Integer.parseInt(parts[3]);
				endline = Integer.parseInt(parts[5]);
			} else if (line.startsWith("</source>") && inBlock) {
				inBlock = false;
				TempBlock block = new TempBlock(startline, endline, terms);
				ret.add(block);
			} else if(inBlock) {
				terms.add(line.trim());
			} else {
				System.err.println("Line found outside of a block: " + line + " for file: " + file.toString());
			}
		}
		
		return ret;
	}
		
	/**
	 * Get exit status.  Null if not yet complete.
	 * @return
	 */
	public Integer getExitStatus() {
		return this.exitStatus;
	}
	
	/**
	 * Get exit message.  Null if not yet complete.
	 * @return
	 */
	public String getExitMessage() {
		return this.exitMessage;
	}
	
	private class TempBlock {
		int startline;
		int endline;
		List<String> tokens;
		
		TempBlock(int startline, int endline, List<String> tokens) {
			this.startline = startline;
			this.endline = endline;
			this.tokens = tokens;
		}
	}
	
}
