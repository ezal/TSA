/**
   @author Benjamin Livshits <livshits@cs.stanford.edu>
   
   $Id: Pred3.java,v 1.5 2006/04/04 20:00:40 livshits Exp $
 */
package simple_ones;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 *  @servlet description="simple correlated tests" 
 *  @servlet vuln_count = "0" 
 *  */
public class MyPred3 {
    private static final String FIELD_NAME = "name";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean choice = new Random().nextBoolean();
        String name = "abc";
        
        if(choice) {
            name = req.getParameter(FIELD_NAME);
        }
        
        else {
            PrintWriter writer = resp.getWriter();
            writer.println(name);              /* OK */     // nothing bad gets here
        }
    }

    public String getDescription() {
        return "simple correlated tests";
    }

    public int getVulnerabilityCount() {
        return 0;
    }    
}
