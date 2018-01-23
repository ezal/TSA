package mockup.misc;

import java.io.IOException;
import java.io.Writer;

import ourlib.nonapp.TaintAPI;

public class PrintWriter extends Writer {

	PrintWriter(String s) {}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(char[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub

	}
	
	public void println(Object obj) {
		if (obj instanceof String) {
			TaintAPI.outputString((String)obj); 
		}
		else if (obj instanceof Collection<?>) {
			Collection<?> c = (Collection<?>)obj;
			println(c.iterator().next());
		}
	}

	public void println(String str) {
		TaintAPI.outputString(str);
	}
}
