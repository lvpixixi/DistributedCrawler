package bscrawler.spider.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.utils.UrlUtils;

public class UrlParseUtils extends UrlUtils{
	
	/**
     * s从url中匹配虚拟目录（除去协议、域名、端口号和参数部分）     
     * @param url
     * @param containProjectName 是否去掉项目名
     * @return
     */
    public static String getURI(String url, boolean containProjectName) {
        String regx = "(?:https?://)(?:(?:\\w+\\.){2,3}|[a-zA-Z0-9]+)(?:\\w+)(?::[0-9]+)?" + (containProjectName ? "(?:/\\w+/)" : "")
                + "([^?]*)";
        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

	/**
	 * 获取完整的URL链接
	 * @param href
	 * @param url
	 * @return
	 */
	public static String getFullHref(String href, String url) {
		
		
		if(!href.startsWith("http")) {
			
			if (href.startsWith("//"+getDomain(url))) {
				return getHost(url).substring(0, getHost(url).indexOf(":")+1) + href;
			}
			
			if(href.indexOf("/") == -1) {
				return url.substring(0, url.lastIndexOf("/")+1)+href;
			}
			
			if(!href.startsWith("/")) {
				href = getHost(url)+"/"+href;
			} else {
				href = getHost(url)+href;
			}
		}
		return href;
	}
	
	public static String getFullUrl(String reqUrl,String fileUrl) {
		
		try {
			URL baseUri = new URL(reqUrl);
			URL absoluteUri =new URL(baseUri,fileUrl);
			return absoluteUri.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static boolean isURL(String str){
        //转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"  
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@  
               + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184  
                 + "|" // 允许IP和DOMAIN（域名） 
                 + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.  
                 + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名  
                + "[a-z]{2,6})" // first level domain- .com or .museum  
                + "(:[0-9]{1,4})?" // 端口- :80  
                + "((/?)|" // a slash isn't required if there is no file name  
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";  
        
        Pattern pattern =Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        if( !match.matches() ){
            return false;
        }
        return true;


    }
	
	/**
     * 将字符串text中由openToken和closeToken组成的占位符依次替换为args数组中的值
     * @param openToken
     * @param closeToken
     * @param text
     * @param args
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, Object... args) {
        if (args == null || args.length <= 0) {
            return text;
        }
        int argsIndex = 0;

        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;
        // search open token
        int start = text.indexOf(openToken, offset);
        if (start == -1) {
            return text;
        }
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    ///////////////////////////////////////仅仅修改了该else分支下的个别行代码////////////////////////

                    String value = (argsIndex <= args.length - 1) ?
                            (args[argsIndex] == null ? "" : args[argsIndex].toString()) : expression.toString();
                    builder.append(value);
                    offset = end + closeToken.length();
                    argsIndex++;
                    ////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    public static String parse0(String text, Object... args) {
        return parse("${", "}", text, args);
    }


    public static String parse1(String text, Object... args) {
        return parse("{", "}", text, args);
    }
    
    public static String getDomain(String urlStr){
		java.net.URL url;
		try {
			url = new  java.net.URL(urlStr);
			return url.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
		
    }

	
	public static void main(String[] args) {
		String reqUrl = "https://airbusdefenceandspace.com/newsroom/news-and-features/spacedatahighway-to-reach-asia-pacific/";
		String srcUrl = "/wp-content/uploads/2017/03/press-release-cis-08032017-edrs.png";
		
		System.out.println(isURL(reqUrl));
		System.out.println(isURL(srcUrl));
		System.out.println(getFullHref(srcUrl,reqUrl));
		System.out.println(getDomain(reqUrl));
		System.out.println(getDomain("http://www.noaa.gov"));
		
		System.out.println(parse0("我的名字是${},结果是${}，可信度是%${}", "雷锋", true, 100));
	}
}
