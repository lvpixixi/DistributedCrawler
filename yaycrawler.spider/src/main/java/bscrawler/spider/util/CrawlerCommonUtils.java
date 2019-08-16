package bscrawler.spider.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.utils.HttpConstant;
import us.codecraft.webmagic.utils.UrlUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;


/**
 * 爬虫公共方法工具类
 * @author wwq
 * 2016-3-8
 */
public class CrawlerCommonUtils {
	
	protected static ResourceBundle resource = ResourceBundle.getBundle("translateConfig");//资源配置
	
	private static Pattern pattern = Pattern.compile("\\S*[?]\\S*");  
	
	private static String urlRegex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
	
	public static boolean strIsNull(String str) {
		return str == null || "".equals(trim(str));
	}
	
	/**
     * canonicalizeUrl
     * <br>
     * Borrowed from Jsoup.
     *
     * @param url url
     * @param refer refer
     * @return canonicalizeUrl
     */
    public static String canonicalizeUrl(String url, String refer) {
    	if(strIsNull(url)) {
			return "";
		}
    	return UrlUtils.canonicalizeUrl(url, refer);
    }
	
	/**
	* 验证网址Url
	* 
	* @param 待验证的字符串
	* @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	*/
	public static boolean IsUrl(String str) {
		if(strIsNull(str)) {
			return false;
		}
		return match(urlRegex, str);
	}
	
	
	public static String matchDateString(String dateStr) {  
		try {  
			List<String> matches = null;  
			Pattern p = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);  
			Matcher matcher = p.matcher(dateStr);  
			if (matcher.find() && matcher.groupCount() >= 1) {  
				matches = new ArrayList<String>();  
				for (int i = 1; i <= matcher.groupCount(); i++) {  
					String temp = matcher.group(i);  
					matches.add(temp);  
				}  
			} else {  
				matches = Collections.EMPTY_LIST;  
			}             
			if (matches.size() > 0) {  
				return ((String) matches.get(0)).trim();  
			}  
		} catch (Exception e) {  
			return "";  
		}  
		return dateStr;  
	} 
	
	public static Request getPostRequest(String url, String[] name, String[] value) {
		NameValuePair[] nameValuePair = new NameValuePair[name.length];
		Request req = new Request(url);
		for (int i = 0; i < nameValuePair.length; i++) {
			if(name[i]!=null && !name[i].equals("")){
				nameValuePair[i] = new BasicNameValuePair(name[i], value[i]);
			}
		}
		req.setMethod(HttpConstant.Method.POST);
		req.putExtra("nameValuePair", nameValuePair);
		return req;
	}
	
	
	
	 public static String fileToString(File file) {
    	InputStream in = null;
    	BufferedReader br=null;
    	Reader reader = null;
    	String result = "";
		try {
			in = new FileInputStream(file);
			reader = new InputStreamReader(in, "UTF-8");
			br=new BufferedReader(new InputStreamReader(in,"UTF-8"));  
	    	String line = null;  
	    	
	    	while ((line = br.readLine()) != null) {  
	    		result += line;  
	    	}  
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(br);
		}
		return result;
    }
	
	 /**
	  * 在URL中截取文件的扩展名
	  * @param url
	  * @return
	  */
	public static String parseSuffix(String url) {
		try {
			Matcher matcher = pattern.matcher(url);   
	        String[] spUrl = url.toString().split("/");  
	        int len = spUrl.length;  
	        String endUrl = spUrl[len - 1];  
	        if(matcher.find()) {  
	            String[] spEndUrl = endUrl.split("\\?");
	            if(spEndUrl[0].indexOf(".") == -1) {
	            	return "";
	            }
	            String name = spEndUrl[0].substring(spEndUrl[0].lastIndexOf(".")+1, spEndUrl[0].length());
	            return name;
	        }
	        if(endUrl.indexOf(".") == -1) {
            	return "";
            }
	        String name = endUrl.substring(endUrl.lastIndexOf(".")+1, endUrl.length());
	        return name;       
		} catch (Exception e) {
			return "";
		}
	}
	

	
	/**
	 * 判断json中的值是否都为空
	 * @param json
	 * @return
	 */
	public static boolean jsonValueIsAllNull(JSONObject json) {
		Collection<Object> list = json.values();
		if(list == null || list.size() == 0) {
			return true;
		}
		for(Object obj : list) {
			if(obj != null && !"".equals(StringUtils.trimToEmpty(obj.toString()))) {
				return false;
			}
		}
		return true;
	}
	
	public static String getAuthHeader(boolean isLockIP, boolean isRelease, Object num) {
    	// 定义申请获得的appKey和appSecret
    	String appkey = resource.getString("APP_KEY");//"170799173";
    	String secret = resource.getString("APP_SECERT");//"f7f83717372bdd351512af2985de037c"; 
    	// 创建参数表
    	SortedMap<String, String> paramMap = new TreeMap<String, String>();
    	paramMap.put("app_key", appkey);
    	paramMap.put("enable-simulate", "true");
    	paramMap.put("random-useragent", "pc");
    	paramMap.put("timeout", "60000");
    	paramMap.put("decode-chunk", "true");
    	if(isRelease) {//释放IP
    		paramMap.put("release-transaction", num.toString());
    	}
    	paramMap.put("timestamp", DateUtil.getCurrentDateTimeStr());  
    	
    	if(isLockIP) { //锁定IP
    		paramMap.put("with-transaction", num.toString());
    	}
    	
    	//paramMap.put("with-transaction", "1");
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
    	//System.out.println(authHeader);
    	return authHeader;
    }
	
	/**
	* @param regex
	* 正则表达式字符串
	* @param str
	* 要匹配的字符串
	* @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	*/
	public static boolean match(String regex, String str) {
		return Pattern.compile(regex).matcher(str).matches();
	}
	
	/**
	 * 移除URL中的随机数参数
	 * @param url
	 * @param removeParam
	 * @return
	 */
	public static String removeUrlRandomParam(String url, String removeParam) {
		if(url.indexOf(removeParam+"=") > -1) {
			Map<String, String> paramMap = CrawlerCommonUtils.getParamMap(url);
			paramMap.remove(removeParam);
			String paramStr = Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
			return url.split("\\?")[0] + "?" + paramStr;
		} else {
			return url;
		}
	}
	
	/**
	 * 移除URL中的随机数参数
	 * @param url
	 * @param removeParam
	 * @return
	 */
	public static String replaceUrlParam(String url, String param, String value) {
		if(url.indexOf(param+"=") > -1) {
			Map<String, String> paramMap = CrawlerCommonUtils.getParamMap(url);
			paramMap.put(param, value);
			String paramStr = Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
			return url.split("\\?")[0] + "?" + paramStr;
		} else {
			return url;
		}
	}
	
	
	
	/**
	 * 解析url中的参数并返回
	 * @param url
	 * @return
	 */
	public static Map<String, String> getParamMap(String url) {
		Map<String, String> map = new HashMap<String, String>();
		String[] paramArr = url.split("\\?")[1].split("&");
		for(String str : paramArr) {
			try {
				if(str.split("=").length == 2) {
					map.put(str.split("=")[0], str.split("=")[1]);
				} else {
					map.put(str.split("=")[0], "");
				}
			} catch (Exception e) {
				System.out.println(url);
				e.printStackTrace();
			}
		}
		return map;
	}

	
	/**
	 * 去掉空格
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		str = StringUtils.trimToEmpty(str).
				replaceAll(" ", "").//英文空格
				replaceAll("\u00a0","").//去掉html中的nbsp;空格
				replaceAll("\r\n", "").//中文换行
				replaceAll("　", "").  //中文空格
				replaceAll("\n", "").  //tab回车
				replaceAll("\\s", "").trim();  //空格
		
		return str;
	}
		
	/**
	 * 解析onclick函数中的参数
	 * @param script
	 * @return
	 */
	public static Map<String, Object> getClickParam(String script) {

		Map<String, Object> map = new HashMap<String, Object>();
		if(script.contains("'")) {
			script = script.replaceAll("'", "");
		} else if(script.contains("\"")) {
			script = script.replaceAll("\"", "");
		} 
		String funName = script.substring(0, script.indexOf("("));
		String paramArr[] = script.substring(script.indexOf("(")+1,
				script.lastIndexOf(")")).split(",");
		map.put("funName", funName);
		map.put("paramArr", paramArr);
		return map;

	}


	/**
	 * 得到详细的异常信息
	 * @param e
	 * @return
	 */
	public static String getExceptionMessage(Exception e) {
		StringWriter sw=new StringWriter();  
        PrintWriter pw=new PrintWriter(sw);  
        e.printStackTrace(pw);
        IOUtils.closeQuietly(pw);
		IOUtils.closeQuietly(sw);
        return sw.toString();
	}
	
	public static String getRandom() {
		return new Random().nextInt(100)+"";
	}
	
	public static String getNumInStr(String str) {
	    Pattern p=Pattern.compile("(\\d+)");   
	    Matcher m=p.matcher(str);   
	    String result = "";
	    while(m.find()){
	    	result += m.group(); 
	    } 
	    return result;
	}
	
	/*public static void main(String[] args) {
		String str = "http://www.space.com/news/$page";
		String regex = "http://www.space.com/news/\\d/fas";
		//System.out.println(str.split("news/\\d")[0]);
		System.out.println(str.replaceAll("$page", "4"));
		System.out.println(match(regex, str));
	}*/
	
	public static void main(String[] args) {
		/*JSONObject json = new JSONObject();
		json.put("1", null);
		json.put("2", null);
		json.put("3", null);
		json.put("4", "  ");
		Collection<Object> list = json.values();
		System.out.println(list.toString());
		System.out.println(jsonValueIsAllNull(json));*/
		System.out.println(IsUrl("https://assets.cdn.spaceflightnow.com/wp-content/uploads/2017/05/25070040/electron_quick1-80x60.png"));
		//System.out.println(matchDateString("Techsir  2017年04月12日 12:46"));
	}
}
