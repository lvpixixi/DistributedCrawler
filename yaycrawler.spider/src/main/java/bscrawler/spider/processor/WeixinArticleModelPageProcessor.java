package bscrawler.spider.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.listener.IPageParseListener;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.util.HttpClientPoolUtil;
import bscrawler.spider.util.HttpFileUtil;
import bscrawler.spider.util.YzmUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * json模型的页面解析处理器
 * 支持json配置文件的页面解析处理器
 * @author wangdw
 *
 */
public class WeixinArticleModelPageProcessor extends BasePageProcessor implements PageProcessor{
	
	protected Logger logger = LoggerFactory.getLogger(WeixinArticleModelPageProcessor.class);
	

    
    public final static String LIST_OBJECT_LABEL = "listObject";
    
    public final static String CHATNUMBER_OBJECT_LABEL = "chatNumberObject";
    
    public final static String TITLE_LABEL = "title";
    
    public final static String DATE_LABEL = "pubdate";
    
    private int pages=1;
    
    private String weixinMpRootUrl = "http://mp.weixin.qq.com/mp";
    
    private String jsonRegex = "\\{\"list\":\\[\\{.*?\\]\\}";
    
    @Autowired(required = false)
    private IPageParseListener pageParseListener;
    
    /**
     * 过滤垃圾广告
     */
    private Map<String,String> excludeExpressions;
    
    //private String jsonPath = "$.list[*].app_msg_ext_info,$.list[*].app_msg_ext_info.multi_app_msg_item_list[*]";
	
	//private String seedRex ="http://weixin.sogou.com/weixin?type=${type}&s_from=input&ie=utf8&query=${query}";
	
	private int searchType=SEARCH_ACCOUNT;	
	
	private String authCodePath = "c:/";
	
	//爬取文章类型
	public static final int SEARCH_ARTICLE = 2 ;
	//爬取公众号类型
	public static final int SEARCH_ACCOUNT = 1 ;

    public WeixinArticleModelPageProcessor(Site site ,PageProcessorConf rootCfg) {
        this.site = site;
        Map<String,Object> wxConfig = rootCfg.getMapConf();
        if(wxConfig.containsKey("weixinMpRootUrl"))
        	weixinMpRootUrl = MapUtils.getString(wxConfig, "weixinMpRootUrl");   
        if(wxConfig.containsKey("jsonRegex"))
        	jsonRegex = MapUtils.getString(wxConfig, "jsonRegex"); 
        if(wxConfig.containsKey("authCodePath"))
        	authCodePath = MapUtils.getString(wxConfig, "authCodePath"); 
        if(wxConfig.containsKey("pages"))
        	pages = MapUtils.getInteger(wxConfig, "pages"); 
        
        if(rootCfg.getExcludeExpressions()!=null){
        	this.excludeExpressions = rootCfg.getExcludeExpressions();
        }
        
        this.iniExtractors(rootCfg.getExtractors());
    }
    
    public void iniExtractors(List jarray) {
    	extractors = new HashSet<ObjectModelExtractor>() ;
    	for(int i=0;i<jarray.size();i++){
    		JSONObject json = (JSONObject)jarray.get(i);
    		ObjectModelExtractor extractor = JSONObject.toJavaObject(json, ObjectModelExtractor.class);
    		extractors.add(extractor);
    	}
 
    }

    @Override
	public void process(Page page,List<SpiderListener> listeners) {
    	
    	//获取页面抽取类型		
		Request req = page.getRequest();
		Object type = req.getExtra(TYPE_LABEL);		
		Html html = page.getHtml();
		//处理分页
    	if(type==null){
    		processPage(page);
    	//处理公众号列表
    	}else if("list".equals(type)){
    		if(page.getHtml().xpath("//title/text()").get().indexOf("请输入验证码") > -1) {
				 page = crackAuthCode(page);
			}
			parseSeed(page);
		//处理文章页
		}else if("article".equals(type)){
			
			//设置运行环境变量
	    	//Map<String,Object> context = new HashMap<String,Object>();
	    	//context.put("domain", site.getDomain());
	    	List<String> objectNames = new ArrayList<String>();
	    	page.putField("rawText", page.getRawText());
	    	page.putField("domain", site.getDomain());
	    	page.putField(LIST_OBJECT_LABEL, req.getExtra(LIST_OBJECT_LABEL));
	    	page.putField(TITLE_LABEL, req.getExtra(TITLE_LABEL));
	    	
	    	for (ObjectModelExtractor extractor : extractors) {
	    		//详情页抽取处理
	    		if(extractor.getObjName().equals("weixin")||extractor.getObjName().equals("weixin_image")){
		             //解析页面
		    		 Map<String,Object> process = (Map<String,Object>) extractor.getObject(page,Lists.newArrayList(),Lists.newArrayList());
		             if (process == null || (process instanceof List && ((List) process).size() == 0)) {
		                 continue;
		             }
		             objectNames.add(extractor.getObjName());
		            
		             //数据类型是信息，处理信息
		             if(extractor.getDataType().equalsIgnoreCase("MAP")){
		            	 //整合列表页和详情页的信息
			             if(process instanceof Map){
			            	 process = addExtInfo(page,process,(Map)req.getExtra(LIST_OBJECT_LABEL),(Map<String,Object>)req.getExtra(CHATNUMBER_OBJECT_LABEL),site.getDomain());
			             }
			             //数据类型是图片处理图片
			             processResource(page,process,this.getSite().getDomain());
		             }
		             printMap(process);
		             page.putField(extractor.getObjName(), process);
	    		}
	           
	         }
	         
	         if(objectNames.size()>0){
	         	 page.putField("objectNames", objectNames);
	         }
		}
        
	}
    private void processPage(Page page) {
    	String seedUrl = page.getUrl().toString();
		for(int i=1;i<=this.pages;i++){
			Request newSeed = this.createReq(seedUrl+"&page="+i, "list",null, null, null);
			page.addTargetRequest(newSeed);
		}
		
	}

	private Request createReq(String seedUrl,String type,String json, String title, String date){
    	Request newSeed = new Request(seedUrl);
		HashMap extras = new HashMap();
		extras.put(TYPE_LABEL, type);
		extras.put(TITLE_LABEL, title);
		extras.put(DATE_LABEL, date);
		if(!StringUtils.isEmpty(json)){
			extras.put(LIST_OBJECT_LABEL, json);
		}
		newSeed.setExtras(extras);
		return newSeed;
    }
    
    private Map addExtInfo(Page page,Map detailMa,Map json,Map<String,Object> chatNumberInfo,String domain){
    	if(json!=null){
    		detailMa.putAll(json);
    	}
    	
    	if(chatNumberInfo!=null){
    		detailMa.putAll(chatNumberInfo);
    	}
    	return detailMa;
    }
    

  	
  	/**
  	 * 抽取文章详情页链接信息和列表信息
  	 * @param page
  	 * @param articles
  	 */
  	private void parseSeed(Page page){
  		 // 解析所有的文章信息
  		List<Selectable> list = page.getHtml().xpath("//ul[@class='news-list']/li").nodes();
		for(int i=0; i<list.size(); i++) {
			
			Selectable article = list.get(i);
			Map<String,Object> listObj = new HashMap<>();
			
			String title = article.xpath("//div[@class='txt-box']/h3/a/text()").get();
			String pubDate=article.xpath("//div[@class='txt-box']/div/span/script").get();
			String chatNumber = article.xpath("//div[@class='txt-box']/div/a/text()").get();
			String cover = article.xpath("//div[@class='img-box']/a/img/@src").get();
			String summary=article.xpath("//div[@class='txt-box']/p/text()").get();
			String articleUrl=article.xpath("//div[@class='txt-box']/h3/a/@href").get();
			
			listObj.put("title", title);
			listObj.put("pubDate", parseDateTime2PubDate(pubDate));
			listObj.put("chatNumber", chatNumber);
			listObj.put("cover", cover);
			listObj.put("summary", summary);
			Request newSeed = this.createReq(articleUrl, "article",null, null, null);
			
			this.printMap(listObj);
        	newSeed.putExtra(LIST_OBJECT_LABEL, listObj);
        	//newSeed.putExtra(CHATNUMBER_OBJECT_LABEL, chatNumberInfo);
			page.addTargetRequest(newSeed);
			
		}
  	}

  	// 解析微信的请求参数中的  datetime 转化为 pubdate 字段
  	private String parseDateTime2PubDate(String datetimeStr) {
    	// 从内容上截取路径数组
    	//Pattern pattern = Pattern.compile("(?<=\\')[^\\']+");  
    	Pattern pattern = Pattern.compile("(?<=\\(\\')[^\\'\\)]+");  
    	
    	Matcher matcher = pattern.matcher(datetimeStr);
    	while(matcher.find()){
    	   return matcher.group();
    	}
    	return "";
	}

	/**
	 * 破解验证码
	 * @param page
	 */
	private synchronized Page crackAuthCode(Page page) {
		
		if(page.getHtml().xpath("//title/text()").get().indexOf("请输入验证码") > -1) {
			//生成验证码图谱地址
			String codeUrl = weixinMpRootUrl+"/verifycode?cert=" + (System.currentTimeMillis()+Math.random());
			String filePath = authCodePath + "/authcode/" + DigestUtils.md5Hex(codeUrl)+".jpg";
			
			try {
				
				//下载验证码图片
				List<Cookie> cookieL = HttpFileUtil.getInstance().getFileAndCookieTo(codeUrl, filePath,new String[]{"sig"});
				for(Cookie cookie:cookieL){
					System.out.println(cookie.getName()+"="+cookie.getValue());
				}
				JSONObject json = YzmUtils.getAuthCode(filePath, "1004");
				if(json != null && !json.isEmpty()) {
					/*Page authCodePage = this.spider.download(CrawlerCommonUtils.getPostRequest("http://mp.weixin.qq.com/mp/verifycode", new String[]{"cert", "input"}, 
							new String[]{(System.currentTimeMillis()+Math.random()) + "", json.getString("pic_str")}));
					*/
					Map<String,Object> params = new HashMap<>();
					params.put("input", json.getString("pic_str"));
					String rspJson = HttpClientPoolUtil.httpPostRequest(codeUrl, params, cookieL, null);
					JSONObject resultJson = JSONObject.parseObject(rspJson);
					logger.info("verifycode json is " + resultJson);
					if("0".equals(resultJson.get("ret")+"")) { //验证通过
						logger.info("authCode is cracked!");
						String seedUrl = page.getRequest().getUrl()+System.currentTimeMillis()+Math.random();
						Request newSeed = this.createReq(seedUrl, "article_list",null, null, null);
						for(Cookie cookie:cookieL){
							//newSeed.addCookie(cookie.getName(), cookie.getValue());
						}
						
						page.addTargetRequest(newSeed);
					} else {
						YzmUtils.reportError(json.getString("pic_id"));
						//将搜索词放入队列中重新爬取
						logger.error("verifycode fail " + resultJson);		
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		return page;
	}
	
	/**
     * 资源字段处理
     * @param page
     * @param html
     * @param resExtractor
     * @return
     */
    private void processResource(Page page,Map<String,Object> process,String domain){
    	
    	String filePath = "/res";
    	List<ResourceFile> resources = new ArrayList<ResourceFile>();
    	
    	String coverUrl = MapUtils.getString(process, "cover");
    	ResourceFile coverFile = new ResourceFile(filePath,coverUrl,domain);
    	coverUrl = coverFile.getRelativeUrl();
    	//处理封页图片路径
    	process.put("cover", coverUrl);
    	resources.add(coverFile);
    			
    	/*String avatarUrl = MapUtils.getString(process, "avatar");
    	ResourceFile avatarFile = new ResourceFile(filePath,avatarUrl,domain);
    	avatarUrl = avatarFile.getRelativeUrl();
    	//处理微信公众号图片路径
		process.put("avatar", avatarUrl);
		resources.add(avatarFile);*/
    	
    	String resultHtml = MapUtils.getString(process, "content");
    	List<String> imageUrls = (List<String>)process.get("images");
		for(String resUrl:imageUrls){
			if(!StringUtils.isEmpty(resUrl)){
				// 根据绝对路径下载对应的 附件资源
				ResourceFile rf = new ResourceFile(filePath,resUrl,domain);
				resources.add(rf);
				// 将 content 中的图片的路径 即 src 属性替换为本地的图片的路径
				resultHtml = StringUtils.replace(resultHtml, resUrl, rf.getRelativeUrl());
			}
			
		}
		//处理内容页图片路径
		process.put("content", resultHtml);
	
		
		List<ResourceFile> oldResources = (List<ResourceFile>)page.getResultItems().get("resources");
		if(oldResources!=null&&oldResources.size()>0){
			oldResources.addAll(resources);
		}else{
			page.putField("resources", resources);	
		}
		
    }
}
