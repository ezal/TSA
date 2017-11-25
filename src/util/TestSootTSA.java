package util;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestCase;

import sootTSA.Main;
import sootTSA.TSA;

@RunWith(Parameterized.class)
public class TestSootTSA extends TestCase {
	String className;
	String methodName;
	int expectedResult;

	public TestSootTSA(String c, String m, int r) {
		super(c + "." + m);
		className = c;
		methodName = m;
		expectedResult = r;
	}

	@Test
	public void runTest() {
		System.out.println("\nrunning test for " + className);
		try {
			Main.main(new String[] {className, methodName});
			if (expectedResult != TSA.SUCCESS)
				fail();
		}
		catch (RuntimeException e) {
			String msg = e.getMessage();
			System.out.println(msg);
			for(StackTraceElement t: e.getStackTrace())
				System.out.println(t);
//			if (msg == null || !msg.equals(TypeBasedAnalysis.TYPE_ERROR)) 
//				fail();
//			else if (expectedResult != TypeBasedAnalysis.FAILURE)
//				fail();
		}
		System.out.println("test finished for " + className);
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
	 return Arrays.asList(new Object[][] {
	  {"securibench.micro.basic.Basic1", "doGet", TSA.FAILURE },
	  // {"securibench.micro.basic.Basic2", "doGet", TypeBasedAnalysis.FAILURE },
	  {"securibench.micro.basic.Basic3", "doGet", TSA.FAILURE }});
	}
}
