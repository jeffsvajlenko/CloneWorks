package cwdetect.util;

import java.io.IOException;
import java.io.Writer;

import cwdetect.detection.Clone;

public class CloneFileWriter {
	
	private Writer out;
	
	public CloneFileWriter(Writer out, CloneDetectionConfig config) throws IOException {
		this.out = out;
		out.write("#blocks=" + config.getBlocks() + "\n");
		out.write("#clones=" + config.getClones() + "\n");
		out.write("#minsimilarity=" + config.getMinSimilarity() + "\n");
		out.write("#minlines=" + config.getMinLines() + "\n");
		out.write("#maxlines=" + config.getMaxLines() + "\n");
		out.write("#mintokens=" + config.getMinTokens() + "\n");
		out.write("#maxtokens=" + config.getMaxTokens() + "\n");
		out.write("#maxpartitionsize=" + config.getMaxPartitionSize() + "\n");
		out.write("#ispartitioned=" + config.isPartitioned() + "\n");
		out.write("#ispresorted=" + config.isPresorted() + "\n");
	}
	
	public void write(Clone clone) throws IOException {
		out.write(clone.getFileid1() + "," + clone.getStartline1() + "," + clone.getEndline1() + "," + clone.getFileid2() + "," + clone.getStartline2() + "," + clone.getEndline2() + "\n");
	}
	
	public void flush() throws IOException {
		out.flush();
	}
	
	public void close() throws IOException {
		out.close();
	}
	
}
