package cwdetect.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import cwbuild.block.InputBlock;
import cwdetect.DF.DFHashMap;
import cwdetect.DF.IDocumentFrequency;
import cwdetect.GTF.GTFHashMap;
import cwdetect.GTF.GTFTermFreqComparator;
import cwdetect.GTF.IGlobalTermFrequency;
import cwdetect.block.Block;
import cwdetect.detection.Clone;
import cwdetect.detection.HeuristicCloneDetector;
import cwdetect.index.ConcurrentHashMapIndex;
import cwdetect.index.IIndex;
import cwdetect.prefixer.MyPrefixer;
import cwdetect.requirements.Requirements;
import cwdetect.requirements.SizeRequirements;
import cwdetect.util.BlockFileReader;
import cwdetect.util.CloneDetectionConfig;
import cwdetect.util.CloneFileWriter;
import cwdetect.workers.BlockIndexer;
import cwdetect.workers.CloneDetection;
import cwdetect.workers.CloneWriter;
import cwdetect.workers.GTFBuilder;
import cwdetect.workers.helpers.ObjectBlockInput;
import cwdetect.workers.helpers.ObjectInputBlockInput;
import cwdetect.workers.helpers.StringInputBlockInput;
import util.blockingqueue.IEmitter;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class SelfCloneDetection {
	
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
		IQueue<String>     Q_input_gtf         = QueueBuilder.<String>groupQueue_arrayBacked(50, 10);
		IQueue<InputBlock> Q_gtf_indexer       = QueueBuilder.groupQueue_linkedListBacked(10);
		IQueue<Block>      Q_indexer_detection = QueueBuilder.groupQueue_linkedListBacked(10);
		IQueue<Clone>      Q_detection_output  = QueueBuilder.groupQueue_arrayBacked(50, 10);
		
// -- Workers
		
		// Structures
		IGlobalTermFrequency gtf = new GTFHashMap(10000);
		IDocumentFrequency df;
		if(config.isDF())
			df = new DFHashMap(10000);
		else
			df = null;
			
		IIndex index = new ConcurrentHashMapIndex(10000);
		
		// GTFBuilders
		GTFBuilder [] W_gtfbuilder = new GTFBuilder[numThreads];
		for(int i = 0; i < numThreads; i++)
			W_gtfbuilder[i] = new GTFBuilder(new StringInputBlockInput(Q_input_gtf.getReceiver()),
					                         Q_gtf_indexer.getEmitter(),
					                         gtf, df);
				
		// Indexers
		BlockIndexer [] W_indexers = new BlockIndexer[numThreads];
		for(int i = 0; i < numThreads; i++)
			W_indexers[i] = new BlockIndexer(new ObjectInputBlockInput(Q_gtf_indexer.getReceiver()),
					                         Q_indexer_detection.getEmitter(),
					                      	 index,
					                         new GTFTermFreqComparator(gtf),
					                         df,
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
		
	// -- Built GTF
		System.out.println("Building GTF...");
		// Start
		for(int i = 0; i < numThreads;  i++)
			W_gtfbuilder[i].start();
		
		// Feed
		IEmitter<String> sblock_emitter = Q_input_gtf.getEmitter();
		BlockFileReader reader = new BlockFileReader(new BufferedReader(new FileReader(input.toFile())));
		String sblock;
		while((sblock = reader.nextInputBlockString()) != null) {
			while(true) {
				try {
					sblock_emitter.put(sblock);
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		while(true) {
			try {
				sblock_emitter.flush();
				break;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		reader.close();
		//System.out.println(num + " blocks fed.");
		
		// Poison
		while(true) {try {
			Q_input_gtf.poisonReceivers();
			break;
		} catch (InterruptedException e) {e.printStackTrace();}}
		
		// Wait-For GTF to complete
		for(int i = 0; i < numThreads; i++) {
			while(true) {
				try {
					W_gtfbuilder[i].join();
					//System.out.println("\tGTF Builder [" + i + "] has completed with exit: " + W_gtfbuilder[i].getExitStatus() + " - " + W_gtfbuilder[i].getExitMessage());
					W_gtfbuilder[i] = null;
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println(gtf.getKeySet().size() + " terms tracked.\n");
		
	// -- Index
		//System.out.println("Indexing... " + Q_gtf_indexer.size() + " blocks to process.");
		// Start Indexer
		System.out.println("Indexing...");
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
		
	// - Detection
		//System.out.println("Detecting clones... " + Q_indexer_detection.size() + " query blocks to process.");
		
		System.out.println("Detection...");
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