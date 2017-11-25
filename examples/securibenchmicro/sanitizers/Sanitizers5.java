package securibenchmicro.sanitizers;



import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 *  @servlet description="encode and then decode" 
 *  @servlet vuln_count = "1" 
 *  */
public class Sanitizers5 {
    private static final String FIELD_NAME = "name";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String s = req.getParameter(FIELD_NAME);
        String name = s.toLowerCase(Locale.UK);
        String enc = URLEncoder.encode("/user/" + name, "UTF-8");
        String dec = URLDecoder.decode(enc, "UTF-8");

        resp.sendRedirect(dec);     								/* BAD */
        resp.sendRedirect(enc);    									/* OK */
    }
    
}