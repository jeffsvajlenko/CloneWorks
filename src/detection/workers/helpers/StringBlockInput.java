package detection.workers.helpers;

import java.util.Comparator;

import detection.block.Block;

import detection.prefixer.Prefixer;
import detection.requirements.Requirements;
import input.block.InputBlock;
import input.block.TermFrequency;
import util.blockingqueue.IReceiver;

public class StringBlockInput implements BlockInput {

	private IReceiver<String> input; 
	
	private Comparator<TermFrequency> sorter;
	private Prefixer prefixer;
	private Requirements requirements;
	
	public StringBlockInput(IReceiver<String> input, Comparator<TermFrequency> sorter, Prefixer prefixer, Requirements requirements) {
		this.input = input;
		this.sorter = sorter;
		this.prefixer = prefixer;
		this.requirements = requirements;
	}
	
	@Override
	public Block take() {
		while(true) {
			String sblock;
		
			// Get String
			while(true) {
				try {
					sblock = input.take();
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Check Poison
			if(sblock == null)
				return null;
			
			// Parse
			InputBlock iblock = InputBlock.readDetectionBlock(sblock);
			
			// Check Requirements
			if(!requirements.approve(iblock))
				continue;
			
			// Sort Block
			if(sorter != null)
				iblock.sort(sorter);
						
			// Prefix
			Block block = prefixer.prefix(iblock);
			
			// Return
			return block;
		}
		
	}

	@Override
	public boolean isPoisoned() {
		return input.isPoisoned();
	}

}
