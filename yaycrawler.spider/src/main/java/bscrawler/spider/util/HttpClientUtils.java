package bscrawler.spider.util;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bscrawler.spider.translate.LocalRequest;
import us.codecraft.webmagic.utils.HttpConstant;

public class HttpClientUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);
	public static void realseResponse(CloseableHttpResponse httpResponse) {
		 if (httpResponse != null) {
             //ensure the connection is released back to pool
             try {
				EntityUtils.consume(httpResponse.getEntity());
			} catch (IOException e) {
				LOGGER.warn("close response fail", e);
			}
         }
	}
	
	public static HttpUriRequest getHttpUriRequest(LocalRequest request, int timeout, Map<String, String> headers, HttpHost proxy) {
		/*HttpHost proxy = null; //TODO
        if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
        	proxy = site.getHttpProxyFromPool().getHttpHost();
        } else if(site.getHttpProxy()!= null){
        	proxy = site.getHttpProxy();
        }*/
		RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setCookieSpec(CookieSpecs.BEST_MATCH);
        if (proxy !=null) {
			requestConfigBuilder.setProxy(proxy);
			//request.putExtra(Request.PROXY, proxy);
		}
        
        String method = request.getMethod();
        
        if (method != null && method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
        	requestConfigBuilder.setRedirectsEnabled(false);
        }
        return requestBuilder.build();
    }

	public static RequestBuilder selectRequestMethod(LocalRequest request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePair != null && nameValuePair.length > 0) {
                requestBuilder.addParameters(nameValuePair);
            }
            Object json = request.getExtra("jsonParam");
            if(json != null) {
            	StringEntity entity = new StringEntity(json.toString(),"UTF-8");
            	entity.setContentEncoding("UTF-8");    
                entity.setContentType("application/json"); 
                requestBuilder.setEntity(entity);
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }
	
public static final String PROXY = "proxy";
	
	public static LocalRequest getPostRequest(String url, String[] name, String[] value) {
		NameValuePair[] nameValuePair = new NameValuePair[name.length];
		LocalRequest req = new LocalRequest(url);
		for (int i = 0; i < nameValuePair.length; i++) {
			if(name[i]!=null && !name[i].equals("")){
				nameValuePair[i] = new BasicNameValuePair(name[i], value[i]);
			}
		}
		req.setLocalMethod(HttpConstant.Method.POST);
		req.putExtra("nameValuePair", nameValuePair);
		return req;
	}
	
	
}
