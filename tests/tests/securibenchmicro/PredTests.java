package tests.securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class PredTests extends JUnitTests {
	@Test public void pred1() {oneRun("securibench.micro.pred.Pred1", "doGet", TSA.SUCCESS);}
	@Test public void pred2() {oneRun("securibench.micro.pred.Pred2", "doGet", TSA.FAILURE);}
	@Test public void pred3() {oneRun("securibench.micro.pred.Pred3", "doGet", TSA.FAILURE);}
	@Test public void pred4() {oneRun("securibench.micro.pred.Pred4", "doGet", TSA.FAILURE);}
	@Test public void pred5() {oneRun("securibench.micro.pred.Pred5", "doGet", TSA.FAILURE);}
	@Test public void pred6() {oneRun("securibench.micro.pred.Pred6", "doGet", TSA.SUCCESS);} // fails. NOTE: path-sensitivity needed
	@Test public void pred7() {oneRun("securibench.micro.pred.Pred7", "doGet", TSA.SUCCESS);} // fails. NOTE: path-sensitivity needed
	@Test public void pred8() {oneRun("securibench.micro.pred.Pred8", "doGet", TSA.FAILURE);} // with arrays
	@Test public void pred9() {oneRun("securibench.micro.pred.Pred9", "doGet", TSA.FAILURE);} // with arrays
}
