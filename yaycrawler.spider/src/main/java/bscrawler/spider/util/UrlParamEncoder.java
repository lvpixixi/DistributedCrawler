package bscrawler.spider.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlParamEncoder {

	/**
	 * 对URL的参数进行编码
	 * @param url
	 * @param charset
	 * @return
	 */
	public static String encodeURLParam(String url) {
		if(url.indexOf("?") == -1) {
			return url;
		}
		if(url.split("\\?").length == 1) {
			return url;
		}
		String urlParam = url.split("\\?")[1];
		try {
			urlParam = URLEncoder.encode(urlParam, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url.split("\\?")[0] + "?" + urlParam;
	}
}
