package securibenchmicro.sanitizers;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ourlib.nonapp.SanitizersAPI;;




public class Sanitizers1 {
	private static final String FIELD_NAME = "name";
	private PrintWriter writer;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter(FIELD_NAME);
		String clean = SanitizersAPI.clean(name);

		writer = resp.getWriter();
		resp.setContentType("text/html");

		writer.println("<html>");
		writer.println("<b>" + name + "</b>"); /* BAD */
		writer.println("<b>" + clean + "</b>"); /* OK */
		writer.println("</html>");

	}

	/**
	 * @sanitizer javascript sanitization routine
	 * 
	private String clean(String name) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			switch (ch) {
			case '<':
				buf.append("&lt;");
				break;
			case '>':
				buf.append("&gt;");
				break;
			case '&':
				buf.append("&amp;");
				break;
			default:
				if (Character.isLetter(ch) || Character.isDigit(ch)
						|| ch == '_') {
					buf.append(ch);
				} else {
					buf.append('?');
				}
			}
		}

		return buf.toString();
	} */

}