package cwformat.formatter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import cwformat.logic.ReportReader;
import cwformat.objects.Clone;

public class xml_withsrc implements Formatter {
	
	Path basedir;
	
	public xml_withsrc(String options) {
		this.basedir = Paths.get(options);
	}
	
	@Override
	public void format(ReportReader rreader, Writer output) throws Exception {
		Clone clone;
		int i = 0;
		output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		while((clone = rreader.nextClone()) != null) {
			output.write("<clone id=\"" + (++i) +  "\">\n");
			output.write("<source file=\"" + clone.getFile1() + "\"" + " startline=\"" + clone.getStart1() + "\" endline=\"" + clone.getEnd1() + "\">\n");
			output.write(getCodeFragment(basedir, clone.getFile1(), clone.getStart1(), clone.getEnd1()));
			output.write("</source>\n");
			output.write("<source file=\"" + clone.getFile2() + "\"" + " startline=\"" + clone.getStart2() + "\" endline=\"" + clone.getEnd2() + "\"/>\n");
			output.write(getCodeFragment(basedir, clone.getFile2(), clone.getStart2(), clone.getEnd2()));
			output.write("</source>\n");
			output.write("</clone>\n");
			output.write("\n");
		}
		output.flush();
	}

	public String getCodeFragment(Path base, Path srcfile, int startline, int endline) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(base.resolve(srcfile).toFile()));
		String fragment = "";
		int i = 0;
		String line;
		while((line = br.readLine()) != null) {
			i++;
			if(i >= startline && i <= endline) {
				fragment += line + "\n";
			}
		}
		br.close();
		return fragment;
	}
	
}
