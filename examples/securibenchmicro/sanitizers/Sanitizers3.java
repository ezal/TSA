package securibenchmicro.sanitizers;



import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** 
 *  @servlet description="safe redirect" 
 *  @servlet vuln_count = "0" 
 *  */
public class Sanitizers3 {
    private static final String FIELD_NAME = "name";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String s = req.getParameter(FIELD_NAME);
        String name = s.toLowerCase(Locale.UK);

       resp.sendRedirect(URLEncoder.encode("/user/" + name, "UTF-8"));		/* OK */
    }
    
}