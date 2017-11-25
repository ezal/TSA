package mockup.javax.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DummyHttpServlet extends GenericServlet {
	
	 @Override
		public ServletConfig getServletConfig(){
			return new DummyServletConfig();
	    }

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
