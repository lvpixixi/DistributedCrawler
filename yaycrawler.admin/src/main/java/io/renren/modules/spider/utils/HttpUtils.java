package io.renren.modules.spider.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.druid.util.StringUtils;


public class HttpUtils {

	 public static HttpMsg doCheck(String url, Map<String, String> param,String ip,int port){
	        // 创建Httpclient对象
	        CloseableHttpClient httpclient = null;
	       
	        HttpMsg msg = new HttpMsg();
	        
	        if(!StringUtils.isEmpty(ip)&&port>0) {
		        HttpHost proxy = new HttpHost(ip, port);
		        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		        httpclient = HttpClients.custom().setRoutePlanner(routePlanner).build();
	        }else {
	        	httpclient = HttpClients.createDefault();
	        }
	        

	        
	        CloseableHttpResponse response = null;
	        try {
	            // 创建uri
	            URIBuilder builder = new URIBuilder(url);
	            if (param != null) {
	                for (String key : param.keySet()) {
	                    builder.addParameter(key, param.get(key));
	                }
	            }
	            URI uri = builder.build();

	            // 创建http GET请求
	            HttpGet httpGet = new HttpGet(uri);
	            
	            RequestConfig requestConfig = RequestConfig.custom()
		                .setSocketTimeout(200*1000)
		                .setConnectTimeout(20*1000)
		                .build();
	            httpGet.setConfig(requestConfig);

	            // 执行请求
	            response = httpclient.execute(httpGet);
	            
	            msg.setResponseCode(response.getStatusLine().getStatusCode());
	            // 判断返回状态是否为200
	            if (response.getStatusLine().getStatusCode() == 200) {
	            	msg.setReachAble(true);
	                return msg;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            msg.setReachAble(false);
	            msg.setErrorMsg(e.getMessage());
	        } finally {
	            try {
	                if (response != null) {
	                    response.close();
	                }
	                httpclient.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return msg;
	    }
	 
	public static String getLocation(String url){
		URL serverUrl;
		String location = "";
		HttpURLConnection conn = null;
		try {
			serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl  
	                  .openConnection();  
	          conn.setRequestMethod("GET");  
	          // 必须设置false，否则会自动redirect到Location的地址  
	          conn.setInstanceFollowRedirects(false);  

	          conn.addRequestProperty("Accept-Charset", "UTF-8;");  
	          conn.addRequestProperty("User-Agent",  
	                  "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");  
	          conn.addRequestProperty("Referer", "http://zuidaima.com/");  
	          conn.connect();  
	          location = conn.getHeaderField("Location");  
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(conn!=null) {
				conn.disconnect();
			}
		}
        return location;
	}
	
    public static String doGet(String url, Map<String, String> param,String ip,int port){
        // 创建Httpclient对象
        CloseableHttpClient httpclient = null;
        if(!StringUtils.isEmpty(ip)&&port>0) {
	        HttpHost proxy = new HttpHost(ip, port);
	        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
	        httpclient = HttpClients.custom().setRoutePlanner(routePlanner).build();
        }else {
        	httpclient = HttpClients.createDefault();
        }
        
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url,String ip,int port){
        return doGet(url, null,ip,port);
    }


    public static String doPost(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
                return resultString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    /**
     *
     */
    public static String doPostJson(String url, String json) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            ContentType contentType;
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            return resultString;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
    
    public static void main(String[] args) throws Exception {
    	//String serverUrl="http://www.thespacereview.com/";
    	
    	String url = "https://www.baidu.com/link?url=ZvKGX1TJfyMwvzc3I-XCNLej-3I2u9lYfew9RA3jWtJDdnaj9gLb3vt8TOXTfasyg36R-crR3JHgxD4pQoIB52x38iefz4hISojOUe23PnK&amp;wd=&amp;eqid=af115e580020c2b6000000065cb1d2a5";
    	/*String proxyHost = "127.0.0.1";
    	int proxyPort = 51279;
    	HttpMsg msg = doCheck(url,null,proxyHost,proxyPort);
    	System.out.println(msg.getErrorMsg());*/
    	System.out.println(getLocation(url));
    	
    }
}
