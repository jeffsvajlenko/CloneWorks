package cwformat.formatter;

import java.io.IOException;
import java.io.Writer;

import cwformat.logic.ReportReader;
import cwformat.objects.Clone;

public class csv implements Formatter {

	private String delimeter = ",";
	
	public csv(String options) {
		if(!options.equals("")) {
			delimeter = options;
		}
	}
	
	@Override
	public void format(ReportReader rreader, Writer output) throws IOException {
		Clone clone;
		while((clone = rreader.nextClone()) != null) {
			
			output.write(clone.getFile1() + delimeter +
					     clone.getStart1() + delimeter +
					     clone.getEnd1() + delimeter +
					     clone.getFile2() + delimeter +
					     clone.getStart2() + delimeter +
					     clone.getEnd2()+ 
					     "\n");
		}
		output.flush();
	}

}
