package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.exec.StreamPumper;

public class SerialMemoryPerforamnce {

	public static void main(String args[]) throws IOException, InterruptedException {
		for(int i = 0; i<1; i++) {
		String c2 = "./txl/java-extract-functions.x";
		String c3 = "./txl/java-rename-blind-functions.x";

		long time = System.currentTimeMillis();
		
		ProcessBuilder pb2 = new ProcessBuilder(c2, "stdin");
		pb2.redirectInput(new File("example/JHotDraw54b1/src/CH/ifa/draw/util/Bounds.java"));
		Process p2 = pb2.start();
		
		ProcessBuilder pb3 = new ProcessBuilder(c3,"stdin");
		Process p3 = pb3.start();

		//StreamPumper p1_p2 = new StreamPumper(p1.getInputStream(),p2.getOutputStream(),true);
		StreamPumper p2_p3 = new StreamPumper(p2.getInputStream(),p3.getOutputStream(),true);
		//new Thread(p1_p2).start();
		new Thread(p2_p3).start();
		
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(p3.getInputStream()));
		while((line = br.readLine())!=null);
		
		//p1.waitFor();
		p2.waitFor();
		p3.waitFor();
		
		//p1.destroy();
		p2.destroy();
		p3.destroy();
		
		time = System.currentTimeMillis() - time;
		
		System.out.println(time);
		}
	}
	
}
