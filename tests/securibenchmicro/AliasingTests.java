package securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class AliasingTests extends JUnitTests {
	@Test public void aliasing1() {oneRun("securibench.micro.aliasing.Aliasing1", "doGet", TSA.FAILURE);} // ok
	@Test public void aliasing2() {oneRun("securibench.micro.aliasing.Aliasing2", "doGet", TSA.SUCCESS);} // ok
	@Test public void aliasing3() {oneRun("securibench.micro.aliasing.Aliasing3", "doGet", TSA.SUCCESS);} // ok; (NOTE: bug in the original test case, which expected FAILURE) 
	@Test public void aliasing4() {oneRun("securibench.micro.aliasing.Aliasing4", "doGet", TSA.FAILURE);} // TODO: deal with println(Object)
	@Test public void aliasing5() {oneRun("securibench.micro.aliasing.Aliasing5", "doGet", TSA.FAILURE);} // ok
	@Test public void aliasing6() {oneRun("securibench.micro.aliasing.Aliasing6", "doGet", TSA.FAILURE);} // TODO: deal with println(Object)
}
