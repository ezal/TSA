package simple_ones;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class C {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int x = 2;

		D d;
		if (x + 2 == 3) 
			d = new D(req.getParameter("name"));		 
		else 
			d = new D("Martin");
		 
		d.m(resp);
	}
}

class D {
	String s = null;

	D(String x) {
		s = x;
	}

	void m(HttpServletResponse resp) throws IOException {
		PrintWriter writer = resp.getWriter();
		writer.println(s);
	}
}
