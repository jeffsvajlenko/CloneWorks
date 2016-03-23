package input.worker;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import input.utils.FilePathStream;

/**
 * 
 * Does not close input stream.
 * 
 * Exit: 0 = Success
 *      -1 = IOException 
 * 
 */
public class FileProducer extends Thread {
	
	FilePathStream in;
	private int maxGroupSize;
	private BlockingQueue<List<Path>> output;
	private Integer exitStatus;
	private String exitMessage;
	
	/**
	 * 
	 * @param language The programming language.
	 * @param files The concurrent queue to add the files to.
	 * @param root The directory to search files in.
	 * @param maxGroupSize File group sizes.
	 */
	public FileProducer(FilePathStream in, BlockingQueue<List<Path>> files, int maxGroupSize) {
		this.in= in;
		this.output = files;
		this.maxGroupSize = maxGroupSize;
		this.exitStatus = null;
		this.exitMessage = null;
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
	
	@Override
	public void run() {
		try {
			// Fill buffer, output buffer when full
			Path path;
			ArrayList<Path> buffer = new ArrayList<Path>(maxGroupSize);
			while((path = in.next()) != null) {
				buffer.add(path);
				if(buffer.size() == maxGroupSize) {
					put(buffer);
					buffer.clear();
				}
			}
			
			// Output final (potentially partial) buffer
			if(buffer.size() > 0)
				put(buffer);
			
			// Exit Status
			exitStatus = 0;
			exitMessage = "Success.";
		} catch (Exception e) {
			exitStatus = -1;
			exitMessage = "Failed with exception: " + e.getMessage() + ".";
		}
	}
	
	private void put(List<Path> flist) {
		boolean succeed = false;
		
		// Create new array of the files
		ArrayList<Path> out = new ArrayList<Path>(flist.size());
		out.addAll(flist);
		
		// Add to concurrent queue, re-try if interrupted while waiting.
		do {
			try {
				output.put(out);
				succeed = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(!succeed);
	}
}
