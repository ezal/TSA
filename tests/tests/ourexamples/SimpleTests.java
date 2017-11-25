package tests.ourexamples;

import org.junit.Test;

import sootTSA.TSA;
import util.JUnitTests;

public class SimpleTests extends JUnitTests {
	@Test public void c() {oneRun("simple_ones.C", "doGet", TSA.FAILURE);}
	@Test public void egood() {oneRun("simple_ones.Egood", "main", TSA.SUCCESS);}
	@Test public void ebad() {oneRun("simple_ones.Ebad", "main", TSA.FAILURE);}
	@Test public void myBasic30() {oneRun("simple_ones.MyBasic30", "doGet", TSA.FAILURE);}
	@Test public void myPred3() {oneRun("simple_ones.MyPred3", "doGet", TSA.SUCCESS);}
	@Test public void myPred4() {oneRun("simple_ones.MyPred4", "doGet", TSA.SUCCESS);} // fails: requires path-sensitivity
	@Test public void foo() {oneRun("simple_ones.Foo", "main", TSA.FAILURE);}
	@Test public void goo() {oneRun("simple_ones.Goo", "main", TSA.FAILURE);} // TODO: too strong? TODO: split in two? 
	@Test public void hoo() {oneRun("simple_ones.Hoo", "main", TSA.FAILURE);}
	@Test public void moo_f() {oneRun("simple_ones.Moo", "f", TSA.FAILURE);}
	@Test public void moo_f2() {oneRun("simple_ones.Moo", "f2", TSA.SUCCESS);}
	@Test public void moo_g() {oneRun("simple_ones.Moo", "f", TSA.FAILURE);}
	@Test public void moo_h() {oneRun("simple_ones.Moo", "f2", TSA.SUCCESS);}
	@Test public void excp() {oneRun("simple_ones.ExcpExample", "m", TSA.FAILURE);}
	@Test public void libexample() {oneRun("simple_ones.LibExample", "doGet", TSA.FAILURE);}
	@Test public void large1() {oneRun("simple_ones.ListNode", "bad", TSA.FAILURE);}
	@Test public void large2() {oneRun("simple_ones.ListNode", "ok", TSA.SUCCESS);}
}
