/**
   @author Benjamin Livshits <livshits@cs.stanford.edu>
   
   $Id: Pred4.java,v 1.4 2006/04/04 20:00:40 livshits Exp $
 */
package simple_ones;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** 
 *  @servlet description="correlated test with an int variable" 
 *  @servlet vuln_count = "0" 
 *  */
public class MyPred4 {
    private static final String FIELD_NAME = "name";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int x = 3;
        
        String name = req.getParameter(FIELD_NAME);
        
        if(x == 4) {    // should never be taken
            PrintWriter writer = resp.getWriter();
            writer.println(name);              /* OK */     // nothing bad gets here
        }
    }

    public String getDescription() {
        return "correlated test with an int variable";
    }

    public int getVulnerabilityCount() {
        return 0;
    }    
}
