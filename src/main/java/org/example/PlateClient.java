package org.example;
 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import eu.contrail.security.SecurityCommons;
 
@Controller
public class PlateClient extends HttpServlet
{
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
    @RequestMapping(value="/hello")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try {
            servePlate(response, readFood(new URI(apiurl), new RestTemplate()));
    	} catch(URISyntaxException e) {
    		throw new ServletException(e);
    	}
    }
    
    @RequestMapping(value="/secure")
    protected void doSecure(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	try {
    		// create certificate request
    		SecurityCommons sc = new SecurityCommons();
    		KeyPair keyPair = sc.generateKeyPair("RSA", 2048);
    		String signatureAlgorithm = "SHA256withRSA";
    		String csr = sc.writeCSR(sc.createCSR(keyPair, "CN=ignored_username", signatureAlgorithm));
    		
    		// retrieve certificate from OAuth-protected service
    		oauth2RestTemplate.getAccessToken();
    		oauth2RestTemplate.getMessageConverters().clear();
    		oauth2RestTemplate.getMessageConverters().add(new FormHttpMessageConverter());
    		oauth2RestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    		MultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
    		param.add("certificate_request", csr);
    		String r = oauth2RestTemplate.postForObject(new URI(certurl), param, String.class);
    		X509Certificate cert = (X509Certificate)sc.readPEM(new StringBufferInputStream(r), null);

    		// put client-side certificate+keypair in in-memory keystore
    		String kspasswd = "unimportant"; // required but not sensitive for in-memory
    		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    		ks.load(null, kspasswd.toCharArray());
    		ks.setEntry("default",
    				new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), new X509Certificate[]{cert}),
    				new KeyStore.PasswordProtection(kspasswd.toCharArray()));
    		
            // and get resource using that
    		SSLSocketFactory sslFactory = new SSLSocketFactory(ks, kspasswd);
    		HttpClient client = new DefaultHttpClient();
    		client.getConnectionManager().getSchemeRegistry().register(
    				new Scheme("https", 443, sslFactory));
            RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
            servePlate(response, readFood(new URI(apiurls), rest));
    	} catch(Exception e) {
    		throw new ServletException(e);
    	}
    }

    // return array from json returned by given url
    private String[] readFood(URI url, RestTemplate rest) throws IOException
    {
    	// only accept json
    	rest.getMessageConverters().clear();
    	rest.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
    	// return data as list of strings
    	ArrayList<String> data = rest.getForObject(url, ArrayList.class);
    	return data.toArray(new String[]{});
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
    private String certurl = null;
    @Required public void setCerturl(String a) { certurl = a; }
    
    // for access to OAuth2-protected resources, set by spring-security-oauth
    private OAuth2RestTemplate oauth2RestTemplate;
    @Required public void setOauth2RestTemplate(OAuth2RestTemplate a) { oauth2RestTemplate = a; }
}
