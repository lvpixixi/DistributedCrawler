package bscrawler.spider;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;

import bscrawler.spider.download.Down2FtpComp;
import bscrawler.spider.download.IFtpConfig;
import bscrawler.spider.downloader.CrawlerHttpClientDownloader;
import bscrawler.spider.downloader.CrawlerPhantomJSDownloader;
import bscrawler.spider.model.CrawlerConfig;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.model.SpiderConf;
import bscrawler.spider.pipeline.File2FtpPipeline;
import bscrawler.spider.pipeline.File2LocalPipeline;
import bscrawler.spider.pipeline.Html2PdfPipeline;
import bscrawler.spider.pipeline.JSONPipeline;
import bscrawler.spider.pipeline.MongDBPipeline;
import bscrawler.spider.pipeline.MongoTranslateDBPipeline;
import bscrawler.spider.pipeline.NewsDBPipeline;
import bscrawler.spider.processor.BatchUrlModelPageProcessor;
import bscrawler.spider.processor.ListDetailPageProcessor;
import bscrawler.spider.processor.SearchModelPageProcessor;
import bscrawler.spider.processor.SubjectDiscoverPageProcessor;
import bscrawler.spider.processor.WeixinArticleModelPageProcessor;
import bscrawler.spider.processor.WeixinModelPageProcessor;
import bscrawler.spider.scheduler.CrawlerQueueScheduler;
import bscrawler.spider.util.MayiProxyUtil;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;


public class SpiderBuilder {
	private static Logger logger = LoggerFactory.getLogger(SpiderBuilder.class);
	private IPageSiteService pageSiteService = new DefaultPageSiteService();
	private MongoTemplate mongoTemplate;
	private Down2FtpComp down2FtpManager;
	
	public SpiderBuilder(MongoTemplate mongoTemplate,IFtpConfig config) {
		this.mongoTemplate = mongoTemplate;
		this.down2FtpManager = new Down2FtpComp(config);
	}
	
	private MayiProxyUtil mayiProxy = new MayiProxyUtil();
	
	public YaySpider builderByFile(String cfgFilePath) throws Exception{
		String json = FileUtils.readFileToString(new File(cfgFilePath));
		return builderByJson(json);
	}
	
	public YaySpider builderByJson(String json) throws Exception{
		CrawlerConfig rootCfg = JSONObject.parseObject(json, CrawlerConfig.class);
		return builderByBean(rootCfg);
	}
	public YaySpider builderByBean(CrawlerConfig rootCfg){
		
		SpiderConf spiderConf = rootCfg.getSpiderConf();
		this.pageSiteService.setConfig(spiderConf);
		Site site = this.pageSiteService.getSite(spiderConf.getDomain(), spiderConf.getProxy_type());
		//初始化页面解析器
		PageProcessor pageProcessor = iniPageProcessor(site,rootCfg);
		
		//初始化爬虫
		YaySpider spider = new YaySpider(rootCfg.getSpiderConf().getDomain(), pageSiteService, pageProcessor);//YaySpider.create(pageProcessor);
		spider.thread(spiderConf.getThread());
		spider.addUrl(spiderConf.getStartURL());
		
		//初始化管道
		List<Pipeline> pipelines = iniPipline(rootCfg);
		spider.setPipelines(pipelines);
		
		//初始化下载器
		Downloader downloader=iniDownLoader(rootCfg);
		spider.setDownloader(downloader);
		
		//初始化url队列
		CrawlerQueueScheduler scheduler = iniScheduler(rootCfg);
		if(scheduler!=null){
			spider.setScheduler(scheduler);
		}
		// 初始化爬虫所属的项目
		spider.setProject(rootCfg.getProject());
		
		return spider;
	}
	
	
	
	
	private CrawlerQueueScheduler iniScheduler(CrawlerConfig rootCfg) {
		CrawlerQueueScheduler scheduler = new CrawlerQueueScheduler();
		return scheduler;
	}

	private List<Pipeline> iniPipline(CrawlerConfig rootCfg){
		SpiderConf spiderConf = rootCfg.getSpiderConf();
		List<Pipeline> pipelines = new ArrayList<Pipeline>();
		if(rootCfg.getPipelines()!=null&&rootCfg.getPipelines().length>0){
			for(String pipeline:rootCfg.getPipelines()){
				if(SpiderConstants.PIPELINE_JSONPIPELINE.equals(pipeline)){
					pipelines.add(new JSONPipeline(spiderConf.getRootPath()));	
				}else if(SpiderConstants.PIPELINE_IMGPIPELINE.equals(pipeline)){
					pipelines.add(new File2LocalPipeline(spiderConf.getRootPath()));	
				}else if(SpiderConstants.PIPELINE_Html2PdfPipeline.equals(pipeline)){
					pipelines.add(new Html2PdfPipeline(spiderConf.getRootPath()));	
				}else if(SpiderConstants.PIPELINE_NEWSDBPIPELINE.equals(pipeline)){
					pipelines.add(new NewsDBPipeline());	
				}else if(SpiderConstants.PIPELINE_MongoDBPipeline.equals(pipeline)) {
					pipelines.add(new MongDBPipeline(this.mongoTemplate));
				}else if(SpiderConstants.PIPELINE_MongoTranslateDBPipeline.equals(pipeline)) {
					pipelines.add(new MongoTranslateDBPipeline(this.mongoTemplate));
				}else if(SpiderConstants.PIPELINE_FTPFilePipeline.equals(pipeline)) {
					pipelines.add(new File2FtpPipeline(this.down2FtpManager));
				}
			}
			
		//缺省初始化管道
		}else{
			//需要按照顺序进行
			pipelines.add(new MongDBPipeline(this.mongoTemplate));	
			//pipelines.add(new ExcelPipeline());	
			pipelines.add(new File2FtpPipeline(this.down2FtpManager));
		}
		return pipelines;
		
	}
	
	private Downloader iniDownLoader(CrawlerConfig rootCfg){
		String downloader = rootCfg.getDownloader();
		if("PhantomJsMockDonwnloader".equals(downloader)) {
			return new CrawlerPhantomJSDownloader();
		}else {
			return new CrawlerHttpClientDownloader();
		}
	}
	
	private PageProcessor iniPageProcessor(Site site,CrawlerConfig rootConf){

		PageProcessorConf pageProcessorConf = rootConf.getPageProcessorConf();
		PageProcessor pageProcessor = null;
		
		if(pageProcessorConf.getName().equalsIgnoreCase(SpiderConstants.WEIXIN_PAGE_PROCESSORMODEL)){
			pageProcessor = new WeixinModelPageProcessor(site,pageProcessorConf);
		}else if(pageProcessorConf.getName().equalsIgnoreCase(SpiderConstants.WEIXINARTICLE_PAGE_PROCESSORMODEL)){
			pageProcessor = new WeixinArticleModelPageProcessor(site,pageProcessorConf);
		}else if(pageProcessorConf.getName().equalsIgnoreCase(SpiderConstants.SEARCH_PAGE_PROCESSORMODEL)){
			pageProcessor = new SearchModelPageProcessor(site,pageProcessorConf);
		}else if(pageProcessorConf.getName().equalsIgnoreCase(SpiderConstants.LISTANDDETAIL_PAGE_PROCESSORMODEL)){
			pageProcessor = new ListDetailPageProcessor(site,pageProcessorConf);
		}else if(pageProcessorConf.getName().equalsIgnoreCase(SpiderConstants.SUBJECTDISCOVER_PROCESSORMODEL)){
			pageProcessor = new SubjectDiscoverPageProcessor(site,pageProcessorConf);
		}else if(pageProcessorConf.getName().equalsIgnoreCase(SpiderConstants.BATCHURLS_PAGE_PROCESSORMODEL)){
			pageProcessor = new BatchUrlModelPageProcessor(site,pageProcessorConf);
		}/*else if(Constants.SUBJECT_PAGE_PROCESSORMODEL.equals(config.getPageProcessorModel())){
			modelPageProcessor = new SubjectModelPageProcessor(site,config);
		}else if(Constants.WEIXIN_PAGE_PROCESSORMODEL.equals(config.getPageProcessorModel())){
			modelPageProcessor = new WeixinModelPageProcessor(site,config);
		}else if(Constants.LISTANDDETAIL_PAGE_PROCESSORMODEL.equals(config.getPageProcessorModel())){
			modelPageProcessor = new ListDetailPageProcessor(site,config);
		}else if(Constants.IPTEST_PAGE_PROCESSORMODEL.equals(config.getPageProcessorModel())){
			modelPageProcessor = new IPTestModelPageProcessor(site,config);
		}*/

        return pageProcessor;
		
	}
	
	private Site getSite(SpiderConf config){
		Site site = Site.me();
		site.setDomain(config.getDomain());
		site.setSleepTime(config.getSleep());
		site.setTimeOut(config.getTimeout());
		site.setRetryTimes(config.getRetry());
		site.setCharset(config.getCharset());
		
		/*if(StringUtils.isEmpty(config.getDomain()))
		site.addHeader("host", config.getDomain());*/
		
		if(StringUtils.isEmpty(config.getUserAgent())) {
			site.addHeader("User-Agent", config.getUserAgent());
		}
	    //处理header
	    Map<String, String> headMap = config.getHeaders();
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            site.addHeader(entry.getKey(), entry.getValue());
        }
	    
        //处理Cookies
        Map<String, String> cookiesMap = config.getCookies();
        for (Map.Entry<String, String> entry : cookiesMap.entrySet()) {
            site.addCookie(config.getDomain(), entry.getKey(), entry.getValue());
        }
        String proxyType = config.getProxy_type();
        HttpHost httpProxy;
        switch(proxyType) {
        	case SpiderConstants.PROXY_TYPE_LOCAL:
        		httpProxy = new HttpHost(config.getProxy_Host(), config.getProxy_port());
        		site.setHttpProxy(httpProxy);
        		break;
        	case SpiderConstants.PROXY_TYPE_MAYI:
        		httpProxy = new HttpHost(config.getProxy_Host(), config.getProxy_port());
        		site.setHttpProxy(httpProxy);
        		String authHeader = MayiProxyUtil.getAuthHeader(config.getProxy_user(), config.getProxy_password());
        		site.addHeader("Proxy-Authorization", authHeader);
        		break;
        	default:
        }
		return site;
	}
	private String getAuthHeader(String appkey,String secret){
		// 定义申请获得的appKey和appSecret
		//String appkey = "170799173";
		//String secret = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
		 
		// 创建参数表
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("app_key", appkey);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT+8"));//使用中国时间，以免时区不同导致认证错误
		paramMap.put("timestamp", format.format(new Date()));
		 
		// 对参数名进行排序
		String[] keyArray = paramMap.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		 
		// 拼接有序的参数名-值串
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(secret);
		for(String key : keyArray){
		    stringBuilder.append(key).append(paramMap.get(key));
		}
		     
		stringBuilder.append(secret);
		String codes = stringBuilder.toString();
		         
		// MD5编码并转为大写， 这里使用的是Apache codec
		String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(codes).toUpperCase();
		paramMap.put("sign", sign);
		// 拼装请求头Proxy-Authorization的值，这里使用 guava 进行map的拼接
		String authHeader = "MYH-AUTH-MD5 " + Joiner.on('&').withKeyValueSeparator("=").join(paramMap);
		return authHeader;
	}
	
	/*public static void main(String[] args) throws Exception {
		//微信采集
		//String cfgFilePath = "E:/product/YayCrawler/yaycrawler.spider/doc/weixin_batch.txt";
		String cfgFilePath = "E:/product/YayCrawler/yaycrawler.spider/doc/search_model_bak.txt";
		//外文采集
		//String cfgFilePath = "E:/源码/java/renren-fast/doc/crawlerSamples/listDetail.txt";
		//百度新闻采集
		//String cfgFilePath = "E:/源码/java/renren-fast/doc/crawlerConfigex/subject.txt";
		
		
		SpiderBuilder builder = new SpiderBuilder();
		YaySpider spider = builder.builderByFile(cfgFilePath);
		spider.run();
		//spider.start();
		
		
		
	}*/

}
