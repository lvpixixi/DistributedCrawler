package web_search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 我就是不写注释！
 * 
 * @author Administrator
 * 
 */
public class HtmlDownloader {
	public static void main(String[] args) {
		System.out
				.println(down("http://www.baidu.com/s?wd=runtime.exec%20%B5%C8%B4%FD%CD%CB%B3%F6&pn=90&tn=monline_4_dg"));
	}

	public static String down(String u) {
		return new Helper().fuck(u);
	}

}

class Helper {
	public String fuck(String u) {
		String[] p = new String[2];
		p[0] = u;
		p[1] = "";
		Thread thread = new T(p);
		thread.start();
		synchronized (this) {
			try {
				int count = 100;
				while (count-- > 0) {
					this.wait(100);
					if (!thread.isAlive())
						break;
				}
			} catch (Exception e) {
			}
			if (thread.isAlive())
				thread.interrupt();
		}
		return p[1];
	}
}

class T extends Thread {
	static String user_agent_str = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:9.0) Gecko/20100101 Firefox/9.0";
	String[] l;

	public T(String[] p) {
		l = p;
	}

	public void run() {
		String fu = l[0];
		try {
			StringBuilder ret = new StringBuilder();
			int HttpResult;
			URL url = new URL(fu);
			HttpURLConnection urlconn = (HttpURLConnection) url
					.openConnection(); // ������
			// URLConnection
			// ��������ĳ��࣬����Ӧ�ó����
			// URL
			// ֮���ͨ�����ӣ�ͨ����
			// URL
			// �ϵ���
			// openConnection
			// �����������Ӷ���
			//urlconn.addRequestProperty("Accept-Language", "zh-cn");
			urlconn.addRequestProperty("User-Agent", user_agent_str);

			urlconn.setFollowRedirects(true);
			urlconn.setInstanceFollowRedirects(false);

			urlconn.connect(); // ʹ�� connect ����������Զ�̶����ʵ������
			HttpURLConnection httpconn = (HttpURLConnection) urlconn; // ÿ��
																		// HttpURLConnection
																		// ʵ���������ɵ������󣬵�������ʵ�����͸���ع������ӵ�
																		// HTTP
																		// �������Ļ�����

			// HttpResult = httpconn.getResponseCode(); // getResponseCode���Դ�
			// HTTP
			// ��Ӧ��Ϣ��ȡ״̬��

			while (String.valueOf(httpconn.getResponseCode()).startsWith("3")) {
				String theLocation = httpconn.getHeaderField("Location");
				httpconn.disconnect();
				url = new URL(theLocation);
				httpconn = (HttpURLConnection) url.openConnection();
				httpconn.setFollowRedirects(true);
				httpconn.setInstanceFollowRedirects(false);
				httpconn.connect();
			}
			if (httpconn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("HTTP error code : " + httpconn.getResponseCode() + " from " + fu);
			} else {
				String charset = httpconn.getContentType();
				// System.out.println(charset);
				String charType = charset
						.substring(charset.lastIndexOf("=") + 1);
				// System.out.println(charType);
				if ("text/html".equals(charType))
					charType = "gbk";
				InputStreamReader isr = null;
				try {
					isr = new InputStreamReader(httpconn.getInputStream(),
							charType);
				} catch (UnsupportedEncodingException e) {
					System.out.println(e.getMessage() + fu);
					isr = new InputStreamReader(httpconn.getInputStream(),
							"gbk");
				}
				BufferedReader in = new BufferedReader(isr);
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					ret.append(inputLine);
				}
				in.close();
			}
			l[1] = ret.toString();
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage() + fu);
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage() + fu);
			return;
		}
	}

}
