package detection.workers;

import detection.GTF.GTFHashMap;
import detection.workers.helpers.InputBlockInput;
import input.block.InputBlock;
import input.block.TermFrequency;
import util.blockingqueue.IEmitter;

public class GTFBuilder extends Thread {

	private InputBlockInput input;
	private IEmitter<InputBlock> output;
	private GTFHashMap gtf;
	
	private Integer exitStatus;
	private String exitMessage;
	
	public GTFBuilder(InputBlockInput input, IEmitter<InputBlock> output, GTFHashMap gtf) {
		this.input = input;
		this.output = output;
		this.gtf = gtf;
	}
	
	@Override
	public void run() {
		while(true) {
			
			if(Thread.currentThread().isInterrupted()) {
				exitStatus = -1;
				exitMessage = "Was interrupted.";
				return;
			}
			
			// Get next input block
			InputBlock block = input.take();
			
			// Check for poison
			if(input.isPoisoned())
				break;
			
			// Update GTF
			for(TermFrequency tf : block.getTokens()) {
				gtf.add(tf.getTerm(), tf.getFrequency());
			}
			
			// Put input block (if desired
			if(output != null)
				put(block);
		}
		
		exitStatus = 0;
		exitMessage = "Success.";
	}
	
	private void put(InputBlock block) {
		while(true) {
			try {
				output.put(block);
				return;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}
	
	public int getExitStatus() {
		return this.exitStatus;
	}
	
	public String getExitMessage() {
		return this.exitMessage;
	}

}
