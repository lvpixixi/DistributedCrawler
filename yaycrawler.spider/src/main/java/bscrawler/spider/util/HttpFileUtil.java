package bscrawler.spider.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.io.Files;

import us.codecraft.webmagic.downloader.HttpClientGenerator;

public class HttpFileUtil {
    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
    private static HttpFileUtil hfu = new HttpFileUtil();
    public static HttpFileUtil getInstance(){
    	return hfu;
    }
    private HttpFileUtil(){
    	
    }
    
    /**
	 * 带cookie的请求
	 * @param fileUrl
	 * @param filePath
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
    public String getRequestWithCookie(String reqUrl,List<Cookie> cookieL) throws IOException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CookieStore cookieStore = new BasicCookieStore();
		for(Cookie entry:cookieL){
			cookieStore.addCookie(entry);
		}
		httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		InputStream in = null;
		String responseStr = "";
        try {
       	 	HttpGet httpget = new HttpGet(reqUrl);
            httpget.setHeader( "User-Agent",  
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");  
            httpget.setHeader("Accept","*/*");  
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));

            String inputLine;
            StringBuffer result = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
            	result.append(inputLine);
            }
            responseStr = result.toString();
            reader.close();
       } catch (ClientProtocolException e) {
			e.printStackTrace();
		}finally {
			
            try {
           	 if(in!=null){
           		 in.close();
           	 }
			} catch (IOException e) {
				e.printStackTrace();
			}
           try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return responseStr;
	}
    
    /**
	 * 获取远程文件下载到本地
	 * @param fileUrl
	 * @param filePath
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public List<Cookie> getFileAndCookieTo(String fileUrl,String filePath,String[] cookie_Names) throws IOException{
		
		 System.out.println("fileUrl="+fileUrl);
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 CookieStore cookieStore = new BasicCookieStore();
		 httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		 InputStream in = null;
		 Map<String,String> cookies = new HashMap<>();
		 List<Cookie> cookieList = null;
         try {
        	 HttpGet httpget = new HttpGet(fileUrl);
             httpget.setHeader( "User-Agent",  
                     "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");  
             httpget.setHeader("Accept","*/*");  
             HttpResponse response = httpclient.execute(httpget);
             
           /*  if(cookie_Names!=null&&cookie_Names.length>0){
	             List<Cookie> cookieList = cookieStore.getCookies();
	             for(Cookie cookie:cookieList){
	            	 for(String cookie_Name:cookie_Names){
	            		 if (cookie.getName().equals(cookie_Name)) {
	            			 cookies.put(cookie_Name, cookie.getValue());
	                     }
	            	 }
	             }
             }*/
            
              cookieList = cookieStore.getCookies();
             
             HttpEntity entity = response.getEntity();
             in = entity.getContent();
             File file = new File(filePath);
             System.out.println("AbsolutePath="+file.getAbsolutePath());
         	 Files.createParentDirs(file);
             FileOutputStream fout = new FileOutputStream(file);
             int l = 0;
             byte[] tmp = new byte[1024];
             while ((l = in.read(tmp)) != -1) {
                 fout.write(tmp,0,l);
             }
             
            
             fout.flush();
             fout.close();
             System.out.println("downing :"+fileUrl);
         
        } catch (ClientProtocolException e) {
			e.printStackTrace();
		}finally {
             try {
            	 if(in!=null){
            		 in.close();
            	 }
			} catch (IOException e) {
				e.printStackTrace();
			}
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
         }
         
         return cookieList;
	}
	
	/**
	 * 获取远程文件下载到本地
	 * @param fileUrl
	 * @param filePath
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public void getFileTo(String fileUrl,String filePath) throws IOException{
		if(fileUrl.startsWith("//")) {
			fileUrl = "http:" + fileUrl;
		}
		 System.out.println("fileUrl="+fileUrl);
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 InputStream in = null;
         try {
        	 HttpGet httpget = new HttpGet(fileUrl);
             httpget.setHeader( "User-Agent",  
                     "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");  
             httpget.setHeader("Accept","*/*");  
             HttpResponse response = httpclient.execute(httpget);
             HttpEntity entity = response.getEntity();
             in = entity.getContent();
             File file = new File(filePath);
             System.out.println("AbsolutePath="+file.getAbsolutePath());
         	 Files.createParentDirs(file);
             FileOutputStream fout = new FileOutputStream(file);
             int l = 0;
             byte[] tmp = new byte[1024];
             while ((l = in.read(tmp)) != -1) {
                 fout.write(tmp,0,l);
             }
             
            
             fout.flush();
             fout.close();
        } catch (ClientProtocolException e) {
			e.printStackTrace();
		}finally {
             try {
            	 if(in!=null){
            		 in.close();
            	 }
			} catch (IOException e) {
				e.printStackTrace();
			}
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
         }
	}
}
