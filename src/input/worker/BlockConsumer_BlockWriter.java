package input.worker;

import java.io.Writer;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import input.block.InputBlock;

public class BlockConsumer_BlockWriter extends Thread {
	
	private Writer output;
	private BlockingQueue<List<InputBlock>> blocks_in;
	
	private Integer exitStatus;
	private String exitMessage;
	
	public BlockConsumer_BlockWriter(Writer output, BlockingQueue<List<InputBlock>> blocks_in) {
		super();
		this.output = output;
		this.blocks_in = blocks_in;
	}
	
	
	@Override
	public void run() {
		List<InputBlock> buffer = null;
		
		while(true) {
			// Read next buffer
			try {
				buffer = blocks_in.take();
			} catch (InterruptedException e) {
				continue; // If interrupted, ignore and continue.
			}
			
			//Check for poison (end-condition)
			if(buffer.size() == 0) {
				break;
			}
			
			// Write blocks, handle errors
			for(InputBlock block : buffer) {
				try {
					output.write(InputBlock.getInputBlockString(block));
				} catch (Exception e) {
					exitStatus = -1;
					exitMessage = "Failed with exception: " + e.getMessage() + ".";
					break;
				}
			}
		}
		
		// If no errors
		if(exitStatus == null) {
			exitStatus = 0;
			exitMessage = "Success.";
		}
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
