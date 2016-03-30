package detection.workers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import input.block.InputBlock;
import util.blockingqueue.IEmitter;

/**
 * 
 * BlockReader
 * 
 *  Input: Stream of serialized InputBlock objects.
 * Output: De-serialized InputBlock objects to a BlockingQueue.
 *
 * Side-Effects:
 *     - Closes the reader.
 *
 */
public class InputBlockReader implements Runnable {

	private IEmitter<InputBlock> output;
	private BufferedReader input;

	private Integer exitStatus;
	private String exitMessage;
	
	/**
	 * Convenience constructor.  Opens the input file using the default character set.
	 * @param input
	 * @param output
	 * @throws FileNotFoundException 
	 */
	public InputBlockReader(Path input, IEmitter<InputBlock> output) throws FileNotFoundException {
		this.input = new BufferedReader(new FileReader(input.toFile()));
		this.output = output;
	}
	
	public InputBlockReader(BufferedReader input, IEmitter<InputBlock> output) {
		this.input = input;
		this.output = output;
	}
	
	public int getExitStatus() {
		return this.exitStatus;
	}
	
	public String getExitMessage() {
		return this.exitMessage;
	}
	
	@Override
	public void run() {
		try {
			String line;
			String sblock = "";
			
			while((line = input.readLine()) != null) {
				
				// Reached end of a block
				if(!line.startsWith("\t") && !sblock.equals("")) {
					
					
					// Parse and Put
					parseAndPut(sblock);
					
					// Start saving new block
					sblock = line + "\n";
				} else {
					// Add to string
					sblock += line + "\n";
				}
			}
			
			// Final block
			if(!sblock.equals("")) {
				parseAndPut(sblock);
			}
			
			// Flush
			boolean success = false;
			do {
				try {
					output.flush();
					success = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while(!success);
			
		} catch (IOException e) {
			e.printStackTrace();
			exitStatus = -1;
			exitMessage = "Failure: " + e.getMessage();
			return;
		}
		
		exitStatus = 0;
		exitMessage = "Success.";
		return;
	}
	
	private void parseAndPut(String sblock) {
		// Parse Block
		InputBlock block = InputBlock.readDetectionBlock(sblock);
		
		// Put block
		boolean success = false;
		do {
			try {
				output.put(block);
				success = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!success);
	}
	
}
