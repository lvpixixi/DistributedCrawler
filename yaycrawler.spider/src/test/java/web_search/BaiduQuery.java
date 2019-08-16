package web_search;

import html_parsing.TagRemover;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

class SearchThreadb extends Thread {
	private FileWriter d;
	private String u;

	public SearchThreadb(FileWriter r, String url) {
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

public class BaiduQuery {
	public static void main(String[] args) {
		try {
			query("d:\\baiduQuery.txt", "操作系统是一种");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String queryHead = "http://www.baidu.com/baidu?wd=";
	static String queryTail = "&tn=monline_4_dg";
	static String findPattern = "\'})\" href=\"";

	static String mkQueryUrl(String keyword) {
		return queryHead + keyword + queryTail;
	}

	public static void query(String outputfile, String query)
			throws IOException {
		query = URLEncoder.encode(query, "utf-8");

		String queryUrl = mkQueryUrl(query);
		String html = HtmlDownloader.down(queryUrl);
		System.out.println(html);
		int find = html.indexOf(findPattern);

		FileWriter o = new FileWriter(outputfile);

		ArrayList<Thread> tList = new ArrayList<>();
		while (find != -1) {
			find += findPattern.length();
			int nextQ = html.indexOf('\"', find);
			String u = html.substring(find, nextQ);
			SearchThreadg thread = new SearchThreadg(o, u);
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
