package mockup.javax.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DummyHttpServlet extends GenericServlet {
	
	static {
		// javax.servlet.http.HttpServlet implements <clinit>. So we need to implement it as well.
		int z = 1;
	}

	@Override
	public ServletConfig getServletConfig(){
		return new DummyServletConfig();
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
	}

}
