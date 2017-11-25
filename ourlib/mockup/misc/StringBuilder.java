package mockup.misc;

public class StringBuilder {
	private String x;

	public StringBuilder(String y) {
		x = y;
	}

	public StringBuilder append(String y) {
		x = y;
		return this;
	}
	
	public StringBuilder append(int y) {
		return this;
	}
	
	public StringBuilder append(char y) {
		return this;
	}

	public String toString() {
		return x;
	}
}
