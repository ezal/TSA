package tests.securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class BasicTests extends JUnitTests {
	@Test public void basic1() {oneRun("securibench.micro.basic.Basic1", "doGet", TSA.FAILURE);}
	@Test public void basic2() {oneRun("securibench.micro.basic.Basic2", "doGet", TSA.FAILURE);}
	@Test public void basic3() {oneRun("securibench.micro.basic.Basic3", "doGet", TSA.FAILURE);}
	@Test public void basic4() {oneRun("securibench.micro.basic.Basic4", "doGet", TSA.FAILURE);}
	@Test public void basic5() {oneRun("securibench.micro.basic.Basic5", "doGet", TSA.FAILURE);}
	@Test public void basic6() {oneRun("securibench.micro.basic.Basic6", "doGet", TSA.FAILURE);}
	@Test public void basic7() {oneRun("securibench.micro.basic.Basic7", "doGet", TSA.FAILURE);}
	@Test public void basic8() {oneRun("securibench.micro.basic.Basic8", "doGet", TSA.FAILURE);}
	@Test public void basic9() {oneRun("securibench.micro.basic.Basic9", "doGet", TSA.FAILURE);}
	@Test public void basic10() {oneRun("securibench.micro.basic.Basic10", "doGet", TSA.FAILURE);}
	@Test public void basic11() {oneRun("securibench.micro.basic.Basic11", "doGet", TSA.FAILURE);}
	@Test public void basic12() {oneRun("securibench.micro.basic.Basic12", "doGet", TSA.FAILURE);}
	@Test public void basic13() {oneRun("securibench.micro.basic.Basic13", "doGet", TSA.FAILURE);}
	@Test public void basic14() {oneRun("securibench.micro.basic.Basic14", "doGet", TSA.FAILURE);} // TODO: source method does not return a string, but an enumeration: how to say that all its elements are tainted?	
	@Test public void basic15() {oneRun("securibench.micro.basic.Basic15", "doGet", TSA.FAILURE);}
	@Test public void basic16() {oneRun("securibench.micro.basic.Basic16", "doGet", TSA.FAILURE);}
	@Test public void basic17() {oneRun("securibench.micro.basic.Basic17", "doGet", TSA.FAILURE);}
	@Test public void basic18() {oneRun("securibench.micro.basic.Basic18", "doGet", TSA.FAILURE);}
	@Test public void basic19() {oneRun("securibench.micro.basic.Basic19", "doGet", TSA.FAILURE);}   
	@Test public void basic20() {oneRun("securibench.micro.basic.Basic20", "doGet", TSA.FAILURE);}
	@Test public void basic21() {oneRun("securibench.micro.basic.Basic21", "doGet", TSA.FAILURE);} // using arrays
	@Test public void basic22() {oneRun("securibench.micro.basic.Basic22", "doGet", TSA.FAILURE);}
	@Test public void basic23() {oneRun("securibench.micro.basic.Basic23", "doGet", TSA.FAILURE);}
	@Test public void basic24() {oneRun("securibench.micro.basic.Basic24", "doGet", TSA.FAILURE);}
	@Test public void basic25() {oneRun("securibench.micro.basic.Basic25", "doGet", TSA.FAILURE);} // using arrays
	@Test public void basic26() {oneRun("securibench.micro.basic.Basic26", "doGet", TSA.FAILURE);} // TODO: as with Basic14: source method returns map
	@Test public void basic27() {oneRun("securibench.micro.basic.Basic27", "doGet", TSA.FAILURE);}
	@Test public void basic28() {oneRun("securibench.micro.basic.Basic28", "doGet", TSA.FAILURE);}
	@Test public void basic29() {oneRun("securibench.micro.basic.Basic29", "doGet", TSA.FAILURE);}
	@Test public void basic30() {oneRun("securibench.micro.basic.Basic30", "doGet", TSA.FAILURE);}
	@Test public void basic31() {oneRun("securibench.micro.basic.Basic31", "doGet", TSA.FAILURE);} // TODO: as with Basic14: source method returns Cookie array
	@Test public void basic32() {oneRun("securibench.micro.basic.Basic32", "doGet", TSA.FAILURE);}
	@Test public void basic33() {oneRun("securibench.micro.basic.Basic33", "doGet", TSA.FAILURE);} // TODO: as with Basic14: source method returns enumeration
	@Test public void basic34() {oneRun("securibench.micro.basic.Basic34", "doGet", TSA.FAILURE);}
	@Test public void basic35() {oneRun("securibench.micro.basic.Basic35", "doGet", TSA.FAILURE);} // TODO: as with Basic14: source method returns enumeration; strange example...
	@Test public void basic36() {oneRun("securibench.micro.basic.Basic36", "doGet", TSA.FAILURE);} // TODO: as with Basic14: source method does not return string
	@Test public void basic37() {oneRun("securibench.micro.basic.Basic37", "doGet", TSA.FAILURE);} 
	@Test public void basic38() {oneRun("securibench.micro.basic.Basic38", "doGet", TSA.FAILURE);} 
	@Test public void basic39() {oneRun("securibench.micro.basic.Basic39", "doGet", TSA.FAILURE);}
	@Test public void basic40() {oneRun("securibench.micro.basic.Basic40", "doGet", TSA.FAILURE);}
	@Test public void basic41() {oneRun("securibench.micro.basic.Basic41", "doGet", TSA.FAILURE);}
	@Test public void basic42() {oneRun("securibench.micro.basic.Basic42", "doGet", TSA.FAILURE);}
}
