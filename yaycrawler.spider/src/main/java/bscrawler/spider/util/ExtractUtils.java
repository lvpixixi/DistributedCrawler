package bscrawler.spider.util;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 页面抽取工具类
 * @author wwq
 *
 */
public class ExtractUtils {
	/**
	 * unicode转码
	 * @param ori
	 * @return
	 */
	public static String convertUnicode(String ori){
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
 
        }
        return outBuffer.toString();
	}
	
	/**
	 * 根据列表的两个XPath推算出列表XPath
	 * @param xpath1
	 * @param xpath2
	 * @return
	 */
	public static String getListXpath(String xpath1,String xpath2) {	
		String regex = "\\[\\d+\\]";
		Matcher m = Pattern.compile(regex).matcher(xpath1);
		Matcher m1 = Pattern.compile(regex).matcher(xpath2);
		while(m.find() && m1.find()) {
			String str1 = m.group(0);
			String str2 = m1.group(0);
			if(!str1.equals(str2)) {
				xpath1 = subStr(xpath1, m.start(), m.end());
				break;
			}
		}		
		return xpath1;
	}
	
	private static String subStr(String str, int begin, int end) {
		return str.substring(0, begin)+str.substring(end, str.length());
	}
	
	/**
	 * 去掉页面内容的a标签 <script>标签 及class,width,height等属性
	 * @param html
	 * @return
	 */
	public static String denoisHtml(String html, String... regArr) {
		
		if(ArrayUtils.isNotEmpty(regArr)) {
			html = html.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");
			String result = html;
			for(String regex : regArr) {
				if(!"".equals(StringUtils.trimToEmpty(regex))) {
					result = Pattern.compile(regex).matcher(result).replaceAll("");
				}
			}
			return result;
		} else {
			return html;
		}
	}
	
	/**
	 * 获取抽取的所有html内容
	 * @param sel
	 * @return
	 */
	public static String getAllString(Selectable sel) {
		List<String> list = sel.all();
		String result = "";
		for(int i=0; i<list.size(); i++) {
			if("".equals(result)) {
				result = list.get(i);
			} else {
				result += list.get(i) + "\r\n";
			}
		}
		return result;
	}
	
	/**
	 * 获取html内容
	 * @param html
	 * @return
	 */
	public static String getSmartContent(String html) {
		 html = html.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "");
		 html = html.replaceAll("<html.*?>|</html>", "");
		 html = html.replaceAll("<body.*?>|</body>", "");
		 html = html.replaceAll("<head.*?>|</head>", "");
		 html = html.replaceAll("<i>|</i>", "");
		 return html;
	}
	
	public static Selectable getAllSel(Selectable html) {
		String allStr = getAllString(html);
		return new Html(allStr);
	}
		
	public static void main(String[] args) {
		String str = FileUtils.fileToString(new File("E:\\mss\\crawler\\regexTest.html"));
		str = str.replaceAll("\n", "");
		System.out.println(str);
		//String regex = "\\{\"list\":\\[\\{.*?\\]\\}";
		String regex = "<!--.*?-->";
		Matcher m = Pattern.compile(regex).matcher(str);
		System.out.println(m.replaceAll(""));
		
		/*while(m.find()){
			System.out.println(m.group(0));
		}*/
		
	}
}
