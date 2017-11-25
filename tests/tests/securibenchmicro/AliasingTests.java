package tests.securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class AliasingTests extends JUnitTests {
	@Test public void aliasing1() {oneRun("securibench.micro.aliasing.Aliasing1", "doGet", TSA.FAILURE);} // ok
	@Test public void aliasing2() {oneRun("securibench.micro.aliasing.Aliasing2", "doGet", TSA.SUCCESS);} // ok
	@Test public void aliasing3() {oneRun("securibench.micro.aliasing.Aliasing3", "doGet", TSA.FAILURE);} // fails; bug in the original test case: we should check for TSA.SUCCESS 
	@Test public void aliasing4() {oneRun("securibench.micro.aliasing.Aliasing4", "doGet", TSA.FAILURE);} // bug
	@Test public void aliasing5() {oneRun("securibench.micro.aliasing.Aliasing5", "doGet", TSA.FAILURE);} // ok
	@Test public void aliasing6() {oneRun("securibench.micro.aliasing.Aliasing6", "doGet", TSA.FAILURE);} // bug
}
