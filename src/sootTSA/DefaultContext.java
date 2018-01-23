package sootTSA;

public class DefaultContext implements Context {
	
	private static DefaultContext instance = null;	
	private DefaultContext() {
		// Exists only to defeat instantiation.
	}	
	public static DefaultContext getInstance() {
		if(instance == null) {
			instance = new DefaultContext();
		}
		return instance;
	}

	@Override
	public int compareTo(Context c) {
		if (c instanceof CallStringContext)
			return 1;
		return 0;
	}
	
	public String toString() {
		return "ctx0";
	}

}
