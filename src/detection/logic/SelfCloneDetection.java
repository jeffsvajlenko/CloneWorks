package detection.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import detection.GTF.GTFHashMap;
import detection.GTF.GTFTermFreqComparator;
import detection.block.Block;
import detection.detection.Clone;
import detection.detection.HeuristicCloneDetector;
import detection.index.ConcurrentHashMapIndex;
import detection.index.IIndex;
import detection.prefixer.MyPrefixer;
import detection.requirements.SizeRequirements;
import detection.util.BlockFileReader;
import detection.workers.BlockIndexer;
import detection.workers.CloneDetection;
import detection.workers.CloneWriter;
import detection.workers.GTFBuilder;
import detection.workers.helpers.ObjectBlockInput;
import detection.workers.helpers.ObjectInputBlockInput;
import detection.workers.helpers.StringInputBlockInput;
import input.block.InputBlock;
import util.blockingqueue.IEmitter;
import util.blockingqueue.IQueue;
import util.blockingqueue.QueueBuilder;

public class SelfCloneDetection {

	public static void main(String args[]) throws IOException {
		Path input = Paths.get(args[0]);
		Path output = Paths.get(args[1]);
		
		int numThreads = Integer.parseInt(args[2]);
		
		int minLines = Integer.parseInt(args[3]);
		int maxLines = Integer.parseInt(args[4]);
		int minTokens = Integer.parseInt(args[5]);
		int maxTokens = Integer.parseInt(args[6]);
		double sim = Double.parseDouble(args[7]);
		
		SelfCloneDetection.detect(input, output, numThreads, minLines, maxLines, minTokens, maxTokens, sim);
		
	}
	
	public static void detect(Path input, Path output, int numThreads, int minLines, int maxLines, int minTokens, int maxTokens, double sim) throws IOException {
// -- Data
		// Queues
		IQueue<String>     Q_input_gtf         = QueueBuilder.<String>groupQueue_arrayBacked(50, 10);
		IQueue<InputBlock> Q_gtf_indexer       = QueueBuilder.groupQueue_linkedListBacked(10);
		IQueue<Block>      Q_indexer_detection = QueueBuilder.groupQueue_linkedListBacked(10);
		IQueue<Clone>      Q_detection_output  = QueueBuilder.groupQueue_arrayBacked(50, 10);
		
// -- Workers
		
		// Structures
		GTFHashMap gtf = new GTFHashMap(1000);
		IIndex index = new ConcurrentHashMapIndex(1000);
		
		// GTFBuilders
		GTFBuilder [] W_gtfbuilder = new GTFBuilder[numThreads];
		for(int i = 0; i < numThreads; i++)
			W_gtfbuilder[i] = new GTFBuilder(new StringInputBlockInput(Q_input_gtf.getReceiver()),
					                         Q_gtf_indexer.getEmitter(),
					                         gtf);
				
		// Indexers
		BlockIndexer [] W_indexers = new BlockIndexer[numThreads];
		for(int i = 0; i < numThreads; i++)
			W_indexers[i] = new BlockIndexer(new ObjectInputBlockInput(Q_gtf_indexer.getReceiver()),
					                      index,
					                      new GTFTermFreqComparator(gtf),
					                      new MyPrefixer(sim),
					                      new SizeRequirements(minLines /*minLines*/, maxLines /*maxLines*/, minTokens /*minTokens*/, maxTokens /*maxTokens*/));
		
		// Detectors
		CloneDetection [] W_detection = new CloneDetection[numThreads];
		for(int i = 0; i < numThreads; i++)
			W_detection[i] = new CloneDetection(new ObjectBlockInput(Q_indexer_detection.getReceiver()),
												Q_detection_output.getEmitter(),
					                            index,
					                            new HeuristicCloneDetector(sim));
					                            
		
		// Outputer
		BufferedWriter out = new BufferedWriter(new FileWriter(output.toFile()));
		CloneWriter W_output = new CloneWriter(Q_detection_output.getReceiver(),
				                               out);
		
// -- Orchestrate
		
	// -- Built GTF
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
					System.out.println("GTF Builder [" + i + "] has completed with exit: " + W_gtfbuilder[i].getExitStatus() + " - " + W_gtfbuilder[i].getExitMessage());
					W_gtfbuilder[i] = null;
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	// -- Index
		// Start Indexer
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
			System.out.println("Indexer [" + i + "] has completed with exit: ");
			W_indexers[i] = null;
		}
		
	// - Detection
		
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
			System.out.println("Detector [" + i + "] has completed with exit: ");
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
		System.out.println("Outputter has completed with exit: ");
		W_output = null;
		out.flush();
		
		
	}
	
}