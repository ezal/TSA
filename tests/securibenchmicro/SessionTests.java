package securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class SessionTests extends JUnitTests {
	@Test public void session1() {oneRun("securibench.micro.session.Session1", "doGet", TSA.FAILURE);}
	@Test public void session2() {oneRun("securibench.micro.session.Session2", "doGet", TSA.FAILURE);}
	@Test public void session3() {oneRun("securibench.micro.session.Session3", "doGet", TSA.FAILURE);}
}
