package util;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestSuite;
import junit.framework.TestResult;

import sootTSA.TSA;

import junit.runner.Version;
		
public class TestAll {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new TestSootTSA("securibench.micro.basic.Basic1", "doGet", TSA.FAILURE));
		suite.addTest(new TestSootTSA("securibench.micro.basic.Basic2", "doGet", TSA.FAILURE));
		suite.addTest(new TestSootTSA("securibench.micro.basic.Basic3", "doGet", TSA.FAILURE));
		return suite;
	}
	
	public static void main(String[] args) {
		System.out.println("JUnit version is: " + Version.id());

		TestSuite suite = new TestSuite();
		suite.addTest(new TestSootTSA("securibench.micro.basic.Basic2", "doGet", TSA.FAILURE));
		suite.addTest(new TestSootTSA("securibench.micro.basic.Basic1", "doGet", TSA.FAILURE));
		// suite.addTest(new TestSootTSA("securibench.micro.basic.Basic3", "doGet", TypeBasedAnalysis.FAILURE));
		
		TestResult res = new TestResult();
		suite.run(res);

		for (Enumeration<TestFailure> e = res.failures(); e.hasMoreElements();)
			System.out.println(e.nextElement());
		
		System.out.println("Test suite successful? " + res.wasSuccessful());
			// TODO: write a testsuite
			// "securibench.micro.basic.Basic" + test, "doGet"
//			public void testBasicAll() {
//				for (int i = 1; i < 3; i++) {
//					testBasic(i);
//				}
//			}

	}
}



