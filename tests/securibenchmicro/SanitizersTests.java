package securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class SanitizersTests extends JUnitTests {
	@Test public void sanitizers1() {oneRun("securibenchmicro.sanitizers.Sanitizers1", "doGet", "binary", TSA.FAILURE);}
	@Test public void sanitizers2() {oneRun("securibenchmicro.sanitizers.Sanitizers2", "doGet", "binary", TSA.SUCCESS);}
	@Test public void sanitizers3() {oneRun("securibenchmicro.sanitizers.Sanitizers3", "doGet", "binary", TSA.SUCCESS);}
	@Test public void sanitizers4() {oneRun("securibenchmicro.sanitizers.Sanitizers4", "doGet", "binary", TSA.FAILURE);} // see NOTES
	@Test public void sanitizers5() {oneRun("securibenchmicro.sanitizers.Sanitizers5", "doGet", "binary", TSA.FAILURE);}
	@Test public void sanitizers6() {oneRun("securibenchmicro.sanitizers.Sanitizers6", "doGet", "binary", TSA.SUCCESS);}
}

/*
NOTES:

As we do not analyze the sanitization method (e.g. clean), 
we cannot distinguish between a correct sanitization method and an incorrect one.
This is why in the Sanitizers4 example the call
  writer.println("<html>" + clean + "</html>");                  // BAD
is considered ok. 
Also, from our analysis' point of view, there is no difference between Sanitizers2 and Sanitizers6. 
*/