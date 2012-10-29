package org.example;
 
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
 
@Controller
public class PlateClient extends HttpServlet
{
    @RequestMapping(value="/hello")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try {
            servePlate(response, readFood(new URI(apiurl), new SimpleClientHttpRequestFactory()));
    	} catch(URISyntaxException e) {
    		throw new ServletException(e);
    	}
    }
    
    @RequestMapping(value="/secure")
    protected void doSecure(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try {
    		oauth2RestTemplate.getAccessToken();
            servePlate(response, readFood(new URI(apiurls), oauth2RestTemplate.getRequestFactory()));
    	} catch(URISyntaxException e) {
    		throw new ServletException(e);
    	}
    }

    // return array from json returned by given url
    private String[] readFood(URI url, ClientHttpRequestFactory factory) throws IOException
    {
    	ClientHttpRequest request = factory.createRequest(url, HttpMethod.GET);
    	request.getHeaders().add("Accept", "application/json");
    	InputStream is = request.execute().getBody();
    	StringBuffer data = new StringBuffer();
    	while (is.available()>0) data.append((char)is.read());
    	return (String[])JSONArray.fromObject(data.toString()).toArray(new String[0]);
    }
        
    // output html
    private void servePlate(HttpServletResponse response, String[] what) throws IOException
    {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.getWriter().println("<h1>Plate Client</h1>");
        response.getWriter().println("<p>Serving you a plate with food from elsewhere:</p>");
        response.getWriter().println("<ul>");
        for (int i=0; i<what.length; i++)
          response.getWriter().println("  <li>"+what[i]+"</li>");
        response.getWriter().println("</ul>");
    }

    // configuration by Spring application context
    private String apiurl = null;
    @Required public void setApiurl(String a) { apiurl = a; }
    private String apiurls = null;
    @Required public void setApiurls(String a) { apiurls = a; }
    
    // for access to OAuth2-protected resources, set by spring-security-oauth
    private OAuth2RestTemplate oauth2RestTemplate;
    @Required public void setOauth2RestTemplate(OAuth2RestTemplate a) { oauth2RestTemplate = a; }
}
