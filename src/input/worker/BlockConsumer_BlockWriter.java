package input.worker;

import java.io.Writer;

import input.block.InputBlock;
import util.blockingqueue.IReceiver;

public class BlockConsumer_BlockWriter extends Thread {
	
	private Writer output;
	private IReceiver<InputBlock> blocks_in;
	
	private Integer exitStatus;
	private String exitMessage;
	
	public BlockConsumer_BlockWriter(Writer output, IReceiver<InputBlock> receiver) {
		super();
		this.output = output;
		this.blocks_in = receiver;
	}
	
	
	@Override
	public void run() {
		InputBlock buffer;
		
		while(true) {
			// Read next buffer
			try {
				buffer = blocks_in.take();
			} catch (InterruptedException e) {
				continue; // If interrupted, ignore and retry.
			}
			
			//Check for poison (end-condition)
			if(blocks_in.isPoisoned()) {
				break;
			}
			
			// Write block, handle error
			try {
				output.write(InputBlock.getInputBlockString(buffer));
				output.write(">\n");
			} catch (Exception e) {
				exitStatus = -1;
				exitMessage = "Failed with exception: " + e.getMessage() + ".";
				break;
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
