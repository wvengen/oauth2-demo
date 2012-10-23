package org.example;
 
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
 
@Controller
public class FooService extends HttpServlet
{
    // list returned to access without authorization
    private static String[] listOpen = new String[] {"apple", "banana"};
    // list returned when authorized
    private static String[] listProt = new String[] {"apple", "banana", "pear", "grape"};
    
    @RequestMapping(value="/hello")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        boolean wantJson = false;
        // return json when accepted, html otherwise
        for (Enumeration e = request.getHeaders("Accept"); e.hasMoreElements(); ) {
           if ("application/json".equals(e.nextElement())) wantJson = true;
        }

        // select list according to authorization
        String[] list = listOpen;
        if (request.getRemoteUser() != null)
        	list = listProt;

        // output
        response.setStatus(HttpServletResponse.SC_OK);
        if (wantJson) {
           response.setContentType("application/json");
           response.getWriter().println(JSONArray.fromObject(list));
        } else {
          response.setContentType("text/html");
          response.getWriter().println("<h1>Foo Service</h1>");
          response.getWriter().println("<ul>");
          for (int i=0; i<list.length; i++)
            response.getWriter().println("  <li>"+list[i]+"</li>");
          response.getWriter().println("</ul>");
        }
    }
    
    // security is handled by spring-servlet.xml, just redirect here
    @RequestMapping(value="/secure")
    protected String doSecure()
    {
    	return "hello";
    }
}
