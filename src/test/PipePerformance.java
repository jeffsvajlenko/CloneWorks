package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PipePerformance {

	public static void main(String args[]) throws IOException, InterruptedException {
		for(int i = 0; i < 100; i++) {
			String command = "cat example/JHotDraw54b1/src/CH/ifa/draw/util/Bounds.java | ./txl/java-extract-functions.x stdin | ./txl/java-rename-blind-functions.x stdin";
			String commands[] = new String[3];
			commands[0] = "bash";
			commands[1] = "-c";
			commands[2] = command;
			
			long time = System.currentTimeMillis();
			Process p = Runtime.getRuntime().exec(commands);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while((line = in.readLine()) != null);
				//System.out.println(line);
			p.waitFor();
			p.destroy();
			
			
			time = System.currentTimeMillis() - time;
			System.out.println(time);
		}
	}
	
}
