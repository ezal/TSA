package ourlib.nonapp;

public class TaintAPI {
	public static String getTaintedString() {
		return "tainted";
	}

	public static void outputString(String s) {
		// s should not be tainted!
		System.out.println(s);
	}
}
