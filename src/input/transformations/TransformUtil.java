package input.transformations;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import constants.InstallDir;
import util.StreamGobbler;

public class TransformUtil {

	public static Path getTransformationRoot() {
		return InstallDir.getInstallDir().resolve("transformations");
	}
	
	public static TransformChain createChain(List<ITransform> transformations, String language, String granularity) {
		String chain = "";
		ITransform transformation;
		Iterator<ITransform> iterator = transformations.iterator();
		
		while(iterator.hasNext()) {
			transformation = iterator.next();
			chain += TransformUtil.getCommand(transformation, language, granularity);
			if(iterator.hasNext())
				chain += " | ";
		}
		return new TransformChain(chain);
	}
	
	private static String getCommand(ITransform transformation, String language, String granularity) {
		String command = "";
		command += "\"" + TransformUtil.getTransformationRoot().resolve(transformation.getExec()).toString() + "\"";
		command += " \"r\"";
		command += " \"" + InstallDir.getInstallDir().toString() + "\"";
		command += " \"" + language + "\"";
		command += " \"" + granularity + "\"";
		command += " " + transformation.params();
		return command;
	}
	
	public static TransformValidation validate(ITransform transformation, String language, String granularity) {
		String errormsg = null;
		boolean valid = false;
		
		String exec = TransformUtil.getTransformationRoot().resolve(transformation.getExec()).toString();
		String mode = "v";
		String installdir = InstallDir.getInstallDir().toString();
		
		String command = "\""+ exec + "\" \"" + mode + "\" \"" + installdir + "\" \"" + language + "\" \"" + granularity + "\" " + transformation.params();
		
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
		Process p = null;
		int retval;
		try {
			p = pb.start();
			new StreamGobbler(p.getErrorStream()).start();
			List<String> output = IOUtils.readLines(new InputStreamReader(p.getInputStream()));
			if(output.size() > 0)
				errormsg = output.get(0);
			retval = p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			errormsg = "Validation failed with exception: " + e.getMessage();
			return new TransformValidation(valid,errormsg);
		} catch (InterruptedException e) {
			e.printStackTrace();
			errormsg = "Validation was interrupted.";
			return new TransformValidation(valid,errormsg);
		} finally {
			p.destroy();
		}
		
		if(retval == 0)
			valid = true;
		return new TransformValidation(valid, errormsg);
	}
	
	public static TransformOutput run(TransformChain chain, byte [] file) {
		
		int retval = 0;
		List<String> lines = null;
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", chain.get());
		Process p = null;
		
		try {
			p = pb.start();
			new StreamGobbler(p.getErrorStream()).start();
			
			// Write
			OutputStream out = p.getOutputStream();
			IOUtils.write(file, out);
			out.close();
			
			// Read
			lines = IOUtils.readLines(new InputStreamReader(p.getInputStream()));
			
			retval = p.waitFor();
			
		} catch (IOException e) {
			return new TransformOutput(false,null,e);
		} catch (InterruptedException e) {
			return new TransformOutput(false,null,e);
		} finally {
			p.destroy();
		}
		
		if(retval != 0) {
			return new TransformOutput(false,null,null);
		}
		
		
		return new TransformOutput(true,lines,null);
	}
	
//	public static void main(String args[]) throws FileNotFoundException, IOException {
//		Transform ifdef = new Transform("txl_script", "ifdef");
//		Transform extract = new Transform("txl_normalization", "extract");
//		Transform renameblind = new Transform("txl_normalization", "rename-blind");
//		Transform tokenize = new Transform("txl_normalization", "tokenize");
//		
//		List<Transform> transformations = new LinkedList<Transform>();
//		//transformations.add(ifdef);
//		transformations.add(extract);
//		//transformations.add(renameblind);
//		//transformations.add(tokenize);
//		
//		// Validate
//		System.out.println("!!!!!!!!!!! Validate");
//		for(Transform transform : transformations) {
//			TransformValidation validation = TransformUtil.validate(transform, "java", "function");
//			System.out.println(transform);
//			System.out.println("\t" + validation.isvalid() + "-" + validation.getErrorMsg());
//		}
//		System.out.println();
//		System.out.println("!!!!!!!!!!! CHAIN");
//		
//		TransformChain tchain = TransformUtil.createChain(transformations, "java", "function");
//		System.out.println(tchain.get());
//		
//		System.out.println();
//		System.out.println("!!!!!!!!!!! Try");
//		byte [] file = IOUtils.toByteArray(new FileInputStream(new File("/media/jeff/WorkStorage/git/ThriftyClone/src/detection/logic/SelfCloneDetection.java")));
//		TransformOutput tout = TransformUtil.run(tchain, file);
//		System.out.println(tout.success());
//		for(String line : tout.output())
//			System.out.println(line);
//	}
//	
}
