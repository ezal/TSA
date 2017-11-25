package simple_ones;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @servlet description="simple collection deposit/retrieve and derived string test"
 * @servlet vuln_count = "1"
 */
@SuppressWarnings("serial")
public class LibExample {
	class Data {
		String value1;
		String value2;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String s1 = req.getParameter("name");		
		LinkedList<String> ll = new LinkedList<String>();
		ll.addLast(s1);
		String s2 = (String) ll.getLast();
		PrintWriter writer = resp.getWriter();  
		writer.println(s2);                    /* BAD */
	}

	public String getDescription() {
		return "field sensitivity";
	}

	public int getVulnerabilityCount() {
		return 1;
	}
}


