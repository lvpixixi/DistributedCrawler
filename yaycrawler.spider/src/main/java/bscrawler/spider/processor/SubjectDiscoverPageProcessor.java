package bscrawler.spider.processor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bscrawler.spider.SpiderConstants;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.resolver.SelectorExpressionResolver;
import bscrawler.spider.util.FileUtils;
import bscrawler.spider.util.UrlParseUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;


/**
 * 基于关键字网站发现页面解析
 * @author wangdw
 * @Description  按照提供的关键字自动发现网站链接，并进行统计。
 * @date: 2018/12/2
 */
public class SubjectDiscoverPageProcessor extends BasePageProcessor implements PageProcessor {
	protected Logger logger = LoggerFactory.getLogger(SubjectDiscoverPageProcessor.class);
	private List<String> indexs;
	private String urlSeed;
	private String listRule;
	private int totalCount;
	private int pageSize;
	// 开始索引
	private int startIndex;
	public SubjectDiscoverPageProcessor(Site site ,PageProcessorConf rootCfg) {
        this.site = site;
        if(rootCfg.getMapConf()!=null){
        	if(rootCfg.getMapConf().containsKey("indexFile")) {
	        	String filePath = MapUtils.getString(rootCfg.getMapConf(), "indexFile");
	        	indexs = FileUtils.fileToList(new File(filePath));
        	}else if(rootCfg.getMapConf().containsKey("indexs")) {
        		indexs = (List) rootCfg.getMapConf().get("indexs");
        	}
        	this.urlSeed = MapUtils.getString(rootCfg.getMapConf(), "urlSeed");
        	this.listRule = MapUtils.getString(rootCfg.getMapConf(), "listRule");
        	
        	this.totalCount = MapUtils.getIntValue(rootCfg.getMapConf(), "totalCount");
        	this.pageSize = MapUtils.getIntValue(rootCfg.getMapConf(), "pageSize");
        	this.startIndex = MapUtils.getIntValue(rootCfg.getMapConf(), "startIndex");
        }
        //this.iniExtractors(rootCfg.getExtractors());
    }
	
   /* public void iniExtractors(List jarray) {
    	extractors = new HashSet<ObjectModelExtractor>() ;
    	for(int i=0;i<jarray.size();i++){
    		JSONObject json = (JSONObject)jarray.get(i);
    		ObjectModelExtractor extractor = JSONObject.toJavaObject(json, ObjectModelExtractor.class);
    		extractors.add(extractor);
    	}
    }*/

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
					//page.putField("searchText", searchText);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
    			String curUrl;
    			int index = startIndex;
    			while(index<=totalCount) {
	    			curUrl = UrlParseUtils.parse0(this.getUrlSeed(),searchText_Ui,index+"");
	    			logger.info("reqUrl="+curUrl);
	    			Request seedUrl = new Request(curUrl).putExtra(SpiderConstants.REQUEST_TYPE,
	    					SpiderConstants.REQUEST_DETAIL_LIST).putExtra("searchText", searchText);
	    			page.addTargetRequest(seedUrl);
	    			index = index+pageSize;
    			}
    		}
    			
    	//2)抽取检索结果列表链接
    	}else if(SpiderConstants.REQUEST_DETAIL_LIST.equals(requestType)){
    		Selectable rootEle = SelectorExpressionResolver.getPageSelectable(page, page.getRequest(), this.listRule);
      		List<String> links = SelectorExpressionResolver.resolve(page.getRequest(), rootEle, this.listRule);
      		String searchText = (String)page.getRequest().getExtra("searchText");
      		String domain;
      		List<Map<String,String>> list = new ArrayList<>();
      		Map<String,String> obj = new HashMap<>();
      		for(String link:links) {
      			obj.put("siteUrl", UrlParseUtils.getDomain(link));
      			obj.put("searchText", searchText);
      			obj.put("searchUrl", this.site.getDomain());
      			logger.info("siteUrl="+UrlParseUtils.getDomain(link));
      			list.add(obj);
      		}
      		objectNames.add("site");
      		page.putField("objectNames", objectNames);
      		page.putField("site", list);
    		
    	}
    	
    }

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public List<String> getIndexs() {
		return indexs;
	}

	public void setIndexs(List<String> indexs) {
		this.indexs = indexs;
	}

	public String getUrlSeed() {
		return urlSeed;
	}

	public void setUrlSeed(String urlSeed) {
		this.urlSeed = urlSeed;
	}

	public String getListRule() {
		return listRule;
	}

	public void setListRule(String listRule) {
		this.listRule = listRule;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
