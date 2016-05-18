package input.worker;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import constants.LanguageConstants;
import input.file.InputFile;
import input.utils.FileIDWriter;
import util.blockingqueue.IEmitter;

/**
 * 
 * Does not close input stream.
 * 
 * Exit: 0 = Success
 *      -1 = IOException 
 * 
 */
public class FileProducer_FromRoot extends Thread {
	
	private Path root;
	private FileFilter pattern;
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
	public FileProducer_FromRoot(Path root, FileFilter pattern, IEmitter<InputFile> files, FileIDWriter writer) {
		this.root = root;
		this.pattern = pattern;
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
			
			Files.walkFileTree(root, new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(pattern.accept(file.toFile())) {
						int language = LanguageConstants.getLanguage(file);
						InputFile ifile = new InputFile(currentid++, file, language);
						if(writer != null)
							writer.write(ifile.getId(),ifile.getPath());
						put(ifile);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
				
			});
			
			
			// Flush
			flush();
			
			// Exit Status
			exitStatus = 0;
			exitMessage = "Success.";
		} catch (Exception e) {
			e.printStackTrace();
			exitStatus = -1;
			exitMessage = "Failed with exception: " + e.getMessage() + ".";
		}
		
		
		
	}
	
	private void put(InputFile file) {
		while(true) {
			try {
				output.put(file);
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void flush() {
		while(true) {
			try {
				output.flush();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
