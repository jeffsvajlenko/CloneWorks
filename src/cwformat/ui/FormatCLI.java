package cwformat.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import constants.InstallDir;
import cwformat.formatter.Formatter;
import cwformat.logic.Format;

public class FormatCLI {

	private static Options options;
	private static HelpFormatter formatter;
	
	private static void panic(String str) {
		if(str != null) {
			System.out.println(str);
			System.out.println();
		}
		formatter.printHelp(200, "cwformat", "CloneWorks-Format - Clone output formatter.", options, "", true);
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
		
		options.addOption(Option.builder("t")
				                .longOpt("formater")
				                .required()
				                .hasArg()
				                .argName("formater")
				                .desc("The formatter to use.")
				                .build()
		);
		
		options.addOption(Option.builder("v")
								.longOpt("formatter options")
								.hasArg()
								.argName("options")
								.desc("A string containing the options for this formatter.")
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
		
		InstallDir.setInstallDir(Paths.get(line.getArgList().get(0)));
		
		Path clones;
		Path fileids;
		Path output;
		Formatter formatter;
		String formatter_options;
		
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
		
		if(line.hasOption("v")) {
			formatter_options = line.getOptionValue("v");
		} else {
			formatter_options = "";
		}
		
		
		String tname = line.getOptionValue("t");
		Class<?> clazz;
		try {
			clazz = Class.forName("cwformat.formatter." + tname);
			Constructor<?> ctor = clazz.getConstructor(String.class);
			formatter = (Formatter) ctor.newInstance(new Object[]{formatter_options});
		} catch (ClassNotFoundException e1) {
			panic("No such formatter.");
			return;
		} catch (InstantiationException e) {
			panic("Formatter could not be initialized.");
			return;
		} catch (IllegalAccessException e) {
			panic("IllegalAccessException on formatter.");
			return;
		} catch (IllegalArgumentException e) {
			panic("IllegalArgumentException on formatter.");
			return;
		} catch (InvocationTargetException e) {
			panic("Invocation target exception on formatter.");
			return;
		} catch (NoSuchMethodException e) {
			panic("Formatter is missing the required constructor.");
			return;
		} catch (SecurityException e) {
			panic("Security exception initializing the formatter.");
			return;
		}
		
		// Echo Config
		System.out.println();
		System.out.println("ThriftyClone Version 1.0");
		System.out.println();
		System.out.println("Clone Converter");
		System.out.println();
		System.out.println("FileIDs=" + fileids.toAbsolutePath());
		System.out.println("Clones=" + clones.toAbsolutePath());
		System.out.println("Converted=" + output.toAbsolutePath());
		System.out.println("Formatter=" + "cwformat.formatter." + tname);
		System.out.println("FormatterOptions=" + formatter_options);
		
		try {
			Format.format(fileids, clones, output, formatter);
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return;
		}
		
	}
	
}
