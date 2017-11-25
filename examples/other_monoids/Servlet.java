package other_monoids;

import mockup.javax.servlet.DummyHttpRequest;
import ourlib.nonapp.TaintAPI;
import ourlib.nonapp.XSSAPI;

class Servlet {
	boolean showAlert;
	XSSAPI api;

	private static final String FIELD_NAME = "name";

	public void doGet(DummyHttpRequest request) {

		// String input = request.getParameter(FIELD_NAME);
		String input = TaintAPI.getTaintedString();
		// use case 1: Html embedding
		String s = "<body>" + api.escapeToHtml(input) + "</body>";
		api.output(s);

		// use case 2: JavaScript embedding
		if (showAlert) {
			api.output("<script>");
			api.output("  alert('" + api.escapeToJs(input) + "');");
			api.output("</script>");
		}

	}
}
