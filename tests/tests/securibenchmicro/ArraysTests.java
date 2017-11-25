package tests.securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class ArraysTests extends JUnitTests {
	@Test public void arrays1() {oneRun("securibench.micro.arrays.Arrays1", "doGet", TSA.FAILURE);}
	@Test public void arrays2() {oneRun("securibench.micro.arrays.Arrays2", "doGet", TSA.FAILURE);}
	@Test public void arrays3() {oneRun("securibench.micro.arrays.Arrays3", "doGet", TSA.FAILURE);}
	@Test public void arrays4() {oneRun("securibench.micro.arrays.Arrays4", "doGet", TSA.FAILURE);}
	@Test public void arrays5() {oneRun("securibench.micro.arrays.Arrays5", "doGet", TSA.FAILURE);}
	@Test public void arrays6() {oneRun("securibench.micro.arrays.Arrays6", "doGet", TSA.FAILURE);}
	@Test public void arrays7() {oneRun("securibench.micro.arrays.Arrays7", "doGet", TSA.FAILURE);}
	@Test public void arrays8() {oneRun("securibench.micro.arrays.Arrays8", "doGet", TSA.FAILURE);}
	// @Test public void arrays9() {oneRun("securibench.micro.arrays.Arrays9", "doGet", TypeBasedAnalysis.FAILURE);} // uses multi-arrays
	// @Test public void arrays10() {oneRun("securibench.micro.arrays.Arrays10", "doGet", TypeBasedAnalysis.FAILURE);} // uses multi-arrays
}
