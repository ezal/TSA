package securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class CollectionsTests extends JUnitTests {
	@Test public void collections1() {oneRun("securibench.micro.collections.Collections1", "doGet", TSA.FAILURE);}
	@Test public void collections2() {oneRun("securibench.micro.collections.Collections2", "doGet", TSA.FAILURE);}
	@Test public void collections3_1() {oneRun("securibench.micro.collections.Collections3", "doGet1", TSA.FAILURE);}
	@Test public void collections3_2() {oneRun("securibench.micro.collections.Collections3", "doGet2", TSA.FAILURE);} 
	@Test public void collections4() {oneRun("securibench.micro.collections.Collections4", "doGet", TSA.FAILURE);}
	@Test public void collections5() {oneRun("securibench.micro.collections.Collections5", "doGet", TSA.FAILURE);}
	@Test public void collections6() {oneRun("securibench.micro.collections.Collections6", "doGet", TSA.FAILURE);}
	@Test public void collections7() {oneRun("securibench.micro.collections.Collections7", "doGet", TSA.FAILURE);}
	@Test public void collections8() {oneRun("securibench.micro.collections.Collections8", "doGet", TSA.FAILURE);}
	@Test public void collections9() {oneRun("securibench.micro.collections.Collections9", "doGet", TSA.SUCCESS);}
	@Test public void collections10() {oneRun("securibench.micro.collections.Collections10", "doGet", TSA.FAILURE);}
	@Test public void collections11() {oneRun("securibench.micro.collections.Collections11", "doGet", TSA.FAILURE);}
	@Test public void collections12() {oneRun("securibench.micro.collections.Collections12", "doGet", TSA.FAILURE);}
	@Test public void collections13_2() {oneRun("securibench.micro.collections.Collections13", "doGet2", TSA.FAILURE);}
	@Test public void collections13_3() {oneRun("securibench.micro.collections.Collections13", "doGet3", 1, TSA.SUCCESS);}
	@Test public void collections13_4() {oneRun("securibench.micro.collections.Collections13", "doGet4", TSA.FAILURE);}
	@Test public void collections14() {oneRun("securibench.micro.collections.Collections14", "doGet", TSA.FAILURE);}	
}
