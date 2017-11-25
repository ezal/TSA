package mockup.misc;

public class BufferedReader extends Reader {
		String s;
	
		public BufferedReader(Reader in) {
			s = in.s;
		}
		
		public String readLine() {
			return s;
		}
}
