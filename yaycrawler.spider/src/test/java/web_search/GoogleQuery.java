package web_search;

import html_parsing.TagRemover;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

class SearchThreadg extends Thread {
	private FileWriter d;
	private String u;

	public SearchThreadg(FileWriter r, String url) {
		d = r;
		u = url;
	}

	public void run() {
		String html = HtmlDownloader.down(u);
		String tagsRmved = new TagRemover().parse(html);
		add(tagsRmved);
	}

	private void add(String tagsRmved) {
		try {
			synchronized (d) {
				d.write(tagsRmved);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public class GoogleQuery {
	public static void main(String[] args) {
		try {
			query("googleQuery.txt", "程序是一种");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String queryHead = "http://www.google.com.hk/search?q=";
	static String queryTail = "&ie=utf-8&oe=utf-8&aq=t&rls=org.mozilla:zh-CN:official&client=firefox-a";
	static String findPattern = "<h3 class=\"r\"><a href=\"";

	static String mkQueryUrl(String keyword) {
		return queryHead + keyword + queryTail;
	}

	public static void query(String outputfile, String query)
			throws IOException {
		//query = URLEncoder.encode(query, "utf-8");

		String queryUrl = mkQueryUrl(query);
		String html = HtmlDownloader.down(queryUrl);
		int find = html.indexOf(findPattern);

		FileWriter o = new FileWriter(outputfile);

		ArrayList<Thread> tList = new ArrayList<>();
		while (find != -1) {
			find += findPattern.length();
			int nextQ = html.indexOf('\"', find);
			String u = html.substring(find, nextQ);
			SearchThreadb thread = new SearchThreadb(o, u);
			tList.add(thread);
			thread.start();
			find = html.indexOf(findPattern, find);
		}

		for (Thread t : tList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				System.out.println("Interrupted, huh?");
			}
		}
		o.close();
	}
}
