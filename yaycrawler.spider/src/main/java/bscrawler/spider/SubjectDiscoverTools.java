package bscrawler.spider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import bscrawler.spider.util.UrlParseUtils;
import us.codecraft.webmagic.selector.Html;

public class SubjectDiscoverTools {
	
	private String[] keyWords;
	private int totalCount;
	//private int pageSize;
	private String xpath;
	private String reqUrl;
	private String proxyHost;
	private int port;
	private String proxyUser;
	private String proxyPass;
	private boolean proxy = false;
	private String userAgent;
	private int timeout = 30000;
	
	
	public String testUrl(String url) throws MalformedChallengeException, ClientProtocolException, IOException {
		HttpHost proxy = new HttpHost(proxyHost, port);  
		  
		BasicScheme proxyAuth = new BasicScheme();  
		// Make client believe the challenge came form a proxy  
		proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));  
		BasicAuthCache authCache = new BasicAuthCache();  
		authCache.put(proxy, proxyAuth);  
		  
		CredentialsProvider credsProvider = new BasicCredentialsProvider();  
		credsProvider.setCredentials(  
		        new AuthScope(proxy),  
		        new UsernamePasswordCredentials(proxyUser, proxyPass));  
		  
		HttpClientContext context = HttpClientContext.create();  
		context.setAuthCache(authCache);  
		context.setCredentialsProvider(credsProvider);  
		  
		CloseableHttpClient httpclient = HttpClients.createDefault();  
		try {  
		    CloseableHttpResponse response = httpclient.execute(new HttpGet(url), context);  
		    HttpEntity entry = response.getEntity();
		    entry.getContent();

		    return response.toString();
		} finally {  
		    httpclient.close();  
		    return "";
		}  
		
		
	}
	public HashMap<String,Integer> getUrls() throws InterruptedException{
		HashMap<String,Integer> urls = new HashMap<>();
		String curUrl;
		for(String keyWord:keyWords) {
			//curUrl = StringUtils.replace(reqUrl, "${keyWord}", keyWord);
			int index=0;
			int pageSize=10;
			while(index<=totalCount) {
				curUrl = UrlParseUtils.parse0(reqUrl,keyWord,index);
				System.out.println(curUrl);
				Thread.sleep(new Random().nextInt(10)*100);
				System.out.println("requrl...."+curUrl);
				Connection connection = Jsoup.connect(curUrl).method(Connection.Method.GET);
				if(StringUtils.isEmpty(userAgent)) {
					connection = connection.timeout(timeout);
				}else {
					connection = connection.timeout(timeout).userAgent(userAgent);
				}
				
				if(this.isProxy()) {
				
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, port));
					String headerKey = "Proxy-Authorization";
					String encoded = new String(Base64.getEncoder().encode((new String(proxyUser + ":" + proxyPass).getBytes())));
					String headerValue = "Basic " + encoded;
					connection.request().addHeader(headerKey, headerValue);
					
					connection.proxy(proxy);
					//connection = connection.proxy(proxyHost, port).;
				}
				
			    String body;
				try {
					body = connection.execute().body();
					Html html = new Html(body);					
					List<String>  list = html.links().all();//html.xpath(xpath).all();
					if(list.size()==0) {
						break;
					}else {
						//pageSize = list.size();
						index = index+pageSize;
					}
					String domain;
					Integer count;
					for(String link:list){
						if(UrlParseUtils.isURL(link)) {
							domain = UrlParseUtils.getDomain(link);
							if(urls.containsKey(domain)) {
								count = MapUtils.getInteger(urls, domain);
								count=count+1;
								urls.put(domain, count);
							}else {
								urls.put(domain, 1);
							}
						}
						//System.out.println(link);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		
		}
		
		return urls;
	}
	
	public String[] getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String[] keyWords) {
		this.keyWords = keyWords;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}
	
	
	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public static void bing() throws InterruptedException {
		SubjectDiscoverTools tools = new SubjectDiscoverTools();
		
		String reqUrl = "https://cn.bing.com/search?q=${}&qs=HS&sc=6-0&cvid=AA65D95C1FF2413F8EE99A11CACF0640&sp=1&first=${}&FORM=BESBTB&ensearch=1";
		String xpath="//ol[@class='b_results']/li/h2/a/@href";
		tools.setKeyWords(
				new String[]{"METOC support","weather squadron","weather wing",
							 "weather attachment","meteorology","oceanography",
							 "space weather","weather radar","radiosonde balloon",
							 "satellite","decision aids","UUV","UAV","Weather Data Analysis","Weather Analysis and Forecast System"
				});
		tools.setReqUrl(reqUrl);
		tools.setXpath(xpath);
		tools.setTotalCount(50);
		
		
		Map<String,Integer> urls = tools.getUrls();
		for(Map.Entry<String,Integer> url:urls.entrySet()) {
			System.out.println(url.getValue()+"----"+url.getKey());
		}
	}
	
	public static void google() throws InterruptedException {
		SubjectDiscoverTools tools = new SubjectDiscoverTools();
		
		String reqUrl = "https://www.google.com/search?q=${}&ei=0dQAXOO7CMztvgTk6oGoAw&start=${}&sa=N&ved=0ahUKEwij6ND6uvveAhXMto8KHWR1ADU4ChDy0wMIcw&biw=1366&bih=657";
		String xpath="//ol[@class='b_results']/li/h2/a/@href";
		tools.setKeyWords(
				new String[]{"METOC support","weather squadron","weather wing",
							 "weather attachment","meteorology","oceanography",
							 "space weather","weather radar","radiosonde balloon",
							 "satellite","decision aids","UUV","UAV","Weather Data Analysis","Weather Analysis and Forecast System"
				});
		tools.setReqUrl(reqUrl);
		tools.setXpath(xpath);
		tools.setTotalCount(50);
		tools.setProxy(true);
		tools.setProxyHost("127.0.0.1");
		tools.setPort(51279);
		Map<String,Integer> urls = tools.getUrls();
		for(Map.Entry<String,Integer> url:urls.entrySet()) {
			System.out.println(url.getValue()+"----"+url.getKey());
		}
	}
	
	
	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPass() {
		return proxyPass;
	}

	public void setProxyPass(String proxyPass) {
		this.proxyPass = proxyPass;
	}

	public static void main(String[] args) throws Exception {
		
		SubjectDiscoverTools tools = new SubjectDiscoverTools();		
		String reqUrl = "https://www.google.com/";
		tools.setReqUrl(reqUrl);
		tools.setUserAgent("Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14");
		tools.setTotalCount(50);
		tools.setProxy(true);
		tools.setProxyHost("196.17.64.75");
		tools.setPort(8000);
		tools.setProxyUser("kJMRZp");
		tools.setProxyPass("6dZeTD");
		System.out.println(tools.testUrl(reqUrl));
	}

}
