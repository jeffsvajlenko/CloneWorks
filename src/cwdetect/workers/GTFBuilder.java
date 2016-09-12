package cwdetect.workers;

import cwbuild.block.InputBlock;
import cwbuild.block.TermFrequency;
import cwdetect.DF.IDocumentFrequency;
import cwdetect.GTF.IGlobalTermFrequency;
import cwdetect.workers.helpers.InputBlockInput;
import util.blockingqueue.IEmitter;

public class GTFBuilder extends Thread {

	private InputBlockInput input;
	private IEmitter<InputBlock> output;
	private IGlobalTermFrequency gtf;
	private IDocumentFrequency df;
	
	private Integer exitStatus;
	private String exitMessage;
	
	public GTFBuilder(InputBlockInput input, IEmitter<InputBlock> output, IGlobalTermFrequency gtf, IDocumentFrequency df) {
		this.input = input;
		this.output = output;
		this.gtf = gtf;
		this.df = df;
	}
	
	@Override
	public void run() {
		int num = 0;
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
			
			num++;
			
			// Update GTF and DF
			for(TermFrequency tf : block.getTokens()) {
				gtf.add(tf.getTerm(), tf.getFrequency());
			}
			
			if(df != null)
				df.add(block);
			
			// Put input block (if desired)
			put(block);
		}
		
		flush();
		
		exitStatus = 0;
		exitMessage = "Success. " + num + " blocks processed.";
	}
	
	private void put(InputBlock block) {
		if(output != null)
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
	
	private void flush() {
		if(output != null)
			while(true) {
				try {
					output.flush();
					break;
				} catch (InterruptedException e) {
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
