package input.worker;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import input.file.InputFile;
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
	private BlockingQueue<List<InputFile>> output;
	private Integer exitStatus;
	private String exitMessage;
	private long currentid;
	private Writer writer;
	
	/**
	 * 
	 * @param language The programming language.
	 * @param files The concurrent queue to add the files to.
	 * @param root The directory to search files in.
	 * @param maxGroupSize File group sizes.
	 */
	public FileProducer(FilePathStream in, BlockingQueue<List<InputFile>> files, Writer writer, int maxGroupSize) {
		this.in= in;
		this.output = files;
		this.maxGroupSize = maxGroupSize;
		this.exitStatus = null;
		this.exitMessage = null;
		this.writer = writer;
		this.currentid = 0;
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
			InputFile ifile;
			ArrayList<InputFile> buffer = new ArrayList<InputFile>(maxGroupSize);
			while((path = in.next()) != null) {
				ifile = new InputFile(currentid++, path);
				
				// Output to file
				if(writer != null)
					writer.write(ifile.getId() + "\t" + ifile.getPath() + "\n");
				
				// Add to buffer, emit if buffer full
				buffer.add(ifile);
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
			e.printStackTrace();
			exitStatus = -1;
			exitMessage = "Failed with exception: " + e.getMessage() + ".";
		}
	}
	
	private void put(List<InputFile> flist) {
		boolean succeed = false;
		
		// Create new array of the files
		ArrayList<InputFile> out = new ArrayList<InputFile>(flist.size());
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
