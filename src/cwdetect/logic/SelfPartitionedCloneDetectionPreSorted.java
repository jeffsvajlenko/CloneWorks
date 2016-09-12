package cwdetect.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import cwdetect.block.Block;
import cwdetect.detection.Clone;
import cwdetect.detection.HeuristicCloneDetector;
import cwdetect.index.ConcurrentHashMapIndex;
import cwdetect.index.IIndex;
import cwdetect.prefixer.MyPrefixer;
import cwdetect.prefixer.Prefixer;
import cwdetect.requirements.Requirements;
import cwdetect.requirements.SizeRequirements;
import cwdetect.util.BlockFileReader;
import cwdetect.util.CloneDetectionConfig;
import cwdetect.util.CloneFileWriter;
import cwdetect.workers.BlockIndexer;
import cwdetect.workers.CloneDetection;
import cwdetect.workers.CloneWriter;
import cwdetect.workers.helpers.MultiSourceBlockInput;
import cwdetect.workers.helpers.ObjectBlockInput;
import cwdetect.workers.helpers.StringBlockInput;
import cwdetect.workers.helpers.StringInputBlockInput;
import util.blockingqueue.IEmitter;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class SelfPartitionedCloneDetectionPreSorted {
	
	public static void detect(CloneDetectionConfig config) throws IOException {
		//	  Path input,
		//	  Path output,
		//	  Requirements requirements,
		//	  double sim,
		//	  int blockgroupsize,
		//	  int numThreads
		
		Path input = config.getBlocks();
		Path output = config.getClones();
		Requirements requirements = new SizeRequirements(config.getMinLines(), config.getMaxLines(), config.getMinTokens(), config.getMaxTokens());
		double sim = config.getMinSimilarity();
		int blockgroupsize = config.getMaxPartitionSize();
		int numThreads = config.getNumThreads();
		
		int capacity = 50;
		int maxGroupSize = 10;
		
		boolean endReached = false;
		long blockPosition = 0;
		BlockFileReader indexBlockReader = new BlockFileReader(new BufferedReader(new FileReader(input.toFile())));
		
		Prefixer prefixer = new MyPrefixer(sim);
		
// Outputter
		IQueue<Clone> Q_output = QueueBuilder.groupQueue_arrayBacked(capacity, maxGroupSize);
		CloneFileWriter out = new CloneFileWriter(new BufferedWriter(new FileWriter(output.toFile())), config);
		CloneWriter W_output = new CloneWriter(Q_output.getReceiver(), out);
		W_output.start();
		
		while(!endReached) {
			//System.out.println("Indexing " + blockPosition + " to " + (blockPosition + blockgroupsize));
			
// Data
			IIndex index = new ConcurrentHashMapIndex(1000);
			
			//Queues
			IQueue<String> Q_indexer_in = QueueBuilder.groupQueue_linkedListBacked(maxGroupSize);
			IQueue<Block> Q_indexer_out = QueueBuilder.groupQueue_linkedListBacked(maxGroupSize);
			IQueue<String> Q_detection_in = QueueBuilder.groupQueue_arrayBacked(capacity, maxGroupSize);
// Workers
			
			// Indexers
			BlockIndexer [] W_indexers = new BlockIndexer[numThreads];
			for(int i = 0; i < numThreads; i++)
				W_indexers[i] = new BlockIndexer(new StringInputBlockInput(Q_indexer_in.getReceiver()),
												 Q_indexer_out.getEmitter(),
						                      	 index,
						                         null,
						                         null,
						                         prefixer,
						                         requirements);
			
			// Detectors
			CloneDetection [] W_detection = new CloneDetection[numThreads];
			for(int i = 0; i < numThreads; i++) {
				ObjectBlockInput blockFromIndexer = new ObjectBlockInput(Q_indexer_out.getReceiver());
				StringBlockInput blockFromFile = new StringBlockInput(Q_detection_in.getReceiver(), null, prefixer, requirements);
				MultiSourceBlockInput detectionInput = new MultiSourceBlockInput(blockFromIndexer, blockFromFile);
				W_detection[i] = new CloneDetection(detectionInput,
													Q_output.getEmitter(),
						                            index,
						                            new HeuristicCloneDetector(sim));
			}
			
// Orchestrate
			
	// Index
			// Start Indexers
			for(int i = 0; i < numThreads; i++)
				W_indexers[i].start();
			
			// Feed Next set of blocks
			IEmitter<String> sblock_emitter = Q_indexer_in.getEmitter();
			String sblock;
			for(int i = 0; i < blockgroupsize; i++) {
				sblock = indexBlockReader.nextInputBlockString();
				if(sblock == null) {
					endReached = true;
					break;
				} else {
					blockPosition++;
					while(true) {
						try {
							sblock_emitter.put(sblock);
							break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			while(true) {try {sblock_emitter.flush(); break;} catch (InterruptedException e) {e.printStackTrace();}} // flush
			
			
			// Poison (unbounded queue already full)
			while(true) {
				try {
					Q_indexer_in.poisonReceivers();
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Wait for Indexing to Complete
			for(int i = 0; i < numThreads; i++) {
				while(true) {
					try {
						W_indexers[i].join();
						break;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("\tIndexer [" + i + "] has completed with exit: " + W_indexers[i].getExitValue() + " - " + W_indexers[i].getExitMessage());
				W_indexers[i] = null;
			}
			//System.out.println(index.getTerms().size() + " terms indexed.\n");
			
	// Detection
			//System.out.println("Detecting clones... ");
			
		// Start Detectors
			for(int i = 0; i < numThreads; i++)
				W_detection[i].start();
			
		// Poison from index
			while(true) {
				try {
					Q_indexer_out.poisonReceivers();
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		// Feed
			//Query Block Reader
			BlockFileReader queryBlockReader = new BlockFileReader(new BufferedReader(new FileReader(input.toFile())));
			// Catchup to After Indexed
			queryBlockReader.skipBlocks(blockPosition);
			IEmitter<String> putblock = Q_detection_in.getEmitter();
			String qBlock;
			while((qBlock = queryBlockReader.nextInputBlockString()) != null) {
				while(true) {
					try {
						putblock.put(qBlock);
						break;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			while(true) {
				try {
					putblock.flush();
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			queryBlockReader.close();
			
		// Poison
			while(true) {
				try {
					Q_detection_in.poisonReceivers();
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		// Wait for Detectors
			for(int i = 0; i < numThreads; i++) {
				while(true) {
					try {
						W_detection[i].join();
						break;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("\tDetector [" + i + "] has completed with exit: ");
				W_detection[i] = null;
			}
			
		}
		
	// Poison Outputter
		while(true) {
			try {
				Q_output.poisonReceivers();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	// Wait for Outputter
		while(true) {
			try {
				W_output.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("Outputter has completed with exit: " + W_output.getExitMessage() + " - " + W_output.getExitMessage());
		W_output = null;
		out.flush();
		out.close();
		
		System.exit(0);
	}
	
	
	
}
