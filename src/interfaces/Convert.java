package interfaces;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class Convert {

	private static Options options;
	private static HelpFormatter formatter;
	
	private static void panic(String str) {
		if(str != null) {
			System.out.println(str);
			System.out.println();
		}
		formatter.printHelp(200, "thriftyc", "ThriftyCloneConverter - ThriftyClone clone output converter.", options, "", true);
		System.exit(-1);
		return;
	}
	
	public static void main(String args[]) {
		options = new Options();
		
		options.addOption(Option.builder("f")
								.longOpt("fileids")
								.required()
								.hasArg()
								.argName("path")
								.desc("File containing the FileID<->Path mapping.")
								.build()
		);
		
		options.addOption(Option.builder("c")
								.longOpt("clones")
								.required()
								.hasArg()
								.argName("path")
								.desc("File containing the detected clones output.")
								.build()
		);
		
		options.addOption(Option.builder("o")
								.longOpt("output")
								.required()
								.hasArg()
								.argName("path")
								.desc("File to write the formatted clones to.")
								.build()
		);
		
		formatter = new HelpFormatter();
		formatter.setOptionComparator(null);	
		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch (Exception e) {
			panic(null);
			return;
		}
		
		Path clones;
		Path fileids;
		Path output;
		
		try {
			clones = Paths.get(line.getOptionValue("c"));
		} catch (Exception e) {
			panic("Invalid path for file ids.");
			return;
		}
		
		try {
			fileids = Paths.get(line.getOptionValue("f"));
		} catch (Exception e) {
			panic("Invalid path for clones.");
			return;
		}
		
		try {
			output = Paths.get(line.getOptionValue("o"));
		} catch (Exception e) {
			panic("Invalid path for output.");
			return;
		}
		
		try {
			bcb.Convert.convert(clones, fileids, output);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return;
		}
		
	}
	
}
