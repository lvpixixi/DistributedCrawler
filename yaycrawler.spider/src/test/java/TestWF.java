import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import bscrawler.spider.util.HtmlFormatter;

public class TestWF {
	 public Map<String, Object> request(String url, String content) {  
	        Map<String, Object> result = new HashMap<String, Object>();  
	        String errorStr = "";  
	        String status = "";  
	        String response = "";  
	        PrintWriter out = null;  
	        BufferedReader in = null;  
	        try {  
	            URL realUrl = new URL(url);  
	            // 打开和URL之间的连接  
	            URLConnection conn = realUrl.openConnection();  
	            HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;  
	            // 设置请求属性  
	            httpUrlConnection.setRequestProperty("Content-Type", "application/json");  
	            httpUrlConnection.setRequestProperty("x-adviewrtb-version", "2.1");  
	            // 发送POST请求必须设置如下两行  
	            httpUrlConnection.setDoOutput(true);  
	            httpUrlConnection.setDoInput(true);  
	            // 获取URLConnection对象对应的输出流  
	            out = new PrintWriter(httpUrlConnection.getOutputStream());  
	            // 发送请求参数  
	            out.write(content);  
	            // flush输出流的缓冲  
	            out.flush();  
	            httpUrlConnection.connect();  
	            // 定义BufferedReader输入流来读取URL的响应  
	            in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));  
	            String line;  
	            while ((line = in.readLine()) != null) {  
	                response += line;  
	            }  
	            status = new Integer(httpUrlConnection.getResponseCode()).toString();  
	        } catch (Exception e) {  
	            System.out.println("发送 POST 请求出现异常！" + e);  
	            errorStr = e.getMessage();  
	        }  
	        // 使用finally块来关闭输出流、输入流  
	        finally {  
	            try {  
	                if (out != null) { out.close();}  
	                if (in != null) {in.close();}  
	            } catch (Exception ex) {  
	                ex.printStackTrace();  
	            }  
	        }  
	         
	        result.put("errorStr", errorStr);  
	        result.put("response", response);  
	        result.put("status", status);  
	        return result;  
	  
	    }  
	 
	  public static String downLoadParse(String imageUrl,String reqUrl) {
	    	if(imageUrl.indexOf("http://")!=-1||imageUrl.indexOf("https://")!=-1) {
	    		return imageUrl;
	    	}
	    	
	    	String leftStr = reqUrl.substring(0, reqUrl.indexOf("/"));
	    	return leftStr+imageUrl;
	    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String reqUrl = "http://mp.weixin.qq.com/profile?src=3&timestamp=1543406771&ver=1&signature=6QQ2qVQSVMYLt7cmdAcpG9SG8rKaYyZOyKORWwqJq*aLcWXNl6kXWgmbq-dRMrFjgX5ZcJ1hYLiqTpwkNFIVTw==";
		String imageUrl = "/rr?timestamp=1543405370&src=3&ver=1&signature=dTl4iKVf92S-vNFiXsH-8rfAYzPRomrWaYsM4iF-CdJTzkbu6RfKuRQDfGIuO8etnL-GdlZ0RfvcuG8d*UypmzI2-tJFjeRPrwIHj2qPkiM=";
		System.out.println(HtmlFormatter.getAbsoluteFileSrc(imageUrl,reqUrl));
	}

}
