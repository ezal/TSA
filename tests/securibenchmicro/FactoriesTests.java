package securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class FactoriesTests extends JUnitTests {
	@Test public void factories1() {oneRun("securibench.micro.factories.Factories1", "doGet", TSA.FAILURE);}
	@Test public void factories2() {oneRun("securibench.micro.factories.Factories2", "doGet", TSA.FAILURE);}
	@Test public void factories3() {oneRun("securibench.micro.factories.Factories3", "doGet", TSA.FAILURE);}
}
