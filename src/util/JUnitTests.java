package util;

import static org.junit.Assert.fail;

import java.util.logging.Level;

import org.junit.Before;

import sootTSA.TSA;

public abstract class JUnitTests {
	@Before
    public void resetSoot() {
		// System.out.println("RESETTING SOOT");
		soot.G.reset();
		System.gc();
    }

	protected void oneRun(String className, String methodName, int expectedResult) {
		oneRun(className, methodName, "binary", 0, expectedResult);	
	}
	
	protected void oneRun(String className, String methodName, int ctxDepth, int expectedResult) {
		oneRun(className, methodName, "binary", ctxDepth, expectedResult);	
	}
	
	protected void oneRun(String className, String methodName, String monoid, int expectedResult) {
		oneRun(className, methodName, monoid, 0, expectedResult);
	}
	
	protected void oneRun(String className, String methodName, String monoid, int ctxDepth, int expectedResult) {
		// System.out.println("\nrunning test for " + className);
		
		TSA tsa = TSA.getInstance();
		int result = tsa.run(className, methodName, monoid, ctxDepth, Level.FINER, true);
		
		if (result != expectedResult)
			fail();

		// System.out.println("test finished for " + className);
	}
	
}


