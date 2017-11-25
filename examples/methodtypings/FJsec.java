package methodtypings;

//import java.io.File;
//import java.util.logging.Formatter;

//class Writer {
//	void safewrite(Formatter x, File f) {
//		// System;
//		String s = x.format();
//		// demand(FileWrite)
//		write(s,f);
//	}
//}

import ourlib.nonapp.TaintAPI;

class Input {
	String get() {
		return "non-tainted string";
	} 
}

class InputExt extends Input {
	String get() {
		return TaintAPI.getTaintedString();
	} 
}

public class FJsec {	
	void mok() {
		Input i = new Input();
		String s = i.get();
		TaintAPI.outputString(s);
	}
	
	void mbad() {
		Input i = new InputExt();
		String s = i.get();
		TaintAPI.outputString(s);
	}
}

