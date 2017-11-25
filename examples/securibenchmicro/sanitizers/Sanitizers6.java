package securibenchmicro.sanitizers;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ourlib.nonapp.SanitizersAPI;

/** 
 *  @servlet description="sanitizers for directory traversal" 
 *  @servlet vuln_count = "0" 
 *  */
public class Sanitizers6 {
    private static final String FIELD_NAME = "name";
    private PrintWriter writer;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter(FIELD_NAME);
        String clean = SanitizersAPI.clean(name);
        
        writer = resp.getWriter();
        resp.setContentType("text/html");
        
        writer.println("<html>" + clean + "</html>");                  /* OK */        
    }
    
    /** 
     * @sanitizer 
     * sanitization routine for removing . and /\ characters from strings.
     * This routine performs white-listing by only allowing letters and digits through.  
     * 
    private static String clean(String name) {
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            
            if(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
                buf.append(ch);
            } else {
                buf.append('?');
            }
        }
        
        return buf.toString();
    } */

}
    