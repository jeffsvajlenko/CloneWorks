package input.worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import constants.BlockGranularityConstants;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.block.InputBlock;
import input.block.tempblock.TempBlock;
import input.block.tempblock.TempBlockRequirements;
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
	
	private int granularity;
	private int tokenType;
	private List<ITXLCommand> txl_normalizations;
	
	private List<ITokenProcessor> token_processors;
	private List<ITXLCommand> commands;
	
	private Integer exitStatus;
	private String exitMessage;
	
	private TempBlockRequirements requirements;
	
	public FileConsumer_BlockProducer(IReceiver<InputFile> files_in, IEmitter<InputBlock> blocks_out,
			                          int granularity, int tokenType, List<ITXLCommand> txl_normalizations,
			                          List<ITokenProcessor> token_processors,
			                          TempBlockRequirements requirements) {
		
		
		this.requirements = requirements;
		
		BlockGranularityConstants.ifInvalidThrowException(granularity);
		TokenGranularityConstants.ifInvalidThrowException(tokenType);
		
		
		this.files_in = files_in;
		this.blocks_out = blocks_out;
		
		this.granularity = granularity;
		this.tokenType = tokenType;
		
		this.txl_normalizations = txl_normalizations;
		
		this.token_processors = token_processors;
		
		//System.out.println("language=" + language);
		//System.out.println("granularity=" + granularity);
		//System.out.println("tokenType=" + tokenType);
		
		// Prepare Command
		commands = new LinkedList<ITXLCommand>();
		commands.add(TXLUtil.getIfDef());
		commands.add(TXLUtil.getPythonPreprocess());
		commands.add(new TXLExtract(this.granularity));
		commands.addAll(this.txl_normalizations);
		if(this.tokenType == TokenGranularityConstants.TOKEN) {
			commands.add(new TXLTokenize(this.granularity));
		}
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
			if(blocks == null)     // Error
				continue;
			
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
		if(blocks == null)
			return null;
	
	// 2 - Filter Blocks by Requirements
		for(Iterator<TempBlock> iterator = blocks.iterator(); iterator.hasNext();) {
			TempBlock block = iterator.next();
			if(!requirements.approve(block)) {
				iterator.remove();
			}
		}
		
	// 3 - Token Processors
		for(ITokenProcessor processor : this.token_processors) {
			for(TempBlock block : blocks) {
				block.setTokens(processor.process(block.getTokens(), file.getLanguage(), this.granularity, this.tokenType));
			}
		}
		
	// 4 - Build InputBlocks
		List<InputBlock> retval = new ArrayList<InputBlock>(blocks.size());
		for(TempBlock block : blocks) {
			if(block.getTokens().size() == 0) // Skip 0-size blocks.
				continue;
			InputBlock iblock = new InputBlock(file.getId(), block.getStartline(), block.getEndline(), block.numOriginalTokens(), file.getLanguage(), this.granularity, this.tokenType, block.getTokens());
			retval.add(iblock);
		}
		
		return retval;
	}
	
	private List<TempBlock> extractBlocks(InputFile file) {
		List<TempBlock> ret = new LinkedList<TempBlock>();

		if(file.getLanguage() == LanguageConstants.UNKOWN) {
			System.out.println("	Can't determine the language of file: " + file.getPath() + ".");
			return null;
		}
		
	// Execute and Capture Output
		List<String> lines = TXLUtil.run(commands, file.getPath(), file.getLanguage());
		if(lines == null) {
			//System.out.println("    Failed for file: " + file + ".");
			return null;
		} else if (lines.size() == 0) {
			//System.out.println("    Failed for file: " + file + ".");
			return null;
		}
		
	// Parse
		boolean inBlock = false;
		int startline=0, endline=0;
		List<String> terms = null;
		for(String line : lines) {
			//System.out.println(line);
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
				line = line.trim();
				if(!line.equals("")) {
					terms.add(line.trim());
				}
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
	
}
