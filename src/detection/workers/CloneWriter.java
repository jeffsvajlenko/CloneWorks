package detection.workers;

import java.io.IOException;

import detection.detection.Clone;
import detection.util.CloneFileWriter;
import util.blockingqueue.IReceiver;

public class CloneWriter extends Thread {

	private IReceiver<Clone> input;
	private CloneFileWriter output;
	
	private int exitstatus;
	private String exitmessage;
	
	public CloneWriter(IReceiver<Clone> input, CloneFileWriter output) {
		this.input = input;
		this.output = output;
		this.input = input;
	}
	
	@Override
	public void run() {
		while(true) {
			// Take Clone
			Clone clone = take();
			
			// Check for Poison
			if(input.isPoisoned())
				break;
			
			// Write Clone
			try {
				output.write(clone);
			} catch (IOException e) {
				e.printStackTrace();
				exitstatus = -1;
				exitmessage = "Failed to write a clone with exception: " + e.getMessage();
				return;
			}
		}
		try {
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			exitstatus = -1;
			exitmessage = "Failed to flush writer with exception: " + e.getMessage();
		}
		
		exitstatus = 0;
		exitmessage = "Success.";
	}
	
	private Clone take() {
		while(true) {
			try {
				return input.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getExitStatus() {
		return this.exitstatus;
	}
	
	public String getExitMessage() {
		return this.exitmessage;
	}

}
