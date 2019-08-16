package bscrawler.spider.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

public class MayiProxyUtil {
	// 定义申请获得的appKey和appSecret
	private String appkey = "242326887";
	private String secret = "f3d7fa9d2c41f6025850288b9932a0a6";
	private String proxy_ip ="s5.proxy.mayidaili.com";
	private int proxy_port =8123;
	
	public static String getAuthHeader(String appkey,String secret){
		// 定义申请获得的appKey和appSecret
		//String appkey = "170799173";
		//String secret = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
		 
		// 创建参数表
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("app_key", appkey);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));//使用中国时间，以免时区不同导致认证错误
		paramMap.put("timestamp", format.format(new Date()));
		 
		// 对参数名进行排序
		String[] keyArray = paramMap.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		 
		// 拼接有序的参数名-值串
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(secret);
		for(String key : keyArray){
		    stringBuilder.append(key).append(paramMap.get(key));
		}
		     
		stringBuilder.append(secret);
		String codes = stringBuilder.toString();
		         
		// MD5编码并转为大写， 这里使用的是Apache codec
		String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(codes).toUpperCase();
		paramMap.put("sign", sign);
		// 拼装请求头Proxy-Authorization的值，这里使用 guava 进行map的拼接
		String authHeader = "MYH-AUTH-MD5 " + Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
		return authHeader;
	}
	
	
	public String getAppkey() {
		return appkey;
	}


	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}


	public String getSecret() {
		return secret;
	}


	public void setSecret(String secret) {
		this.secret = secret;
	}


	public String getProxy_ip() {
		return proxy_ip;
	}


	public void setProxy_ip(String proxy_ip) {
		this.proxy_ip = proxy_ip;
	}


	public int getProxy_port() {
		return proxy_port;
	}


	public void setProxy_port(int proxy_port) {
		this.proxy_port = proxy_port;
	}


	public static void main(String[] args) throws IOException{
	/*	MayiProxy mp = new MayiProxy();
		
		Proxy proxy = new Proxy(mp.proxy_ip, mp.proxy_port);
		SimpleProxyProvider proxyProvider = SimpleProxyProvider.from(proxy);
		
		Document doc = Jsoup.connect("http://1212.ip138.com/ic.asp").header("Proxy-Authorization", mp.getAuthHeader()).get();
				*/
				//.proxy("test.proxy.mayidaili.com", 8123, null).header("Proxy-Authorization", authHeader).get();
	}

}
