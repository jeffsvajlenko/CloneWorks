package cwformat.formatter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import cwformat.logic.ReportReader;
import cwformat.objects.Clone;

public class xml implements Formatter {
	
	public xml(String options) {
	}
	
	@Override
	public void format(ReportReader rreader, Writer output) throws IOException {
		Clone clone;
		int i = 0;
		output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		while((clone = rreader.nextClone()) != null) {
			output.write("<clone id=\"" + (++i) +  "\">\n");
			output.write("<source file=\"" + clone.getFile1() + "\"" + " startline=\"" + clone.getStart1() + "\" endline=\"" + clone.getEnd1() + "\"/>\n");
			output.write("<source file=\"" + clone.getFile2() + "\"" + " startline=\"" + clone.getStart2() + "\" endline=\"" + clone.getEnd2() + "\"/>\n");
			output.write("</clone>\n");
		}
		output.flush();
	}

}
