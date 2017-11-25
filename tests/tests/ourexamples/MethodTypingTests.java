package tests.ourexamples;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class MethodTypingTests extends JUnitTests {
	@Test public void fjsecok() {oneRun("methodtypings.FJsec", "mok", TSA.SUCCESS);}
	@Test public void fjsecbad() {oneRun("methodtypings.FJsec", "mbad", TSA.FAILURE);}

	@Test public void mt1_mb1() {oneRun("methodtypings.MT1", "mb1", TSA.FAILURE);}
	@Test public void mt1_mb2() {oneRun("methodtypings.MT1", "mb2", TSA.SUCCESS);}
	@Test public void mt1_mc1() {oneRun("methodtypings.MT1", "mc1", TSA.SUCCESS);}
	@Test public void mt1_mc2() {oneRun("methodtypings.MT1", "mc2", TSA.FAILURE);}
	
	@Test public void mt2_md() {oneRun("methodtypings.MT2", "md", TSA.SUCCESS);}
	@Test public void mt2_me() {oneRun("methodtypings.MT2", "me", TSA.SUCCESS);}
	@Test public void mt2_mg() {oneRun("methodtypings.MT2", "mg", TSA.FAILURE);}
	
	@Test public void mt4_ok() {oneRun("methodtypings.MT4", "m_ok", TSA.SUCCESS);}
	@Test public void mt4_bad() {oneRun("methodtypings.MT4", "m_bad", TSA.FAILURE);}
	@Test public void mt4_bad2() {oneRun("methodtypings.MT4", "m_bad2", TSA.FAILURE);}
}
