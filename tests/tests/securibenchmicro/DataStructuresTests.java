package tests.securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class DataStructuresTests extends JUnitTests {
	@Test public void datastructures1() {oneRun("securibench.micro.datastructures.Datastructures1", "doGet", TSA.FAILURE);}
	@Test public void datastructures2() {oneRun("securibench.micro.datastructures.Datastructures2", "doGet", TSA.FAILURE);}
	@Test public void datastructures3() {oneRun("securibench.micro.datastructures.Datastructures3", "doGet", TSA.FAILURE);}
	@Test public void datastructures4() {oneRun("securibench.micro.datastructures.Datastructures4", "doGet", TSA.SUCCESS);}
	@Test public void datastructures5() {oneRun("securibench.micro.datastructures.Datastructures5", "doGet", TSA.FAILURE);}
	@Test public void datastructures6() {oneRun("securibench.micro.datastructures.Datastructures6", "doGet", TSA.FAILURE);}
}
