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
	
	private int language;
	private int granularity;
	private int tokenType;
	private String strLanguage;
	private String strGranularity;
	private String strTokenType;
	private List<ITXLCommand> txl_normalizations;
	
	private List<ITokenProcessor> token_processors;
	private List<ITXLCommand> commands;
	
	private Integer exitStatus;
	private String exitMessage;
	
	public FileConsumer_BlockProducer(IReceiver<InputFile> files_in, IEmitter<InputBlock> blocks_out,
			                          int language, int granularity, int tokenType, List<ITXLCommand> txl_normalizations,
			                          List<ITokenProcessor> token_processors) {
		
		LanguageConstants.ifInvalidThrowException(language);
		BlockGranularityConstants.ifInvalidThrowException(granularity);
		TokenGranularityConstants.ifInvalidThrowException(tokenType);
		
		
		this.files_in = files_in;
		this.blocks_out = blocks_out;
		
		this.language = language;
		this.granularity = granularity;
		this.tokenType = tokenType;
		
		this.strLanguage = LanguageConstants.getString(language);
		this.strGranularity = BlockGranularityConstants.getString(granularity);
		this.strTokenType = TokenGranularityConstants.getString(tokenType);
		this.txl_normalizations = txl_normalizations;
		
		this.token_processors = token_processors;
		
		// Prepare Command
		commands = new LinkedList<ITXLCommand>();
		if(LanguageConstants.isIfDefLanguage(language))
			commands.add(TXLUtil.getIfDef());
		if(this.strLanguage.equals(LanguageConstants.PYTHON))
			commands.add(TXLUtil.getPythonPreprocess());
		commands.add(new TXLExtract(this.strLanguage, this.strGranularity));
		commands.addAll(this.txl_normalizations);
		if(this.strTokenType.equals(TokenGranularityConstants.TOKEN)) {
			commands.add(new TXLTokenize(this.strLanguage, this.strGranularity));
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
	
	// 2 - Token Processors
		for(ITokenProcessor processor : this.token_processors) {
			for(TempBlock block : blocks) {
				block.tokens = processor.process(block.tokens, this.language, this.granularity, this.tokenType);
			}
		}
		
		//public InputBlock(long fileid, int startline, int endline, int numOriginalTokens, String language, String granularity, String tokenType, List<String> tokens) 
		
	// 3 - Build InputBlocks
		List<InputBlock> retval = new ArrayList<InputBlock>(blocks.size());
		for(TempBlock block : blocks) {
			if(block.tokens.size() == 0) // Skip 0-size blocks.
				continue;
			InputBlock iblock = new InputBlock(file.getId(), block.startline, block.endline, block.numOriginalTokens, this.language, this.granularity, this.tokenType, block.tokens);
			retval.add(iblock);
		}
		
		return retval;
	}
	
	private List<TempBlock> extractBlocks(InputFile file) {
		List<TempBlock> ret = new LinkedList<TempBlock>();
			
	// Execute and Capture Output
		
		
		List<String> lines = TXLUtil.run(commands, file.getPath());
		if(lines == null) {
			System.out.println("    Failed for file: " + file.getPath() + ".");
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
	
	private class TempBlock {
		int startline;
		int endline;
		int numOriginalTokens;
		List<String> tokens;
		
		TempBlock(int startline, int endline, List<String> tokens) {
			this.startline = startline;
			this.endline = endline;
			this.tokens = tokens;
			this.numOriginalTokens = tokens.size();
		}
	}
	
}
