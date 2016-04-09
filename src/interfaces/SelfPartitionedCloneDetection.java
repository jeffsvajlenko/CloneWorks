package interfaces;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import detection.requirements.Requirements;
import detection.requirements.SizeRequirements;

public class SelfPartitionedCloneDetection {

	public static void main(String args[]) throws IOException {
		Path input = Paths.get(args[0]);
		Path output = Paths.get(args[1]);
		
		int numThreads = Integer.parseInt(args[2]);
		
		int minLines = Integer.parseInt(args[3]);
		int maxLines = Integer.parseInt(args[4]);
		int minTokens = Integer.parseInt(args[5]);
		int maxTokens = Integer.parseInt(args[6]);
		double sim = Double.parseDouble(args[7]);
		
		Requirements requirements = new SizeRequirements(minLines /*minLines*/, maxLines /*maxLines*/, minTokens /*minTokens*/, maxTokens /*maxTokens*/);
		
		long time = System.currentTimeMillis();
		
		detection.logic.SelfPartitionedCloneDetection.detect(
									      /*Input Blocks*/ input,
									      /*Clone Output*/ output,
									 /*Block Requirement*/ requirements,
									/*Minimum Similarity*/ sim,
								          /*MaxBlocKSize*/ 500,
									 /*Number of Threads*/ numThreads
								);
		
		System.out.println("Detection time: " + 1.0*(System.currentTimeMillis()-time)/1000 + " seconds.");
		
	}
	
}
