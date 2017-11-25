package mockup.misc;

import java.io.InputStream;

import mockup.javax.servlet.DummyServletInputStream;

public class InputStreamReader extends Reader {
	public InputStreamReader(InputStream in){
		if (in instanceof DummyServletInputStream) {
			s = ((DummyServletInputStream)in).s;
		}		
	}
}
