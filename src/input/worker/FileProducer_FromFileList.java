package input.worker;
import java.nio.file.Path;

import input.file.InputFile;
import input.utils.FileIDWriter;
import input.utils.FilePathStream;
import util.blockingqueue.IEmitter;

/**
 * 
 * Does not close input stream.
 * 
 * Exit: 0 = Success
 *      -1 = IOException 
 * 
 */
public class FileProducer_FromFileList extends Thread {
	
	FilePathStream in;
	private IEmitter<InputFile> output;
	private Integer exitStatus;
	private String exitMessage;
	private long currentid;
	private FileIDWriter writer;
	
	/**
	 * 
	 * @param language The programming language.
	 * @param files The concurrent queue to add the files to.
	 * @param root The directory to search files in.
	 * @param maxGroupSize File group sizes.
	 */
	public FileProducer_FromFileList(FilePathStream in, IEmitter<InputFile> files, FileIDWriter writer) {
		this.in= in;
		this.output = files;
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
			
			while((path = in.next()) != null) {
				InputFile ifile = new InputFile(currentid++, path);
				
				// Output to file tracker (optionally)
				if(writer != null)
					writer.write(ifile.getId(), ifile.getPath());
				
				// Emit, in case of interruption re-try until succeeds
				boolean success = false;
				do {
					try {
						output.put(ifile);
						success = true;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while(!success);
			}
			
			// Flush Emitter
			output.flush();
			
			// Exit Status
			exitStatus = 0;
			exitMessage = "Success.";
		} catch (Exception e) {
			e.printStackTrace();
			exitStatus = -1;
			exitMessage = "Failed with exception: " + e.getMessage() + ".";
		}
	}
}
