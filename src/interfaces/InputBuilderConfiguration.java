package interfaces;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import constants.BlockGranularityConstants;
import constants.InstallDir;
import constants.LanguageConstants;
import constants.TokenGranularityConstants;
import input.tokenprocessors.ITokenProcessor;
import input.txl.ITXLCommand;
import input.txl.TXLNormalization;

public class InputBuilderConfiguration {
	
	private int numthreads;
	private Path pconfigfile;
	private Path system;
	private Path fileids;
	private Path blocks;
	private int [] languages;
	private int granularity;
	private int token_type;
	private List<ITXLCommand> txl_commands;
	private List<ITokenProcessor> token_processors;
	private int minlines;
	private int maxlines;
	private int mintokens;
	private int maxtokens;
	
	public InputBuilderConfiguration(	String system,
										String fileids,
										String blocks,
										String[] language,
										String granularity,
										String configfile,
										String numthreads,
										String minlines,
										String maxlines,
										String mintokens,
										String maxtokens
	) throws ConfigurationException {
		
	// Process Provided Configurations
		this.system      = processSystemConfiguration(system);
		this.fileids     = processFileIdsConfiguration(fileids);
		this.blocks      = processBlocksConfiguration(blocks);
		this.languages    = processLanguage(language);
		this.granularity = processGranularity(granularity);
		
		this.minlines = processMinLines(minlines);
		this.maxlines = processMaxLines(maxlines);
		this.mintokens = processMinTokens(mintokens);
		this.maxtokens = processMaxTokens(maxtokens);
		
		if(this.minlines > this.maxlines)
			throw new ConfigurationException("Minlines can't be larger than maxlines.");
		if(this.mintokens > this.maxtokens)
			throw new ConfigurationException("Mintokens can't be larger than maxtokens.");
		
		this.numthreads  = processNumThreads(numthreads);
		this.pconfigfile = processConfigFile(configfile);
		
	// Process Configuration File
		txl_commands = new LinkedList<ITXLCommand>();
		token_processors = new LinkedList<ITokenProcessor>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(pconfigfile.toFile()));
			String line;
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(line.startsWith(" ") || line.startsWith("#") || line.equals(""))
					continue;
				String parts[] = line.split("=", 2);
				String declaration = parts[0].toLowerCase().trim();
				String value = parts[1].trim();
				switch(declaration) {
					case "tokenization":
						this.token_type = processTokenType(value);
						break;
					case "txl":
						txl_commands.add(processTxlCommand(value, this.languages, this.granularity));
						break;
					case "tokproc":
						token_processors.add(processTokenProcessor(value));
						break;
					default:
						throw new ConfigurationException("Unknown configuration option: " + line);
				}
			}
			br.close();
		} catch (IOException e) {
			throw new ConfigurationException("The specified configuration file is not readable.  IOException occured: " + e.getMessage());
		}
	}
	
	private static int processMinLines(String minlines) throws ConfigurationException {
		if(minlines == null) {
			return 0;
		} else {
			try {
				return Integer.parseInt(minlines);
			} catch (Exception e) {
				throw new ConfigurationException("The specified minlines is invalid: " + minlines + ".");
			}
		}
	}
	
	private static int processMinTokens(String mintokens) throws ConfigurationException {
		if(mintokens == null) {
			return 0;
		} else {
			try {
				return Integer.parseInt(mintokens);
			} catch (Exception e) {
				throw new ConfigurationException("The specified mintokens is invalid: " + mintokens + ".");
			}
		}
	}
	
	private static int processMaxLines(String maxlines) throws ConfigurationException {
		if(maxlines == null) {
			return Integer.MAX_VALUE;
		} else {
			try {
				return Integer.parseInt(maxlines);
			} catch (Exception e) {
				throw new ConfigurationException("The specified maxlines is invalid: " + maxlines + ".");
			}
		}
	}
	
	private static int processMaxTokens(String maxtokens) throws ConfigurationException {
		if(maxtokens == null) {
			return Integer.MAX_VALUE;
		} else {
			try {
				return Integer.parseInt(maxtokens);
			} catch (Exception e) {
				throw new ConfigurationException("The specified maxlines is invalid: " + maxtokens + ".");
			}
		}
	}
	
	private static Path processConfigFile(String configfile) throws ConfigurationException {
		Path retval;
		try {
			retval = InstallDir.getInstallDir().resolve("config/" + configfile);
		} catch (InvalidPathException e) {
			throw new ConfigurationException("The specified configuration file is not a valid path: config/" + configfile);
		}
		if(!Files.exists(retval)) {
			throw new ConfigurationException("The specified configuration file does not exist: config/" + configfile);
		} else if(!Files.isRegularFile(retval)) {
			throw new ConfigurationException("The specified configuration file is not a regular file: config/" + configfile);
		}
		return retval;
	}

	private static int processNumThreads(String numthreads) throws ConfigurationException {
		int retval;
		if(numthreads == null) {
			retval = Runtime.getRuntime().availableProcessors();
		} else {
			try {
				retval = Integer.parseInt(numthreads);
			} catch(NumberFormatException e) {
				throw new ConfigurationException("Invalid parameter for number of threads: " + numthreads);
			}
		}
		return retval;
	}

	public Path getConfigFile() {
		return this.pconfigfile;
	}
	
	public int getNumThreads() {
		return this.numthreads;
	}
	
	public Path getSystem() {
		return system;
	}

	public Path getFileids() {
		return fileids;
	}

	public Path getBlocks() {
		return blocks;
	}

	public int [] getLanguages() {
		return languages;
	}

	public int getBlock_granularity() {
		return granularity;
	}

	public int getTokenType() {
		return token_type;
	}
	
	public int getMinLines() {
		return minlines;
	}
	
	public int getMaxLines() {
		return maxlines;
	}
	
	public int getMinTokens() {
		return mintokens;
	}
	
	public int getMaxTokens() {
		return maxtokens;
	}

	public List<ITXLCommand> getTxl_commands() {
		return txl_commands;
	}

	public List<ITokenProcessor> getToken_processors() {
		return token_processors;
	}
	
	public static ITokenProcessor processTokenProcessor(String value) throws ConfigurationException {
		ITokenProcessor processor;
		
		String parts[] = value.split(" ", 2);
		String sclass = parts[0];
		String init;
		if(parts.length == 1) {
			init = "";
		} else if (parts.length == 2) {
			init = parts[1];
		} else {
			System.err.println("Critical error in InputBuilderConfig.");
			System.exit(-1);
			return null;
		}
		
		try {
			Class<?> clazz = Class.forName("input.tokenprocessors." + sclass);
			Constructor<?> ctor = clazz.getConstructor(String.class);
			processor = (ITokenProcessor) ctor.newInstance(new Object[]{init});
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Could not find token processor class " + sclass + ".  Exception: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException("Token processor " + sclass + " is missing a string-based init constructor.  Exception: " + e.getMessage());
		} catch (SecurityException e) {
			throw new ConfigurationException("Security prevented reflection of token processor " + sclass + ".  Exception: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new ConfigurationException("Could not create instance of token processor " + sclass + " with parameters '" + init + "'.  Exception: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ConfigurationException("Token processor class " + sclass + " with parameters '" + init + "' is not accessible.  Is it declared public?  Exception: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new ConfigurationException("Token processor class " + sclass + " with parameters '" + init + "' constructor failed with exception " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ConfigurationException("Token processor class " + sclass + " with parameters '" + init + " 'threw exception: " + e.getMessage());
		}
		return processor;
	}
	
	public static TXLNormalization processTxlCommand(String command, int [] langauges, int block_granularity) throws ConfigurationException {
		String parts[] = command.split(" ", 2);
		String script = parts[0];
		String arguments;
		if(parts.length == 1) {
			arguments = "";
		} else if (parts.length == 2) {
			arguments = parts[1];
		} else {
			System.err.println("Coding error in InputBuilderConfiguration.");
			System.exit(-1);
			return null;
		}
		TXLNormalization txl = new TXLNormalization(script, arguments, block_granularity);
		for(int lang : langauges) {
			if(!txl.existsScript(lang) && !txl.existsExec(lang))
				throw new ConfigurationException("TXL script: '" + txl.toString() + "' does not exist as script or executable for language " + LanguageConstants.getString(lang) + ".");
		}
		
		return txl;
	}
	
	public static int processTokenType(String tokentype) throws ConfigurationException {
		int retval;
		try {
			retval = TokenGranularityConstants.getCanonized(tokentype);
		} catch (IllegalArgumentException e) {
			throw new ConfigurationException("Unsupported token type.");
		}
		return retval;
	}
	
	public static int processGranularity(String granularity) throws ConfigurationException {
		int retval;
		try {
			retval = BlockGranularityConstants.getCanonical(granularity);
		} catch (IllegalArgumentException e) {
			throw new ConfigurationException("Unsupported granularity.");
		}
		return retval;
	}
	
	public static int [] processLanguage(String [] language) throws ConfigurationException {
		int [] retval = new int[language.length];
		int i = 0;
		try {
			for(i = 0; i < language.length; i++) {
				retval[i] = LanguageConstants.getCanonical(language[i]);
			}
		} catch (IllegalArgumentException e) {
			throw new ConfigurationException("Unsupported language: " + retval[i] + ".");
		}
		return retval;
	}
	
	public static Path processFileIdsConfiguration(String fileids) throws ConfigurationException {
		Path p_fileids;
		try {
			p_fileids = Paths.get(fileids);
		} catch (InvalidPathException e) {
			throw new ConfigurationException("Illegal path specified for fileids: " + fileids);
		}
		if(Files.exists(p_fileids)) {
			if(!Files.isRegularFile(p_fileids)) {
				throw new ConfigurationException("Path for fileids indicates an existing directory.  Must be a new or existing regular file.");
			}
		}
		return p_fileids;
	}
	
	public static Path processBlocksConfiguration(String blocks) throws ConfigurationException {
		Path p_blocks;
		try {
			p_blocks = Paths.get(blocks);
		} catch (InvalidPathException e) {
			throw new ConfigurationException("Illegal path specified for blocks: " + blocks);
		}
		if(Files.exists(p_blocks)) {
			if(!Files.isRegularFile(p_blocks)) {
				throw new ConfigurationException("Path for blocks indicates an existing directory.  Must be a new or existing regular file.");
			}
		}
		return p_blocks;
	}
	
	public static Path processSystemConfiguration(String system) throws ConfigurationException {
		Path p_system;
		try {
			p_system = Paths.get(system);
		} catch (InvalidPathException e) {
			throw new ConfigurationException("Illegal path specified for system: " + system);
		}
		if(!Files.exists(p_system)) {
			throw new ConfigurationException("Path for system does not indicate an existing file or directory.");
		}
		return p_system;
	}
	
}
