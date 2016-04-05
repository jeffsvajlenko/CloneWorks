package detection.workers;

import java.util.Comparator;
import java.util.Objects;

import detection.block.Block;
import detection.index.IIndex;
import detection.prefixer.Prefixer;
import detection.requirements.Requirements;
import detection.workers.helpers.InputBlockInput;
import input.block.InputBlock;
import input.block.TermFrequency;
import util.blockingqueue.IEmitter;

public class BlockIndexer extends Thread {

	private InputBlockInput input;
	private IEmitter<Block> output;
	private IIndex index;
	private Prefixer prefixer;
	private Comparator<TermFrequency> sorter;
	private Requirements requirements;
	
	private int exitvalue;
	private String exitmsg;
	
	public BlockIndexer(InputBlockInput input, IEmitter<Block> output, IIndex index, Comparator<TermFrequency> sorter, Prefixer prefixer, Requirements requirements) {
		Objects.requireNonNull(input);
		Objects.requireNonNull(index);
		Objects.requireNonNull(prefixer);
		Objects.requireNonNull(requirements);
		
		this.input = input;
		this.output = output;
		
		this.index = index;
		this.sorter = sorter;
		this.prefixer = prefixer;
		this.requirements = requirements;
	}
	
	@Override
	public void run() {
		int num = 0;
		while(true) {
			
			// Get an Input Block
			InputBlock iblock = input.take();
			
			// Check Poison -- If poison, quit.
			if(input.isPoisoned())
				break;
			
			// Check Requirements  -- If fail, skip to the next block.
			if(!requirements.approve(iblock))
				continue;
			
			num++;
			
			// Sort
			if(sorter != null)
				iblock.sort(sorter);
			
			// Convert to Detection Block
			Block dblock = prefixer.prefix(iblock);
			
			// Index
			index.put(dblock);
			
			// Output
			output(dblock);
		}
		
		// Flush Output
		flush_output();
		
		exitvalue = 0;
		exitmsg = "Success, " + num + " blocks indexed.";
		
	}
	
	public int getExitValue() {
		return this.exitvalue;
	}
	
	public String getExitMessage() {
		return this.exitmsg;
	}
	
	private void output(Block dblock) {
		if(output != null) {
			while(true) {
				try {
					output.put(dblock);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void flush_output() {
		if(output != null) {
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

}
