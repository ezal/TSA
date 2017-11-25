package tests.ourexamples;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class OtherMonoidsTests extends JUnitTests {
	@Test public void xss() {oneRun("other_monoids.Servlet", "doGet", "xss", TSA.SUCCESS);}
	
	@Test public void authorization1() {oneRun("other_monoids.Authorization", "main", "authorization", TSA.FAILURE);}
	@Test public void authorization2() {oneRun("other_monoids.Authorization", "bad1", "authorization", TSA.FAILURE);}
	@Test public void authorization3() {oneRun("other_monoids.Authorization", "bad2", "authorization", TSA.FAILURE);}
	@Test public void authorization4() {oneRun("other_monoids.Authorization", "ok", "authorization", TSA.SUCCESS);}
}
