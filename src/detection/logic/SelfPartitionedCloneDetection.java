package detection.logic;

import java.io.IOException;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import detection.DF.DFHashMap;
import detection.DF.IDocumentFrequency;
import detection.GTF.GTFHashMap;
import detection.GTF.GTFTermFreqComparator;
import detection.GTF.IGlobalTermFrequency;
import detection.block.Block;
import detection.detection.Clone;
import detection.detection.HeuristicCloneDetector;
import detection.index.ConcurrentHashMapIndex;
import detection.index.IIndex;
import detection.prefixer.MyPrefixer;
import detection.prefixer.Prefixer;
import detection.requirements.Requirements;
import detection.requirements.SizeRequirements;
import detection.util.BlockFileReader;
import detection.util.CloneDetectionConfig;
import detection.util.CloneFileWriter;
import detection.workers.BlockIndexer;
import detection.workers.CloneDetection;
import detection.workers.CloneWriter;
import detection.workers.GTFBuilder;
import detection.workers.helpers.MultiSourceBlockInput;
import detection.workers.helpers.ObjectBlockInput;
import detection.workers.helpers.ObjectInputBlockInput;
import detection.workers.helpers.StringBlockInput;
import detection.workers.helpers.StringInputBlockInput;
import input.block.InputBlock;
import util.blockingqueue.IEmitter;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class SelfPartitionedCloneDetection {
	
	public static void detect(CloneDetectionConfig config) throws IOException {
		//	  Path input,
		//	  Path output,
		//	  Requirements requirements,
		//	  double sim,
		//	  int blockgroupsize,
		//	  int numThreads
		//	 ) throws IOException {
		
		Path input = config.getBlocks();
		Path output = config.getClones();
		Requirements requirements = new SizeRequirements(config.getMinLines(), config.getMaxLines(), config.getMinTokens(), config.getMaxTokens());
		int blockgroupsize = config.getMaxPartitionSize();
		double sim = config.getMinSimilarity();
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
			IGlobalTermFrequency gtf = new GTFHashMap(10000);
			IDocumentFrequency df = new DFHashMap(10000);
			IIndex index = new ConcurrentHashMapIndex(1000);
			GTFTermFreqComparator sorter = new GTFTermFreqComparator(gtf);
			
			//Queues
			IQueue<String> Q_input_gtf = QueueBuilder.<String>groupQueue_arrayBacked(capacity, maxGroupSize);
			IQueue<InputBlock> Q_gtf_indexer = QueueBuilder.groupQueue_linkedListBacked(maxGroupSize);
			IQueue<Block> Q_indexer_out = QueueBuilder.groupQueue_linkedListBacked(maxGroupSize);
			IQueue<String> Q_detection_in = QueueBuilder.groupQueue_arrayBacked(capacity, maxGroupSize);
// Workers
			// GTF Builders
			GTFBuilder [] W_gtfbuilder = new GTFBuilder[numThreads];
			for(int i = 0; i < numThreads; i++)
				W_gtfbuilder[i] = new GTFBuilder(new StringInputBlockInput(Q_input_gtf.getReceiver()),
						                         Q_gtf_indexer.getEmitter(),
						                         gtf, df);
			// Indexers
			BlockIndexer [] W_indexers = new BlockIndexer[numThreads];
			for(int i = 0; i < numThreads; i++)
				W_indexers[i] = new BlockIndexer(new ObjectInputBlockInput(Q_gtf_indexer.getReceiver()),
												 Q_indexer_out.getEmitter(),
						                      	 index,
						                         sorter,
						                         df,
						                         prefixer,
						                         requirements);
			
			// Detectors
			CloneDetection [] W_detection = new CloneDetection[numThreads];
			for(int i = 0; i < numThreads; i++) {
				ObjectBlockInput blockFromIndexer = new ObjectBlockInput(Q_indexer_out.getReceiver());
				StringBlockInput blockFromFile = new StringBlockInput(Q_detection_in.getReceiver(), sorter, prefixer, requirements);
				MultiSourceBlockInput detectionInput = new MultiSourceBlockInput(blockFromIndexer, blockFromFile);
				W_detection[i] = new CloneDetection(detectionInput,
													Q_output.getEmitter(),
						                            index,
						                            new HeuristicCloneDetector(sim));
			}
			
// Orchestrate
			// Build GTF
			//System.out.println("\tBuilding GTF....");
			for(int i = 0; i < numThreads; i++)
				W_gtfbuilder[i].start();
			
			// Feed
			IEmitter<String> sblock_emitter = Q_input_gtf.getEmitter();
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
			while(true) {try {sblock_emitter.flush(); break;} catch (InterruptedException e) {e.printStackTrace();}}
			//System.out.println("\t\t" + (blockPosition-originalBlockPosition) + " blocks fed.");
			
			// Poison GTF
			while(true) {try {Q_input_gtf.poisonReceivers(); break;} catch (InterruptedException e) { e.printStackTrace();}}
			
			// Wait for GTF to complete
			for(int i = 0; i < numThreads; i++) {
				while(true) {
					try {
						W_gtfbuilder[i].join();
						//System.out.println("\t\tGTF Builder [" + i + "] has completed with exit: " + W_gtfbuilder[i].getExitStatus() + " - " + W_gtfbuilder[i].getExitMessage());
						W_gtfbuilder[i] = null;
						break;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			//System.out.println("\t" + gtf.getKeySet().size() + " terms tracked.\n");
			
	// Index
			//System.out.println("\tIndexing... " + Q_gtf_indexer.size() + " blocks to process.");
			for(int i = 0; i < numThreads; i++)
				W_indexers[i].start();
			
			// Poison (unbounded queue already full)
			while(true) {
				try {
					Q_gtf_indexer.poisonReceivers();
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
