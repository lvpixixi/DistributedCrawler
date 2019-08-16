package web_search;

import html_parsing.TagRemover;

import java.util.ArrayList;

import com.google.code.bing.search.client.BingSearchClient;
import com.google.code.bing.search.client.BingSearchClient.SearchRequestBuilder;
import com.google.code.bing.search.client.BingSearchServiceClientFactory;
import com.google.code.bing.search.schema.AdultOption;
import com.google.code.bing.search.schema.SearchOption;
import com.google.code.bing.search.schema.SearchResponse;
import com.google.code.bing.search.schema.SourceType;
import com.google.code.bing.search.schema.web.WebResult;
import com.google.code.bing.search.schema.web.WebSearchOption;

class SearchThread extends Thread {
	private ArrayList<String> d;
	private String u;

	public SearchThread(ArrayList<String> r, String url) {
		d = r;
		u = url;
	}

	public void run() {
		String html = HtmlDownloader.down(u);
		String tagsRmved = new TagRemover().parse(html);
		add(tagsRmved);
	}

	private synchronized void add(String tagsRmved) {
		d.add(tagsRmved);
	}
}

public class BingSearcher {
	final static String appid0 = "021353E473C1DD3300BE97611E253D233788DA0D";
	final static String appid1 = "C6E6EE9C70DB618863F6D295F37A4B597C3F1425";

	public static ArrayList<String> search(ArrayList<String> queries) {
		ArrayList<String> ret = new ArrayList<String>();

		String applicationId = appid1;
		BingSearchServiceClientFactory factory = BingSearchServiceClientFactory
				.newInstance();
		BingSearchClient client = factory.createBingSearchClient();

		SearchRequestBuilder builder = client.newSearchRequestBuilder();
		builder.withAppId(applicationId);
		StringBuilder qsb = new StringBuilder();
		for (String q : queries) {
			qsb.append(q);
			qsb.append(' ');
		}
		builder.withQuery(qsb.toString());
		builder.withSourceType(SourceType.WEB);
		builder.withVersion("2.0");
		builder.withMarket("en-us");
		builder.withAdultOption(AdultOption.OFF);
		builder.withSearchOption(SearchOption.ENABLE_HIGHLIGHTING);

		builder.withWebRequestCount(10L);
		builder.withWebRequestOffset(0L);
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_HOST_COLLAPSING);
		builder.withWebRequestSearchOption(WebSearchOption.DISABLE_QUERY_ALTERATIONS);

		SearchResponse response = client.search(builder.getResult());
		ArrayList<Thread> tlist = new ArrayList<>();
		for (WebResult result : response.getWeb().getResults()) {
			// System.out.println(result.getTitle());
			// System.out.println(result.getDescription());
			System.out.println(result.getUrl());
			new SearchThread(ret, result.getUrl()).run();
			//t.start();
			//tlist.add(t);
			// System.out.println(result.getDateTime());
		}
		for (Thread t : tlist) {
			try {
				t.join(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Interrupted, huh?");
			}
		}
		return ret;
	}
}
