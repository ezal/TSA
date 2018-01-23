package sootTSA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Handler;
import java.util.logging.Level;

import soot.Body;
import soot.G;
import soot.Printer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.SourceLocator;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceFileTag;
import soot.util.Chain;
import typetranslation.Transform;
import typetranslation.TypeMap;
import util.Pair;
import monoids.AuthorizationMonoid;
import monoids.BinaryMonoid;
import monoids.Monoid;
import monoids.XssMonoid;

public final class TSA {
	public static Monoid mon;
	
	public static final String TYPE_ERROR = "type inference error";
	static final String INTRAPROC_ERROR = "not enough info for intraprocedural analysis";
	
	public static final int SUCCESS = 0;
	public static final int FAILURE = 1; 

	final static int UNK = -3;
	final static int STAR = -2;
	final static int NIL = -1;
	
	// special region used for static fields and methods
	public final static Region nilRegion = new PosRegion("", NIL);     
	// special region returned only by library methods not in the built-in table 
	// (and not having a mock-up implementation)
	public final static Region unknownRegion = new PosRegion("", UNK); 
	// special region used only to make the built-in table an ordinary method table
	public final static Region starRegion = new PosRegion("", STAR);
	
	public static int ctxDepth = 0;
	
	
	// default "properties"
	static String pathPrefix = "../";
	
	private static TSA instance = null;	
	private TSA() {
		// Exists only to defeat instantiation.
	}	
	public static TSA getInstance() {
		if(instance == null) {
			instance = new TSA();
		}
		
		// Read the Soot classpath related fields from the config.properties
		// file, and update them if needed.
		Properties prop = new Properties();
		try {
			// load the properties file
			InputStream input = new FileInputStream("config.properties");
			prop.load(input);
		} catch (IOException e) {
			Main.mainLog.warning("Properties file not found. Using defaults.");
		}
		String tmp = prop.getProperty("prefix");
		if (tmp != null)
			pathPrefix = tmp;
		
		return instance;
	}
	
	public static Context getTopContext() {
		// return DefaultContext.getInstance();
		return new CallStringContext(ctxDepth);
	}
	
	public static Context contextTransfer(Context ctx, SootMethod m, Stmt stm) {
		if (ctx instanceof CallStringContext) {
			return ((CallStringContext)ctx).push(m, stm);
		}
		else
			// throw new RuntimeException("internal error: wrong context");
			return DefaultContext.getInstance();
	}
	
	public static Region getRegion(Context ctx, SootMethod m, Stmt stm) {
		return getCtxRegion(m, ctx, stm);
	}
	
	public static PosRegion getCtxRegion(SootMethod m, Context aContext, Stmt stm) {
		PosRegion r = getPosRegion(m, stm);
		if (aContext instanceof CallStringContext) {
			String s = ((CallStringContext)aContext).toString();
			r.setString(s + r.getString());
		}
		return r;
	}
	
	public static PosRegion getPosRegion(SootMethod m, Stmt stm) {
		int lineNumber = stm.getJavaSourceStartLineNumber();
		LineNumberTag ntag = (LineNumberTag)stm.getTag("LineNumberTag");
		if (ntag.getLineNumber() != lineNumber)
			throw new RuntimeException("internal error, though not a big deal...");
		SootClass c = m.getDeclaringClass();
		SourceFileTag ftag = (SourceFileTag)c.getTag("SourceFileTag");
		if (lineNumber > 1 && ftag != null) {
			return new PosRegion(c.getJavaPackageName() + "." + ftag.getSourceFile(), lineNumber);
		} else {
			if (ftag == null) {
				Main.mainLog.severe("could not obtain file name. Exiting...");
				throw new RuntimeException("internal error");
			}
			if (lineNumber == -1) {
				Main.mainLog.severe("could not obtain line number. Exiting...");
				throw new RuntimeException("internal error");
			}
			else {
				Main.mainLog.severe("the corresponding region is already used. Exiting...");
				throw new RuntimeException("internal error");
			}
		}
	}
	
	public static Set<Monoid> parseSet(String str) {
		String[] elems = str.substring(1,str.length()-1).split(", ");
		Set<Monoid> s = new HashSet<Monoid>();
		for (int i = 0; i < elems.length; i++) {
			if (!elems[i].equals(""))
				s.add(mon.parseElement(elems[i]));
		}
		return s;
	}


	static SootClass setupSoot(String appRelativePath, String epClass, List<String> appClasses) {
		// NOTE: We read Java bytecode not Java source code.
		// We would get better variable names from sources...
		// Furthermore, reading from sources enables considering as application 
		// classes more than just the ones set explicitly as application classes!		
		// However, as recommended here (https://github.com/Sable/soot/issues/465), 
		// when using Java 8 (which is what we use), we should read byte code 
		// instead of source code, as Soot's source code front-end is not compatible 
		// with Java 8 yet.
		// The concrete error one gets is:
		// Exception in thread "main" java.lang.Error: Error loading
		// java.lang.CharSequence at soot.JastAddJ...
		// That's probably the same issue as reported here:
		// https://github.com/Sable/soot/issues/394
		
		
		//////////////////////////////
		// We set up various soot options:
		
		Options.v().set_keep_line_number(true);

		Options.v().set_output_format(Options.output_format_jimple);

		Options.v().set_validate(true);

		Options.v().set_app(true);

		// Suppress Soot output when logging only at WARNING or SEVERE level
		if (Main.mainLog.getLevel().intValue() >= Level.WARNING.intValue()) {
			try {
				G.v().out = new PrintStream(new File("/dev/null"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

        //////////////////////////////
		// We set the Soot class path.

		// Options.v().set_whole_program(true);

		String libPath = pathPrefix + "securibench-micro/";
		String appPath = libPath + "classes/:";
		if (!appRelativePath.equals(""))
			appPath = pathPrefix + appRelativePath + ":";

		// Build the Soot class path
		String sootClassPath = "";			
		sootClassPath += libPath + "lib/j2ee.jar:";
		sootClassPath += libPath + "lib/cos.jar:";
		sootClassPath += appPath;
		sootClassPath += pathPrefix + "TSA/bin/:"; // TODO: only needed for test case 2) (see Main); update  

		Options.v().set_soot_classpath(sootClassPath);
		Options.v().set_prepend_classpath(true);

		// This is our "library"; the next line is only taken into account if loadNecessaryClasses is called 
		Options.v().set_exclude(Arrays.asList("ourlib.nonapp.*", "jdk.net.*", "com.oreilly.servlet.*"));

		Main.mainLog.config("Working Directory = " + System.getProperty("user.dir"));
		Main.mainLog.config("Java version: " + System.getProperty("java.version"));
		Main.mainLog.config("Java classpath: " + System.getProperty("java.class.path"));
		Main.mainLog.config("Soot classpath (options): " + Options.v().soot_classpath());
		Main.mainLog.config("Soot classpath (scene): " + Scene.v().getSootClassPath());		

		SootClass c = Scene.v().loadClassAndSupport(epClass);
		c.setApplicationClass();
		
		// TODO: It may be that the Scene does not contain it!
		// This is e.g. the case when epClass does not refer to it, 
		// but instead a class referred to by epClass
		if (Scene.v().containsClass("ourlib.nonapp.TaintAPI"))
			Scene.v().getSootClass("ourlib.nonapp.TaintAPI").setLibraryClass();

		String[] array = { "HttpRequest", "HttpResponse", "HttpSession",
				"ServletConfig", "ServletContext", "ServletInputStream" };
		for (String suffix : array) {
			String cName = "mockup.javax.servlet.Dummy" + suffix;
			SootClass cjx = Scene.v().loadClassAndSupport(cName);
			// SootClass cjx = Scene.v().getSootClass(cName);
			cjx.setApplicationClass();
		}
		SootClass c_mp = Scene.v().loadClassAndSupport("mockup.com.oreilly.servlet.MultipartRequest");
		c_mp.setApplicationClass();
		
		for (String cName: appClasses)
			Scene.v().getSootClass(cName).setApplicationClass();
		
		return c;
	}
	
	static SootMethod getEntryPointMethod(SootClass epClass, String mName) {
		try {
			// Generally, we write just the name, not the subsignature.
			// However, when the method is ambiguous, we write the signature.
			return epClass.getMethodByName(mName);
		} catch (RuntimeException e) {
			return epClass.getMethod(mName);
		}
	}

	public int run(String className, String methodName, String monoid, int ctxDepth, Level logLevel, Boolean toFile) {
		return run("", className, methodName, monoid, ctxDepth, logLevel, toFile, new LinkedList<String>());
	}
	
	private void addImplementingClass(String iName, String cName) {
		if (Scene.v().containsClass(iName)) {
			SootClass i = Scene.v().getSootClass(iName);
			if (i.isApplicationClass()) {
				Scene.v().addBasicClass(cName, SootClass.BODIES);
				Scene.v().loadClassAndSupport(cName).setApplicationClass();
			}
		}
	}
	
	void closeLog() {
		for(Handler h: Main.mainLog.getHandlers())
			h.close();   //must call h.close or a .lck file will remain.
	}
	
	// this method inserts, for each application class, a call to the  
	// the static initializer <clinit> (if the class has a static initializer)
	// into each of the methods of that class
	// NOTE: it is not sufficient to add this code only to the constructors of that class
	// (see example simples_ones.StaticField2)
	// NOTE: when adding context-sensitivity, we may need to treat this call in a special way 
	void inlineStaticInitializers() {
		for (SootClass c: Scene.v().getApplicationClasses()) {
			if (c.declaresMethodByName("<clinit>")) {
				for (SootMethod m: c.getMethods()) {
					if (!m.getName().equals("<clinit>") && m.hasActiveBody()) {
						Body b = m.retrieveActiveBody();
						Chain<Unit> units = b.getUnits();
						SootMethodRef mRef = Scene.v().makeMethodRef(c, "<clinit>", new LinkedList<Type>(), VoidType.v(), true);
						InvokeExpr e = Jimple.v().newStaticInvokeExpr(mRef);
						InvokeStmt u = Jimple.v().newInvokeStmt(e);
						units.addFirst(u);
					}
				}
			}
		}
	}
	
	public int run(String appPath, String className, String methodName, String monoid, int kCFA, Level logLevel, Boolean toFile, List<String> appClasses) {
		
		Main.setupLogging(logLevel, toFile, className, methodName);
		
		// Setup Soot
		SootClass epClass = setupSoot(appPath, className, appClasses);
		// Main.mainLog.config("Application classes: " + Scene.v().getApplicationClasses() + "\n");
		// Main.mainLog.config("Library classes: " + Scene.v().getLibraryClasses() + "\n");

		TypingInfo info = new TypingInfo();
		info.epMethod = getEntryPointMethod(epClass, methodName);
		
		// Set the monoid and based on this read the file 
		// containing types of built-in methods
		if (monoid.equals("binary"))
			mon = BinaryMonoid.U;
		else if (monoid.equals("xss"))
			mon = XssMonoid.Lit;
		else if (monoid.equals("authorization"))
			mon = AuthorizationMonoid.Id;
		else
			throw new RuntimeException("unknown monoid: " + monoid);
		
		// set bound on the call string context
		ctxDepth = kCFA;
		
		info.parseMethodTableFile("tables/" + monoid + ".table");

		Scene.v().loadBasicClasses();
				
		// Make all the mockup classes appearing in Scene as application classes
		// Also output these classes for debugging
		// NOTE: this needs to be done before performing the type translation		
		for (Entry<String, String> entry: TypeMap.nameMap.entrySet()) {
			String classKey = entry.getKey();
			String classVal = entry.getValue();
			if (Scene.v().containsClass(classKey)) {
				Scene.v().addBasicClass(classVal, SootClass.BODIES);
				Scene.v().loadClassAndSupport(classVal).setApplicationClass();
				SootClass c = Scene.v().getSootClass(classVal);
				if (c.isConcrete())
					outputJimpleClass(c);
			}
		}
		
		// If an interface is an application class, then at least one 
		// of its implementations should be an application class.
		addImplementingClass("mockup.misc.Collection", "mockup.misc.LinkedList"); 
		addImplementingClass("mockup.misc.Enumeration", "mockup.misc.StringTokenizer");
		
		Scene.v().loadNecessaryClasses();
		
		// Perform the type translation
		Transform tf = new Transform();
		tf.transform();
		Scene.v().releaseActiveHierarchy();
		Scene.v().releaseFastHierarchy();
		
		inlineStaticInitializers();
		
		// Output Jimple representation of the application classes (for debugging)
		Chain<SootClass> allAppClasses = Scene.v().getApplicationClasses();
		outputJimpleClasses(allAppClasses);
		
		Main.mainLog.config("Application classes: " + allAppClasses + "\n");

		long startTime = System.currentTimeMillis();

		// Perform analysis
		int maxIter = 30;
		InterProcAnalysis analysis = new InterProcAnalysis(info);
		analysis.doAnalysis(maxIter);
		
		long endTime = System.currentTimeMillis();
		System.out.println("The analysis took " + (endTime - startTime) + " milliseconds");

		// Check results
		if (analysis.getIter() < maxIter) {
			Effects effects = analysis.getEffects();
			if (mon.allowed(effects.getSet())) {
				Main.mainLog.info("No error detected.");
				System.out.println("No error detected.");
				closeLog();
				return SUCCESS;
			}
			else {
				Main.mainLog.info("Possible error detected.");
				System.out.println("Possible error detected.");
				closeLog();
				return FAILURE;
			}
		} else {
			Main.mainLog.severe("The analysis did not converge in " + maxIter + " iterations.");
			System.out.println("The analysis did not converge in " + maxIter + " iterations.");
			closeLog();
			return FAILURE;
		}
	}

	private static void outputJimpleClasses(Chain<SootClass> classes) {
		for (SootClass c : classes) {
			c.checkLevel(SootClass.BODIES);
			if ((c.isConcrete() || (c.isAbstract() && !c.isInterface()))
					 && !TypeMap.nameMap.containsValue(c.getName())) 
				outputJimpleClass(c);
		}
	}

	public static void outputJimpleClass(SootClass sClass) {
		for (SootMethod m : sClass.getMethods()) {
			if (m.hasActiveBody()) {
				m.retrieveActiveBody();
			}
		}

		OutputStream streamOut = null;
		try {
			String filename = SourceLocator.v().getFileNameFor(sClass, Options.output_format_jimple);
			streamOut = new FileOutputStream(filename);
			PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
			Printer.v().printTo(sClass, writerOut);
			writerOut.flush();
			writerOut.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (streamOut != null)
				try {
					streamOut.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
		}
	}
	
}


