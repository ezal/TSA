package ourlib.nonapp;


public interface XSSAPI {
	String escapeToHtml(String str);

	String escapeToJs(String str);

	void output(String str);
}
