import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class TestProxy {
	public static void main(String args[]) throws ClientProtocolException, IOException
	{
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		HttpHost targetHost = new HttpHost("196.17.64.75", 8000, "http");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
		        new AuthScope(targetHost.getHostName(), targetHost.getPort()),
		        new UsernamePasswordCredentials("kJMRZp", "6dZeTD"));
		 
		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);
		 
		// Add AuthCache to the execution context
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		context.setAuthCache(authCache);
		HttpGet httpget = new HttpGet("https://nsgreg.nga.mil/doc/registers.jsp?register=NSG");
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httpget.setConfig(requestConfig);
	    CloseableHttpResponse response = httpclient.execute(
	            targetHost, httpget, context);
	    try {
	        HttpEntity entity = response.getEntity();
	        System.out.println("------------------------------------------------------");
	        System.out.println(EntityUtils.toString(entity));
	    } finally {
	        response.close();
	    }
		
	}
	
}
