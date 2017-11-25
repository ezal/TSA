package other_monoids;

import ourlib.nonapp.Resource;

public class ReqAuth {
	
	void ok() {
		Resource r1 = new Resource();
		// Resource r2 = new Resource();
		r1.request();
		r1.use();
//		r2.request();
//		r2.use();
	}
	
	void bad() {
		Resource r1 = new Resource();
		Resource r2 = new Resource();
		r1.request();
		r1.use();
		r2.use();
	}
}
