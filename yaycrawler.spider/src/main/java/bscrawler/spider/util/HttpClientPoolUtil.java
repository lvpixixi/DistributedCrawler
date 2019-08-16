package bscrawler.spider.util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.codecraft.webmagic.utils.UrlUtils;

public class HttpClientPoolUtil {
	 
	 protected static Log logger = LogFactory.getLog(HttpClientPoolUtil.class);
	  
	 private static PoolingHttpClientConnectionManager cm;
	 private static String EMPTY_STR = "";
	 private static String encode = "utf-8";
	 
	 private static void init() {
	  if (cm == null) {
	   cm = new PoolingHttpClientConnectionManager();
	   cm.setMaxTotal(50);// 整个连接池最大连接数
	   cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
	  }
	 }
	 
	 /**
	  * 通过连接池获取HttpClient
	  * 
	  * @return
	  */
	 public static CloseableHttpClient getHttpClient(List<Cookie> cookieL) {
	  init();
	  CloseableHttpClient httpclient;
	  if(cookieL!=null&&cookieL.size()>0){
		  CookieStore cookieStore = new BasicCookieStore();
		  for(Cookie entry:cookieL){
			  cookieStore.addCookie(entry);
		  }
		  httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(cm).build();
	  }else{
		  httpclient = HttpClients.custom().setConnectionManager(cm).build();
	  }
	  return httpclient;
	 }
	 
	 public static String httpGetRequest(String url,List<Cookie> cookies,String charset) {
	  HttpGet httpGet = new HttpGet(url);
	  return getResult(httpGet,cookies,charset);
	 }
	 
	 public static String httpGetRequest(String url, Map<String, Object> params,List<Cookie> cookies,String charset) throws URISyntaxException {
	  URIBuilder ub = new URIBuilder();
	  ub.setPath(url);
	 
	  ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
	  ub.setParameters(pairs);
	 
	  HttpGet httpGet = new HttpGet(ub.build());
	   
	  return getResult(httpGet,cookies,charset);
	 }
	 
	 public static String httpGetRequest(String url, Map<String, Object> headers,List<Cookie> cookies, Map<String, Object> params,String charset)
	   throws URISyntaxException {
	  URIBuilder ub = new URIBuilder();
	  ub.setPath(url);
	 
	  ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
	  ub.setParameters(pairs);
	 
	  HttpGet httpGet = new HttpGet(ub.build());
	  for (Map.Entry<String, Object> param : headers.entrySet()) {
	   httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
	  }
	  return getResult(httpGet,cookies,charset);
	 }
	 
	 public static String httpPostRequest(String url,List<Cookie> cookies,String charset) {
	  HttpPost httpPost = new HttpPost(url);
	  return getResult(httpPost,cookies,charset);
	 }
	 
	 public static String httpPostRequest(String url, Map<String, Object> params,List<Cookie> cookies,String charset) throws UnsupportedEncodingException {
	  HttpPost httpPost = new HttpPost(url);
	  ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
	  httpPost.setEntity(new UrlEncodedFormEntity(pairs, encode));
	  return getResult(httpPost,cookies,charset);
	 }
	 
	 public static String httpPostRequest(String url, Map<String, Object> headers,List<Cookie> cookies, Map<String, Object> params,String charset)
	   throws UnsupportedEncodingException {
	  HttpPost httpPost = new HttpPost(url);
	 
	  for (Map.Entry<String, Object> param : headers.entrySet()) {
	   httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
	  }
	 
	  ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
	  httpPost.setEntity(new UrlEncodedFormEntity(pairs, encode));
	 
	  return getResult(httpPost,cookies,charset);
	 }
	 
	 public static String httpPostRequest(String url, Map<String, Object> headers,List<Cookie> cookies, String strBody,String charset)
	   throws Exception {
	  HttpPost httpPost = new HttpPost(url);
	 
	  for (Map.Entry<String, Object> param : headers.entrySet()) {
	   httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
	  }
	  httpPost.setEntity(new StringEntity(strBody, encode));
	  return getResult(httpPost,cookies,charset);
	 }
	  
	 private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
	  ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
	  for (Map.Entry<String, Object> param : params.entrySet()) {
	   pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
	  }
	 
	  return pairs;
	 }
	 
	 /**
	  * 处理Http请求
	  * 
	  * setConnectTimeout：设置连接超时时间，单位毫秒。
	  * setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
	  * setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
	  * 
	  * @param request
	  * @return
	  */
	 private static String getResult(HttpRequestBase request,List<Cookie> cookies,String charset) {
	   
	  RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000)
	    .setConnectionRequestTimeout(5000).setSocketTimeout(60000).build();
	  request.setConfig(requestConfig);// 设置请求和传输超时时间
	 
	  // CloseableHttpClient httpClient = HttpClients.createDefault();
	  CloseableHttpClient httpClient = getHttpClient(cookies);
	  try {
	   CloseableHttpResponse response = httpClient.execute(request); //执行请求
	   
	   // response.getStatusLine().getStatusCode();
	   String result = getContent(charset,response);
	   response.close();
	   // httpClient.close();
	   return result;
	  
	  } catch (ClientProtocolException e) {
	   logger.error("[maperror] HttpClientUtil ClientProtocolException : " + e.getMessage());
	   e.printStackTrace();
	  } catch (IOException e) {
	   logger.error("[maperror] HttpClientUtil IOException : " + e.getMessage());
	   e.printStackTrace();
	  } finally {
	 
	  }
	  return EMPTY_STR;
	 }
	 
	  protected static String getContent(String charset, HttpResponse httpResponse) throws IOException {
	        if (charset == null) {
	            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
	            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
	            if (htmlCharset != null) {
	                return new String(contentBytes, htmlCharset);
	            } else {
	                return new String(contentBytes);
	            }
	        } else {
	            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
	        }
	    }

	    protected static String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
	        String charset;
	        // charset
	        // 1銆乪ncoding in http header Content-Type
	        String value = httpResponse.getEntity().getContentType().getValue();
	        charset = UrlUtils.getCharset(value);
	        if (StringUtils.isNotBlank(charset)) {
	            return charset;
	        }
	        // use default charset to decode first time
	        Charset defaultCharset = Charset.defaultCharset();
	        String content = new String(contentBytes, defaultCharset.name());
	        // 2銆乧harset in meta
	        if (StringUtils.isNotEmpty(content)) {
	            Document document = Jsoup.parse(content);
	            Elements links = document.select("meta");
	            for (Element link : links) {
	                // 2.1html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	                String metaContent = link.attr("content");
	                String metaCharset = link.attr("charset");
	                if (metaContent.indexOf("charset") != -1) {
	                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
	                    charset = metaContent.split("=")[1];
	                    break;
	                }
	                // 2.2 <meta charset="UTF-8" />
	                else if (StringUtils.isNotEmpty(metaCharset)) {
	                    charset = metaCharset;
	                    break;
	                }
	            }
	        }
	        return charset;
	    }
	 public static void main(String[] args){
		 
		 String html = httpGetRequest("https://mp.weixin.qq.com/s?timestamp=1519818453&src=3&ver=1&signature=UaDDpHPY08PlVnTPRZz2oGmcNzEJkfzEeguN3LwRj-mrUuvOw-qzDV9BmePz8j1kLkLhSULGSF46rQGlE4a2sWdn-kLMn031A7UmHGpsZv2u*gGSR-iz45oJXfaCtm*uJ3oxCojoHf3cv4QIw4eIqP*-yuzTxyqu7jx0OCVpmLM=",null,null);
		 System.out.println(html);
	 }
	 
	 
}
