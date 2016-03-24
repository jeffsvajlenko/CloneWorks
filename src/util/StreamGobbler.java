package util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
	InputStream stream;

	public StreamGobbler(InputStream is) {
		this.stream = is;
	}

	@Override
	public void run()
    {
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader br = new BufferedReader(isr);
		try {
			while ((br.readLine()) != null);
		} catch (IOException e) {
			//e.printStackTrace();
		}
    }
}