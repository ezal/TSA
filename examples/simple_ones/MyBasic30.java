/**
   @author Benjamin Livshits <livshits@cs.stanford.edu>
   
   $Id: Basic30.java,v 1.3 2006/04/04 20:00:40 livshits Exp $
 */
// package securibench.micro.basic;
package simple_ones;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @servlet description="field sensitivity"
 * @servlet vuln_count = "1"
 */
public class MyBasic30 {
    class Data {
        String value1;
        String value2;
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        Data d = new Data();
        d.value2 = name;
        d.value1 = "abc";
        
        Data d2 = new Data();
        d2.value1 = name;
        d2.value2 = "xyz";
        
        PrintWriter writer = resp.getWriter();
        writer.println(d.value1);                   /* OK */
        writer.println(d.value2);                   /* BAD */
        
        writer.println(d2.value1);                   /* BAD */
        writer.println(d2.value2);                   /* OK */
    }

    public String getDescription() {
        return "field sensitivity";
    }

    public int getVulnerabilityCount() {
        return 2;
    }
}
