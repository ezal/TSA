package tests.securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class StrongUpdatesTests extends JUnitTests {
	@Test public void strong_updates1() {oneRun("securibench.micro.strong_updates.StrongUpdates1", "doGet", TSA.SUCCESS);}
	@Test public void strong_updates2() {oneRun("securibench.micro.strong_updates.StrongUpdates2", "doGet", TSA.SUCCESS);}
	@Test public void strong_updates3() {oneRun("securibench.micro.strong_updates.StrongUpdates3", "doGet", TSA.SUCCESS);}
	@Test public void strong_updates4() {oneRun("securibench.micro.strong_updates.StrongUpdates4", "doGet", TSA.FAILURE);}
	@Test public void strong_updates5() {oneRun("securibench.micro.strong_updates.StrongUpdates5", "doGet", TSA.SUCCESS);} // 'synchronize' not supported
}
