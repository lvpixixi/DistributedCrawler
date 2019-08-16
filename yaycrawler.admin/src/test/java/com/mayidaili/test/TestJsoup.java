package com.mayidaili.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Joiner;

public class TestJsoup {
	
	// 定义申请获得的appKey和appSecret
	String appkey = "XXXXXXXX";
	String secret = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	String proxyIP = "127.0.0.1";
	int proxyPort = 8123;
	
	public String getAuthHeader() {
		
		// 创建参数表
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("app_key", appkey);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));// 使用中国时间，以免时区不同导致认证错误
		paramMap.put("timestamp", format.format(new Date()));
		
		// 对参数名进行排序
		String[] keyArray = paramMap.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		
		// 拼接有序的参数名-值串
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(secret);
		for (String key : keyArray) {
			stringBuilder.append(key).append(paramMap.get(key));
		}
		
		stringBuilder.append(secret);
		String codes = stringBuilder.toString();
		
		// MD5编码并转为大写， 这里使用的是Apache codec
		String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(codes).toUpperCase();
		
		paramMap.put("sign", sign);
		
		// 拼装请求头Proxy-Authorization的值，这里使用 guava 进行map的拼接
		String authHeader = "MYH-AUTH-MD5 " + Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
		
		System.out.println(authHeader);
		
		return authHeader;
	}
	
	public void ip() {
		
		/*try {
			Document doc = Jsoup.connect("http://1212.ip138.com/ic.asp").header("Proxy-Authorization", getAuthHeader())
					.proxy(proxyIP, proxyPort, null).followRedirects(false).validateTLSCertificates(false).timeout(10000).get();
			
			System.out.println(doc);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public static void main(String[] args) {
		new TestJsoup().ip();
	}
}
