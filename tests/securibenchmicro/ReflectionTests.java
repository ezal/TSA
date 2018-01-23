package securibenchmicro;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class ReflectionTests extends JUnitTests {
	@Test public void reflection1() {oneRun("securibench.micro.reflection.Refl1", "doGet", TSA.FAILURE);}
	@Test public void reflection2() {oneRun("securibench.micro.reflection.Refl2", "doGet", TSA.FAILURE);}
	@Test public void reflection3() {oneRun("securibench.micro.reflection.Refl3", "doGet", TSA.FAILURE);}
	@Test public void reflection4() {oneRun("securibench.micro.reflection.Refl4", "doGet", TSA.FAILURE);}
}
