package tests.securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class InterTests extends JUnitTests {

	@Test public void inter1() {oneRun("securibench.micro.inter.Inter1", "doGet", TSA.FAILURE);}
	@Test public void inter2() {oneRun("securibench.micro.inter.Inter2", "doGet", TSA.FAILURE);}
	@Test public void inter3() {oneRun("securibench.micro.inter.Inter3", "doGet", TSA.FAILURE);}
	@Test public void inter4() {oneRun("securibench.micro.inter.Inter4", "doGet", TSA.FAILURE);}
	@Test public void inter5() {oneRun("securibench.micro.inter.Inter5", "doGet", TSA.FAILURE);}
	@Test public void inter6() {oneRun("securibench.micro.inter.Inter6", "doGet", TSA.FAILURE);}
	@Test public void inter7() {oneRun("securibench.micro.inter.Inter7", "doGet", TSA.FAILURE);}
	@Test public void inter8() {oneRun("securibench.micro.inter.Inter8", "doGet", TSA.FAILURE);}
	@Test public void inter9() {oneRun("securibench.micro.inter.Inter9", "doGet", TSA.FAILURE);}
	@Test public void inter10() {oneRun("securibench.micro.inter.Inter10", "doGet", TSA.FAILURE);}
	@Test public void inter11() {oneRun("securibench.micro.inter.Inter11", "doGet", TSA.FAILURE);}
	@Test public void inter12() {oneRun("securibench.micro.inter.Inter12", "doGet", TSA.FAILURE);} 
	@Test public void inter12_2() {oneRun("securibench.micro.inter.Inter12", "doGet", TSA.SUCCESS);} // TODO: is this an example where context-sensitivity is needed?
	@Test public void inter13() {oneRun("securibench.micro.inter.Inter13", "doGet", TSA.FAILURE);}
	@Test public void inter14() {oneRun("securibench.micro.inter.Inter14", "doGet", TSA.FAILURE);}
}
