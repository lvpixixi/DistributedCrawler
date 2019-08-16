package yaycrawler.spider;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class XpathTest {

	public static void main(String[] args) throws IOException {
		Connection conn = Jsoup.connect("http://www.81.cn/jmywyl/2019-01/29/content_9416195_2.htm");
		Document doc = conn.get();
		Html html = new Html(doc.html());
		Selectable selectable = html.xpath("//div[@id='article-content']/**/img/@src|//div[@id='article-content']/**/**/img/@src");
		System.out.println(selectable);
		
	}

}
