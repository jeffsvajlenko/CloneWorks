package cwformat.formatter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import cwformat.logic.ReportReader;

public interface Formatter {

	public void format(ReportReader rreader, Writer output) throws Exception;
	
}
