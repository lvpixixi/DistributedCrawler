package bscrawler.spider.translate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import bscrawler.spider.util.ExtractUtils;
import bscrawler.spider.util.FileUtils;
import bscrawler.spider.util.HttpClientUtils;
import bscrawler.spider.util.ScriptUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

/**
  
* @ClassName: GoogleTranslate  
* @Description: TODO(谷歌翻译类)
* @author wwq
* @date 2017-6-9 下午2:48:21
*/
public class GoogleTranslate {
	
	private GoogleDownloader downloader = new GoogleDownloader();
	private Logger logger = LoggerFactory.getLogger(GoogleTranslate.class);
	private static Site site = Site.me();
	private static Map<String,String> headerMap = new HashMap<>();
	
	static {
		site.addHeader("accept", "*/*");
		site.addHeader("accept-encoding", "gzip, deflate, br");
		site.addHeader("accept-language", "zh-CN,zh;q=0.8");
		site.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
		site.addHeader("x-client-data", "CKi1yQEIi7bJAQiktskBCMS2yQEI/JnKAQj7nMoBCKmdygE=");
		site.addHeader("content-type","application/x-www-form-urlencoded");
	}
	
	public GoogleTranslate() {
		
	}
	
	public GoogleTranslate(int thread) {
		downloader.setThread(thread);
	}
	
	/**
	 * 读取文件夹中的文件(文本文档 txt )进行翻译
	 * @param sourcePath
	 * @param savePath
	 * @return
	 */
	public void readDir(String sourcePath, String savePath) {
		
		File file = FileUtils.getFile(sourcePath);
		
		File[] files = file.listFiles();
		
		for(File f : files) {
			
			JSONObject json = JSONObject.parseObject(FileUtils.fileToString(f));
			
			String result = translate(json.getString("content"));
			
			FileUtils.print(result, savePath + f.getName().split("\\.")[0]+".html", false);
		}
	}

	/**
	 * 双语对照翻译
	 * @param source
	 * @param sourceLang
	 * @param targetLang
	 * @return
	 */
	public String comparisonTranslate(String source, String sourceLang, String targetLang) {
		return null;
	}
	
	/**
	 * 翻译
	 * @param content
	 * @param sourceLang
	 * @param targetLang
	 * @return
	 */
	public String translate(String content, String sourceLang, String targetLang) {
		
		StringBuilder url = new StringBuilder("https://translate.googleapis.com/translate_a/t?anno=3&client=te_lib&format=html&v=1.0&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw&logld=vTE_20170501_01&sl=").
				append(sourceLang).append("&tl=").append(targetLang).append("&sp=nmt&tc=1&sr=1&tk=");
		
		String tk = getTk(content, getTKK());
		
		url.append(tk);
		
		url.append("&mode=1");
		
		Request req = HttpClientUtils.getPostRequest(url.toString(), new String[]{"q"}, new String[]{content});
	
		Page p;
		try {
			p = downloader.download(req, site.toTask());
			
			String result = ExtractUtils.convertUnicode(p.getRawText());
			
			return result;
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	/**
	 * 翻译方法
	 * @param content
	 * @return
	 */
	/**
	 * 翻译方法
	 * @param content
	 * @return
	 */
	public String translate(String content) {
		StringBuilder url = new StringBuilder("https://translate.googleapis.com/translate_a/t?anno=3&client=te_lib&format=html&v=1.0&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw&logld=vTE_20170501_01&sl=en&tl=zh-CN&sp=nmt&tc=1&sr=1&tk=");
			
		String tk = getTk(content, getTKK());

		url.append(tk);
		
		url.append("&mode=1");
		
		Request req = HttpClientUtils.getPostRequest(url.toString(), new String[]{"q"}, new String[]{content});
		for(Map.Entry<String,String> node:headerMap.entrySet()){
			((LocalRequest) req).addHeader(node.getKey(), node.getValue());
		}

		Page p;
		try {
			p = downloader.download(req, site.toTask());
			if(null != p) {
				String result = ExtractUtils.convertUnicode(p.getRawText());
				
//			JSONArray jsonArr = JSONArray.parseArray(result);
//			result = jsonArr.getString(0);
				
				result = result.substring(result.indexOf("\"")+1, result.length()-1);
				
				Document doc = Jsoup.parse(result);
				
				Elements eles = doc.getElementsByTag("b");
				
				for(Element ele : eles){
					Element preEle = ele.previousElementSibling();
					if(preEle == null) {
						ele.remove();
						continue;
					}
					
					if("i".equals(preEle.tagName())) {
						preEle.html(ele.html());
						ele.remove();
					}
					//System.out.println(ele);
				}
				
				//result = result.replaceAll("<b>", "<br><b>");
				
				//result = "<!doctype html><html lang=\"en\"> <head><meta charset=\"UTF-8\"><title>Document</title></head><body>"+result+"</body></html>";
				
				return ExtractUtils.getSmartContent(doc.html());
				
			} else {
				System.out.println("The request return the value null : " + req.toString());
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
	/**
	 * 获取tk
	 * @param content
	 * @param tkk
	 * @return
	 */
	private String getTk(String content, String tkk) {
		return ScriptUtils.execScript("tk", content, tkk)+"";
	}
	
	/**
	 * 获取tkk
	 * @return
	 */
	private String getTKK() {
		String script1 = "eval('((function(){var a\\x3d1605932948;var b\\x3d2450142413;return 415780+\\x27.\\x27+(a+b)})())');";
		return ScriptUtils.executeScript(script1)+"";
	}
	
	public static void main(String[] args) throws InterruptedException {
		GoogleTranslate translate = new GoogleTranslate();
		
		System.out.println("1 " + translate.translate("Clouds or Snow? Here Are a Few Ways to Tell the Difference"));
		System.out.println("2 " + translate.translate("Clouds or Snow? Here Are a Few Ways to Tell the Difference"));
		System.out.println("3 " + translate.translate("Clouds or Snow? Here Are a Few Ways to Tell the Difference"));
		System.out.println("4 " + translate.translate("Clouds or Snow? Here Are a Few Ways to Tell the Difference"));
	}
}
