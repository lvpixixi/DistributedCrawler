package bscrawler.spider.util;

import org.jsoup.nodes.Document;

import us.codecraft.webmagic.selector.Html;

public class HtmlUtils {

	
	
	
	/**
	 * 替换HTML中某个标签的属性值
	 * @param html
	 * @param tagName
	 * @param attrName
	 * @param replaceValue
	 * @return
	 */
	public static String replaceHtmlAttrValue(String html,String tagName, 
			String attrName, String replaceValue) {
		Document doc = new Html(html).getDocument();
		doc.getElementsByTag("img").get(0).attr("src", replaceValue);
		return new Html(doc).xpath("//body/*").toString();
	}
	
	public static void main(String[] args) {
		/*String str = FileUtils.fileToString(new File("D:\\data\\test.txt"));
		JSONObject json = JSONObject.parseObject(str);
		String newsContent = json.getString("newsContent");
		System.out.println(denoisHtml(newsContent));*/
	}
}
