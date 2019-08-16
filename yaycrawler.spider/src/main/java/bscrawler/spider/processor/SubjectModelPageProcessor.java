package bscrawler.spider.processor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import bscrawler.spider.SpiderConstants;
import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.resolver.SelectorExpressionResolver;
import bscrawler.spider.util.CrawlerCommonUtils;
import bscrawler.spider.util.FileUtils;
import bscrawler.spider.util.UrlParseUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 主题检索模型的页面解析处理器
 * 支持json配置文件的页面解析处理器
 * @author wangdw
 *
 */
public class SubjectModelPageProcessor extends BasePageProcessor implements PageProcessor {
	protected Logger logger = LoggerFactory.getLogger(WeixinModelPageProcessor.class);
	private List<String> indexs;
	private String listRule;
	public SubjectModelPageProcessor(Site site ,PageProcessorConf rootCfg) {
        this.site = site;
        if(rootCfg.getMapConf()!=null){
        	if(rootCfg.getMapConf().containsKey("indexFile")) {
	        	String filePath = MapUtils.getString(rootCfg.getMapConf(), "indexFile");
	        	indexs = FileUtils.fileToList(new File(filePath));
        	}else if(rootCfg.getMapConf().containsKey("indexs")) {
        		indexs = (List) rootCfg.getMapConf().get("indexs");
        	}
        	
        	this.listRule = MapUtils.getString(rootCfg.getMapConf(), "listRule");
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
    	
    	List<String> objectNames = new ArrayList<String>();
    	
    	 Map<String,Object> context = new HashMap<String,Object>();
    	 context.put("domain", site.getDomain());
    	 
    	 
    	String requestType = (String)page.getRequest().getExtra(SpiderConstants.REQUEST_TYPE);
    	 
    	//1)构建检索主题词链接
    	if(requestType == null || SpiderConstants.REQUEST_SEARCH.equals(requestType)) { //搜索页面
    		String searchText_Ui = "";
    		for(String searchText:this.getIndexs()){
				try {
					searchText_Ui = URLEncoder.encode(searchText, "UTF-8");
					page.putField("searchText", searchText);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    			String url = CrawlerCommonUtils.replaceUrlParam(page.getUrl().get(),"word", searchText_Ui);
    			for(int i=1;i<=3;i++){
	    			url = CrawlerCommonUtils.replaceUrlParam(url,"pn", (i-1)*10+"");
	    			//StringUtils.replace(config.getSeedRex(), "${index}",searchText_Ui);
	    			Request seedUrl = new Request(url).putExtra(SpiderConstants.REQUEST_TYPE,
	    					SpiderConstants.REQUEST_DETAIL_LIST).putExtra("searchText", searchText);
	    			page.addTargetRequest(seedUrl);
    			}
    		}
    			
    	//2)抽取检索结果列表链接
    	}else if(SpiderConstants.REQUEST_DETAIL_LIST.equals(requestType)){
    		
    		//List<Selectable> list = page.getHtml().xpath(this.x).nodes();
    		
    		Selectable rootEle = SelectorExpressionResolver.getPageSelectable(page, page.getRequest(), this.listRule);
      		List<Object> links = SelectorExpressionResolver.resolve(page.getRequest(), rootEle, this.listRule);
      		for(Object node:links){
    			String detailurl = UrlParseUtils.getFullHref(node+"", page.getRequest().getUrl());
    			logger.info("detailurl="+detailurl);
    			page.addTargetRequest(new Request(detailurl).putExtra(SpiderConstants.REQUEST_TYPE, SpiderConstants.REQUEST_DETAIL));
    		}
    		
    	//3) 抽取内容页
    	}else if(SpiderConstants.REQUEST_DETAIL.equals(requestType)){
    		String url = page.getRequest().getUrl();
			// 加入当前 搜索的关键字
			page.putField("searchText", page.getRequest().getExtras().get("searchText"));
			
			for (ObjectModelExtractor extractor : this.extractors) {
	    		
	    		 List<ResourceFile> resources = new ArrayList<>();
	             //解析页面
	    		 Object resultObject =  extractor.getObject(page,Lists.newArrayList(), resources);
	             if (resultObject == null || (resultObject instanceof List && ((List) resultObject).size() == 0)) {
	                 continue;
	             }
	             
	             objectNames.add(extractor.getObjName());
	             
	             //解析出的是Bean设置主键
	             if(resultObject instanceof Map) {
	            	 Map<String,Object> process = (Map)resultObject;
	            	 process.put("_id", this.generatorId(extractor.getPks(),process));
	            	 page.putField(extractor.getObjName(), process);
	             //解析出的是列表
	             }else {
	            	 page.putField(extractor.getObjName(), resultObject);
	             }
	         }
	    	
			 //设置对象名称
	         if(objectNames.size()>0){
	         	 page.putField("objectNames", objectNames);
	         }
             
	         
    	}
    	
    	
    }

	public List<String> getIndexs() {
		return indexs;
	}

	public void setIndexs(List<String> indexs) {
		this.indexs = indexs;
	}

}
