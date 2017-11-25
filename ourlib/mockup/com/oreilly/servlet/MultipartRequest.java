package mockup.com.oreilly.servlet;

import javax.servlet.http.HttpServletRequest;

import ourlib.nonapp.TaintAPI;

public class MultipartRequest {
	public MultipartRequest(HttpServletRequest req, String saveDir) {		
	}
	
	public String getParameter(String name) {
		return TaintAPI.getTaintedString();
	}
}
