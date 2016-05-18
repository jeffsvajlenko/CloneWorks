package detection.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import detection.block.Block;
import detection.detection.Clone;
import detection.detection.HeuristicCloneDetector;
import detection.index.ConcurrentHashMapIndex;
import detection.index.IIndex;
import detection.prefixer.MyPrefixer;
import detection.requirements.Requirements;
import detection.requirements.SizeRequirements;
import detection.util.BlockFileReader;
import detection.util.CloneDetectionConfig;
import detection.util.CloneFileWriter;
import detection.workers.BlockIndexer;
import detection.workers.CloneDetection;
import detection.workers.CloneWriter;
import detection.workers.helpers.ObjectBlockInput;
import detection.workers.helpers.StringInputBlockInput;
import util.blockingqueue.IEmitter;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class SelfCloneDetectionPreSorted {
	
	public static void detect(CloneDetectionConfig config) throws IOException {
		//					  Path input,
		//					  Path output,
		//					  Requirements requirements,
		//					  double sim,
		//					  int numThreads
		//					 ) throws IOException {
		
		Path input = config.getBlocks();
		Path output = config.getClones();
		Requirements requirements = new SizeRequirements(config.getMinLines(), config.getMaxLines(), config.getMinTokens(), config.getMaxTokens());
		double sim = config.getMinSimilarity();
		int numThreads = config.getNumThreads();
		
// -- Data
		
		// Queues
		IQueue<String> 	   Q_indexer           = QueueBuilder.groupQueue_arrayBacked(50, 10);
		IQueue<Block>      Q_indexer_detection = QueueBuilder.groupQueue_linkedListBacked(10);
		IQueue<Clone>      Q_detection_output  = QueueBuilder.groupQueue_arrayBacked(50, 10);
		
		// Structures
		IIndex index = new ConcurrentHashMapIndex(1000);
		
// -- Workers
		
		// Indexers
		BlockIndexer [] W_indexers = new BlockIndexer[numThreads];
		for(int i = 0; i < numThreads; i++)
			W_indexers[i] = new BlockIndexer(new StringInputBlockInput(Q_indexer.getReceiver()),
					                         Q_indexer_detection.getEmitter(),
					                      	 index,
					                         null,
					                         null,
					                         new MyPrefixer(sim),
					                         requirements);
		
		// Detectors
		CloneDetection [] W_detection = new CloneDetection[numThreads];
		for(int i = 0; i < numThreads; i++)
			W_detection[i] = new CloneDetection(new ObjectBlockInput(Q_indexer_detection.getReceiver()),
												Q_detection_output.getEmitter(),
					                            index,
					                            new HeuristicCloneDetector(sim));
					                            
		
		// Outputer
		CloneFileWriter out = new CloneFileWriter(new BufferedWriter(new FileWriter(output.toFile())), config);
		CloneWriter W_output = new CloneWriter(Q_detection_output.getReceiver(), out);
		
// -- Orchestrate
		
	// -- Index
		//System.out.println("Indexing... ");
		// Start Indexer
		for(int i = 0; i < numThreads; i++)
			W_indexers[i].start();
		
		// Feed Clones
		IEmitter<String> emitter = Q_indexer.getEmitter();
		BlockFileReader reader = new BlockFileReader(new BufferedReader(new FileReader(input.toFile())));
		String sblock;
		while((sblock = reader.nextInputBlockString()) != null) {
			while(true) {
				try {
					emitter.put(sblock);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		while(true) {
			try {
				emitter.flush();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		reader.close();
		
		// Poison
		while(true) {
			try {
				Q_indexer.poisonReceivers();
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
		
	// - Detection
		//System.out.println("Detecting clones... " + Q_indexer_detection.size() + " query blocks to process.");
		
		// Start Detectors
		for(int i = 0; i < numThreads; i++)
			W_detection[i].start();
		
		// Start Outputter
		W_output.start();
		
		// Poison (unbounded queue already full)
		while(true) {
			try {
				Q_indexer_detection.poisonReceivers();
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
			//System.out.println("Detector [" + i + "] has completed with exit: ");
			W_detection[i] = null;
		}
		
		// Poison
		while(true) {
			try {
				Q_detection_output.poisonReceivers();
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
		//System.out.println("Outputter has completed with exit: ");
		W_output = null;
		out.flush();
		out.close();
		
		//System.out.println("Total time: " + (System.currentTimeMillis() - time)/1000.0);
		
	}
	
}