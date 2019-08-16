package bscrawler.spider.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;

public class CrawlerConfig {
	// 解决 JsonConfig 的 bug
	static{  
		ParserConfig.getGlobalInstance().setAsmEnable(false);  
	} 
	
	/**
	 * 爬虫标识
	 */
	private String id;
	
	/**
	 * 采集器配置
	 */
	private SpiderConf spiderConf;
	
	/**
	 * 页面解析模型配置
	 */
	private PageProcessorConf pageProcessorConf;

	/**
	 * 管道列表
	 */
	private String[] pipelines;
	
	/**
	 * 下载器
	 */
	private String downloader;
	
	/**
	 * 所属的项目
	 */
	private String project;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public static CrawlerConfig create(File file) throws IOException {
		String json = FileUtils.readFileToString(file);
		CrawlerConfig cfg = (CrawlerConfig) JSONObject.parseObject(json, CrawlerConfig.class);
		return cfg;
	}
	public static CrawlerConfig create(String json) {
		json = formatJson(json);
		CrawlerConfig cfg = (CrawlerConfig) JSONObject.parseObject(json, CrawlerConfig.class);
		return cfg;
	}
	
	private static String formatJson(String json) {
		if(json.contains("&lt;")){ 
			json = json.replaceAll("&lt;", "<");
		}
		if(json.contains("&gt;")) {
			json = json.replaceAll("&gt;", ">");
			
		}
		return json;
	}

	public String[] getPipelines() {
		return pipelines;
	}

	public void setPipelines(String[] pipelines) {
		this.pipelines = pipelines;
	}
	
	public SpiderConf getSpiderConf() {
		return spiderConf;
	}

	public void setSpiderConf(SpiderConf spiderConf) {
		this.spiderConf = spiderConf;
	}

	public PageProcessorConf getPageProcessorConf() {
		return pageProcessorConf;
	}

	public void setPageProcessorConf(PageProcessorConf pageProcessorConf) {
		this.pageProcessorConf = pageProcessorConf;
	}
	
	public String getDownloader() {
		return downloader;
	}

	public void setDownloader(String downloader) {
		this.downloader = downloader;
	}
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	public static CrawlerConfig fromJSON(String json) {
		CrawlerConfig cfg = (CrawlerConfig) JSONObject.parseObject(json, CrawlerConfig.class);
		return cfg;
	}
	
	public static CrawlerConfig fromFile(String cfgFilePath) throws IOException {
		String json = FileUtils.readFileToString(new File(cfgFilePath));
		CrawlerConfig cfg = (CrawlerConfig) JSONObject.parseObject(json, CrawlerConfig.class);
		return cfg;
	}
	
	public static void main(String[] args) throws IOException{
		
		String cfgFilePath = "D:/workspace/product/BSCrawler/doc/crawlerConfigex/cnDetail_sinamil.txt";
		String json = FileUtils.readFileToString(new File(cfgFilePath));
		CrawlerConfig cfg = (CrawlerConfig) JSONObject.parseObject(json, CrawlerConfig.class);
	}

}
