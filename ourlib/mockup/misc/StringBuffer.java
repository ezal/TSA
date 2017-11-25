package mockup.misc;

public class StringBuffer {
	private String x;
	
	static {
		// java.lang.StringBuffer implements <clinit>. So we need to implement it as well.
		int z = 1;
	}
	
	public StringBuffer() {
		x = "";
	}
	
	public StringBuffer(String y) {
		x = y;
	}
	
	public StringBuffer append(String y) {
		x = y;
		return this;
	}

	public StringBuffer append(Object y) {
		x = y.toString();
		return this;
	}
	
	public StringBuffer append(int y) {
		return this;
	}
	
	public StringBuffer append(char y) {
		return this;
	}

	public String toString() {
		return x;
	}
	
	public StringBuffer insert(int x, char y) {
		return this; // TODO: is this sound?!
	}
}
