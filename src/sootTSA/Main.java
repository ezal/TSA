package sootTSA;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Main {
	public static Logger mainLog = Logger.getAnonymousLogger();	

	static void setupLogging(Level l, Boolean toFile, String className, String methodName) {
		Handler handler = null;
		// String format = "%1$tF %1$tT %4$s %2$s %5$s%6$s%n";
		String format = "%4$s [%2$s] %5$s %6$s%n";
		System.setProperty("java.util.logging.SimpleFormatter.format", format);
		if (toFile) {
			// System.setProperty("java.util.logging.FileHandler.formatter.format", format);
			try {
				handler = new FileHandler("logs/" + className + '_' + methodName + ".log");
				handler.setFormatter(new SimpleFormatter());
			} catch (IOException e) {
				System.out.println("error opening log file");
				System.exit(1);
			}
		} else {
			handler = new ConsoleHandler();
		}
		handler.setLevel(l); // PUBLISH this level
		mainLog.addHandler(handler);
		mainLog.setLevel(l);
		mainLog.setUseParentHandlers(false);
	}

	
	public static void main(String[] args) {
		if (args.length == 0) {
			// For now, we have 3 types of tests cases
			// 1) examples from securibench-micro
			// 2) our own small examples
			// 3) a few bigger apps
			
			// sample case 1)
			// args = new String[] {"", "securibench.micro.collections.Collections13", "doGet3", "binary", "1", "FINER", "true"};
			// args = new String[] {"", "securibench.micro.aliasing.Aliasing4", "doGet", "binary", "0", "FINER", "true"};			
			// args = new String[] {"", "securibench.micro.inter.Inter12", "doGet2", "binary", "1", "FINER", "true"};
			// args = new String[] {"", "securibench.micro.basic.Basic26", "doGet", "binary", "0", "FINER", "true"};
			// args = new String[] {"", "securibench.micro.session.Session1", "doGet", "binary", "0", "FINER", "true"};

			// sample case 2)
			args = new String[] {"", "simple_ones.G", "m", "binary", "0", "FINER", "false"};			
			// args = new String[] {"", "simple_ones.LibExample", "doGet", "binary", "0", "FINER", "false"};
			// args = new String[] {"", "other_monoids.Authorization", "ok", "authorization", "FINER", "true"};
			// args = new String[] {"", "simple_ones.ListNode", "bad", "binary", "0", "INFO", "true"};
			// args = new String[] {"", "simple_ones.ObjectArray", "main", "binary", "0", "FINEST", "true"};
			// args = new String[] {"", "methodtypings.MT4", "m_bad2", "binary", "0", "FINEST", "true"};
			// args = new String[] {"", "securibenchmicro.sanitizers.Sanitizers1", "doGet", "xss", "FINER", "true"};
			
			// sample case 3)
//			String appPath = "securibench/blueblog/web/WEB-INF/classes";
//			String epClass = "se.bluefish.blueblog.servlet.ForwardingServlet";	
//			args = new String[] {appPath, epClass, "doGet", "binary", "0", "FINER", "true"};
		}
		else if (args.length < 4) {
			System.out.println("Usage: Main <app_path> <class> <method> <monoid> [<kCFA>] [<log-level>] [<to-file>]");
			return;
		}

		// setup the bound on the length of the call string context
		int kCFA = 0;
		if (args.length >= 5)
			kCFA = Integer.parseUnsignedInt(args[4]);
		
		// setup the Java logger, for debugging
		Level level = Level.WARNING;
		if (args.length >= 6)
			level = Level.parse(args[5]);
		
		Boolean toFile = false;
		if (args.length >= 7)
			toFile = Boolean.valueOf(args[6]);

		TSA tsa = TSA.getInstance();
		
		List<String> appClasses = new LinkedList<String>();
		// List<String> appClasses = Arrays.asList("testers.D");

		tsa.run(args[0], args[1], args[2], args[3], kCFA, level, toFile, appClasses);
	}
}
