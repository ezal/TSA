package securibenchmicro.sanitizers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ourlib.nonapp.SanitizersAPI;

/** 
 *  @servlet description="buggy sanitizer" 
 *  @servlet vuln_count = "2" 
 *  */
public class Sanitizers4 {
    private static final String FIELD_NAME = "name";
    private PrintWriter writer;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter(FIELD_NAME);
        String clean = SanitizersAPI.clean(name);
        
        writer = resp.getWriter();
        resp.setContentType("text/html");
        
       writer.println("<html>" + name  + "</html>");                  /* BAD */
       writer.println("<html>" + clean + "</html>");                  /* BAD */        
    }
    
    /** 
     * buggy javascript sanitization routine 
     * 
    private String clean(String name) {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            switch (ch) {
                case '&':
                    buf.append("&amp;"); break;
                default:
                    buf.append(ch); break;
            }
        }
        
        return buf.toString();
    } */

}