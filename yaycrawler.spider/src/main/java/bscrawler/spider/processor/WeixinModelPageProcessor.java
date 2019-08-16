package bscrawler.spider.processor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import bscrawler.spider.SpiderConstants;
import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.util.HtmlFormatter;
import bscrawler.spider.util.HttpClientPoolUtil;
import bscrawler.spider.util.HttpFileUtil;
import bscrawler.spider.util.UrlParseUtils;
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
public class WeixinModelPageProcessor extends BasePageProcessor implements PageProcessor{
	
	protected Logger logger = LoggerFactory.getLogger(WeixinModelPageProcessor.class);
	
    public final static String TYPE_LABEL = "type";
    
    public final static String LIST_OBJECT_LABEL = "listObject";
    
    public final static String CHATNUMBER_OBJECT_LABEL = "chatNumberObject";
    
    public final static String TITLE_LABEL = "title";
    
    public final static String DATE_LABEL = "pubdate";
    
    private String weixinMpRootUrl = "http://mp.weixin.qq.com/mp";
    
    private String baseUrl = "http://localhost";
    
    private String jsonRegex = "\\{\"list\":\\[\\{.*?\\]\\}";
    
    /**
     * a过滤垃圾广告
     */
    private Map<String,String> excludeExpressions;
	
	private String authCodePath = "c:/";
	
	//爬取文章类型
	public static final int SEARCH_ARTICLE = 2 ;
	//爬取公众号类型
	public static final int SEARCH_ACCOUNT = 1 ;

    public WeixinModelPageProcessor(Site site ,PageProcessorConf rootCfg) {
        this.site = site;
        Map<String,Object> wxConfig = rootCfg.getMapConf();
        if(wxConfig.containsKey("weixinMpRootUrl"))
        weixinMpRootUrl = MapUtils.getString(wxConfig, "weixinMpRootUrl");   
        if(wxConfig.containsKey("jsonRegex"))
        jsonRegex = MapUtils.getString(wxConfig, "jsonRegex"); 
        if(wxConfig.containsKey("authCodePath"))
        authCodePath = MapUtils.getString(wxConfig, "authCodePath"); 
        
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
		//处理公众号列表
    	if(type==null){
			List<Selectable> links = html.xpath("//div[@id='main']/div[@class='news-box']/ul/li").nodes();	
			if(links.size()!=0){
				String seedUrl = links.get(0).xpath("//li/div/div/a/@href").get();
				Request newSeed = this.createReq(seedUrl, "article_list",null, null, null);
				page.addTargetRequest(newSeed);
			}
						
		//处理文章列表
		}else if("article_list".equals(type)){
			if(page.getHtml().xpath("//title/text()").get().indexOf("请输入验证码") > -1) {
				 page = crackAuthCode(page);
			}
	
			List<JSONObject> articles = parseArticleList(page);
			Map<String,Object> chatNumberInfo = parseChatNumberInfo(page);
			parseSeed(page, articles,chatNumberInfo);
			
		//处理文章页
		}else if("article".equals(type)){
			
			//设置运行环境变量
	    	List<String> objectNames = new ArrayList<String>();
	    	page.putField("domain", site.getDomain());
	    	page.putField("rawText", page.getRawText());
	    	page.putField(LIST_OBJECT_LABEL, req.getExtra(LIST_OBJECT_LABEL));
	    	page.putField(TITLE_LABEL, req.getExtra(TITLE_LABEL));
	    	
	    	for (ObjectModelExtractor extractor : extractors) {
	    		//详情页抽取处理
	    		if(extractor.getObjName().equals("weixin")){
	    			
	    			 List<ResourceFile> res = Lists.newArrayList();
		             //解析页面
		    		 Object resultObj = extractor.getObject(page, Lists.newArrayList(),res);
		             if (resultObj == null || (resultObj instanceof List && ((List) resultObj).size() == 0)) {
		                 continue;
		             }
		             objectNames.add(extractor.getObjName());
		             
	            	 //合并列表页和详情页的信息
		             if(resultObj instanceof Map) {
		            	 Map<String,Object> process = (Map)resultObj;
			             if(req.getExtra(LIST_OBJECT_LABEL)!=null&& req.getExtra(CHATNUMBER_OBJECT_LABEL)!=null){
			            	 process = addExtInfo(page,process,(JSONObject)req.getExtra(LIST_OBJECT_LABEL),(Map<String,Object>)req.getExtra(CHATNUMBER_OBJECT_LABEL),site.getDomain());
			             }
			             
			             
			             //处理下载资源信息
			             this.processResource(page, process, site.getDomain());
			             //生成唯一主键，防止记录重复
			             process.put("_id", this.generatorId(extractor.getPks(),process));
			             page.putField(extractor.getObjName(), process);
		             //}else {
		            	 //page.putField(extractor.getObjName(), resultObj);
		             }
		            
		             
	    		}
	           
	         }
	         
	         if(objectNames.size()>0){
	         	 page.putField("objectNames", objectNames);
	         }
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
    
    private Map addExtInfo(Page page,Map detailMa,JSONObject json,Map<String,Object> chatNumberInfo,String domain){
    	detailMa.put("author", json.get("author"));
    	detailMa.put("title", json.get("title"));
    	detailMa.put("digest", json.get("digest"));
    	detailMa.put("fileid", json.get("fileid"));
    	detailMa.put("pubDate", json.get("pubDate"));
    	detailMa.put("copyright", json.get("copyright_stat"));
    	detailMa.put("weixin_tmp_url", json.get("weixin_tmp_url"));
    	detailMa.put("cover", json.getString("cover"));
    	detailMa.put("pdffiles", "/" + domain + "/");
    	detailMa.putAll(chatNumberInfo);
    	//printMap(detailMa);
    	return detailMa;
    }
    
    
  	/**
  	 * 解析列表頁的url
  	 * @param page
  	 */
	private List<JSONObject> parseArticleList(Page page){
        String jsonStr = page.getHtml().regex(jsonRegex).get();
        JSONObject json;
        JSONArray articleJSONArray;
        List<JSONObject> articles = new ArrayList<>();
        String temp = "";
        if(!StringUtils.isEmpty(jsonStr)){
			try {
				
				json = JSONObject.parseObject(jsonStr);
				articleJSONArray = json.getJSONArray("list");
		        for (int i = 0; i < articleJSONArray.size(); i++) {
		            JSONObject articleJSON = articleJSONArray.getJSONObject(i).getJSONObject("app_msg_ext_info");
		            // 获取到对应的 JSON 串中的时间戳, 添加发布时间 即 pubDate 字段到对应的 articleJSON 中
		            Integer datetime = (Integer) articleJSONArray.getJSONObject(i).getJSONObject("comm_msg_info").get("datetime");
		            String pubdate = parseDateTime2PubDate(datetime);
		            String moreArticleJSONs = articleJSON.getString("multi_app_msg_item_list");
		            //String title = articleJSON.getString("title").trim();
		            //String key = account + "_" + title;
		            articleJSON.put("pubDate", pubdate);
		            articles.add(articleJSON);
		            if(null != moreArticleJSONs && !moreArticleJSONs.isEmpty()) {
		            	String[] splits = moreArticleJSONs.split("}");
		            	for(String split: splits) {
		            		temp = getTempStr(split);
		            		if(temp != null && temp.length() > 1) {
		            			JSONObject jsonObject = JSONObject.parseObject(temp + "}");
		            			jsonObject.put("pubDate", pubdate);
		            			articles.add(jsonObject);
		            		}
		            	}
		            }
		        }
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
		return articles;
        
	}
	
	/**
	 * 抽取列表页公众号信息
	 * @param page
	 */
  	private Map<String,Object> parseChatNumberInfo(Page page){
  		
  		for (ObjectModelExtractor extractor : extractors) {
    		//详情页抽取处理
    		if(extractor.getObjName().equals("weixin_list")){
	             //解析页面
	    		 Map<String,Object> result = (Map<String,Object>) extractor.getObject(page,Lists.newArrayList(),Lists.newArrayList());
	            
	    		 if (result != null) {
                     result.put("dataType", extractor.getDataType());
                 }
	             return result;
    		}
           
        }
  		
  		return null;
  		
  	}
  	
  	/**
  	 * 抽取文章详情页链接信息和列表信息
  	 * @param page
  	 * @param articles
  	 */
  	private void parseSeed(Page page,List<JSONObject> articles,Map<String,Object> chatNumberInfo){
  		 // 解析所有的文章信息
        for(JSONObject obj: articles) {
        	String articleUrl = "";
        	if(obj.getString("content_url").startsWith("http")) {
        		articleUrl =  obj.getString("content_url").replace("&amp;", "&");
        	} else {
        		articleUrl = "https://mp.weixin.qq.com" + obj.getString("content_url").replace("&amp;", "&");
        	}
        	
        	Request newSeed = this.createReq(articleUrl, "article",null, null, null);
        	newSeed.putExtra(LIST_OBJECT_LABEL, obj);
        	newSeed.putExtra(CHATNUMBER_OBJECT_LABEL, chatNumberInfo);
			page.addTargetRequest(newSeed);
        }
  	}

  	// 解析微信的请求参数中的  datetime 转化为 pubdate 字段
  	private String parseDateTime2PubDate(Integer datetime) {
  		String temp = datetime + "000";
  		long dateStamp = Long.parseLong(temp);
  		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(dateStamp));
	}

	private String getTempStr(String split) {
  		//split = split.replaceFirst(split.charAt(0) + "", "");
  		if(split.startsWith("[")) {
  			split = split.replace("[", "");
  		} else if (split.startsWith("]")) {
  			split = split.replace("]", "");
  		} else {
  			split = split.replaceFirst(split.charAt(0) + "", "");
  		}
		return split;
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
							this.site.addCookie(cookie.getName(), cookie.getValue());
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
     *a 资源字段处理
     * @param page
     * @param html
     * @param resExtractor
     * @return
     */
    private void processResource(Page page,Map<String,Object> process,String domain){
    	String reqUrl = page.getRequest().getUrl();
    	//获取资源列信息
    	List<String> imageFields = new ArrayList<String>();
    	for(ObjectModelExtractor extrator:this.extractors) {
    		imageFields.addAll(extrator.getResourceFields());
    	}
    	
    	List<ResourceFile> resources = new ArrayList<ResourceFile>();
    	//处理图片链接
    	//Map<String,Object> imageMap = new HashMap<>();
    	for(String fieldName :imageFields) {
    		Object imageObject = process.get(fieldName);
    		if(imageObject instanceof List) {
    			List<String> imageList = (List)imageObject;
    			List<String> imageArrayNew = new ArrayList<>();
    			String imageUrl;
    			for(Object imageNode:imageList) {
    				String downUrl = UrlParseUtils.getFullUrl(reqUrl, imageNode.toString()+"");
    				ResourceFile resFile = new ResourceFile(imageNode.toString(),downUrl,domain,baseUrl);
    				imageUrl = resFile.getRelativeUrl();
    				imageArrayNew.add(imageUrl);
    				resources.add(resFile);
    			}
    			process.put(fieldName, imageArrayNew);
    			
    		}else {
    			String imageUrl = (String)imageObject;
    			String downUrl = UrlParseUtils.getFullUrl(reqUrl, imageUrl);
				ResourceFile resFile = new ResourceFile(imageUrl,downUrl,domain,baseUrl);
    			imageUrl = resFile.getRelativeUrl();
    			process.put(fieldName, imageUrl);
    			resources.add(resFile);
    		}
    	}
		//获取新闻列信息
    	List<String> newsFields = new ArrayList<String>();
    	for(ObjectModelExtractor extrator:this.extractors) {
    		if(extrator.getHTMLFields()!=null&&extrator.getHTMLFields().size()>0) {
    			newsFields.addAll(extrator.getHTMLFields());
    		}
    	}
    	
    	//处理新闻字段图片替换
    	for(String newsField:newsFields) {
    		String newsText = MapUtils.getString(process, newsField);
    		System.out.println(newsText);
    		for(ResourceFile imageFile:resources) {
    			newsText = StringUtils.replace(newsText, imageFile.getOriginalUrl(), imageFile.getPublicUrl()); 
    		}
    		newsText = StringUtils.replace(newsText, "data-src", "src"); 
    		//处理内容页图片路径
    		process.put(newsField, newsText);
    	}
		
		List<ResourceFile> oldResources = (List<ResourceFile>)page.getResultItems().get(SpiderConstants.PAGE_RESOURCE_LABEL);
		if(oldResources!=null&&oldResources.size()>0){
			oldResources.addAll(resources);
			page.putField(SpiderConstants.PAGE_RESOURCE_LABEL, oldResources);	
		}else{
			page.putField(SpiderConstants.PAGE_RESOURCE_LABEL, resources);	
		}
		
    }
}
