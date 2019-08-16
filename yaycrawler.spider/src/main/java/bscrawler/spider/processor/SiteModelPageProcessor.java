package bscrawler.spider.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.model.PageProcessorConf;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selector;

/**
 * json模型的页面解析处理器
 * 支持json配置文件的页面解析处理器
 * @author wangdw
 *
 */
public class SiteModelPageProcessor extends BasePageProcessor implements PageProcessor{

	//private Set<ObjectModelExtractor> extractors = new HashSet<>();  
	
    private Site site;
    
    private PageProcessorConf config;
    
    private String rootPath;

    public SiteModelPageProcessor(Site site ,PageProcessorConf config) {
        this.site = site;
        this.config = config;
        this.iniPageModel(config.getExtractors());
    }
	

    @Override
    public void process(Page page,List<SpiderListener> listeners) {
    	
    	List<String> objectNames = new ArrayList<String>();
    	
    	Map<String,Object> context = new HashMap<String,Object>();
    	 
    	context.put("domain", site.getDomain());
    	page.putField("domain", site.getDomain());
    	for (ObjectModelExtractor extractor : this.extractors) {
        	
        	//抽取链接
/*        	if(extractor.getValue().getHelpUrlPatterns()!=null&&extractor.getValue().getHelpUrlPatterns().size()>0){
       	 		extractLinks(page, extractor.getValue().getHelpUrlRegionSelector(), extractor.getValue().getHelpUrlPatterns(), false);
        	}
        	if(!config.isSinglePage()&&extractor.getValue().getTargetUrlPatterns()!=null&&extractor.getValue().getTargetUrlPatterns().size()>0){
        		extractLinks(page, extractor.getValue().getTargetUrlRegionSelector(), extractor.getValue().getTargetUrlPatterns(), true);
        	}*/
           
            //解析页面
            Object process = extractor.getObject(page,Lists.newArrayList(), Lists.newArrayList());
            if (process == null || (process instanceof List && ((List) process).size() == 0)) {
                continue;
            }
            objectNames.add(extractor.getObjName());
            page.putField(extractor.getObjName(), process);
        }
        
        if(objectNames.size()>0){
        	 page.putField("objectNames", objectNames);
        }
        
        if (page.getResultItems().getAll().size() == 0) {
            page.getResultItems().setSkip(true);
        }
    }

    //抽取链接方法
    private void extractLinks(Page page, Selector urlRegionSelector, List<Pattern> urlPatterns, boolean isTargetUrl ) {
        List<String> links;
        if (urlRegionSelector == null) {
            links = page.getHtml().links().all();
        } else {
            links = page.getHtml().selectList(urlRegionSelector).links().all();
        }
        for (String link : links) {
            for (Pattern targetUrlPattern : urlPatterns) {
                Matcher matcher = targetUrlPattern.matcher(link);
                if (matcher.find()) {
                	//去重处理
        			//if(!isTargetUrl || !this.fileCacheDoubleRemvoeFilter.isContains(matcher.group(1))){
        				page.addTargetRequest(new Request(matcher.group(1)));
                    
        			//}
                }
            }
        }
    }
    
}
