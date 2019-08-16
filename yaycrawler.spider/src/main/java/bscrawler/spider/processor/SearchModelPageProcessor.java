package bscrawler.spider.processor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.listener.IPageParseListener;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.util.ChineseAndEnglish;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.utils.HttpConstant;

/**
 * json模型的页面解析处理器
 * 支持json配置文件的页面解析处理器
 * @author wangdw
 *
 */
public class SearchModelPageProcessor extends BasePageProcessor implements PageProcessor{
	
	protected Logger logger = LoggerFactory.getLogger(SearchModelPageProcessor.class);
	
    private Set<ObjectModelExtractor> extractors;    
    
    public final static String TYPE_LABEL = "type";
  
    private Site site;
    
    private int pages=1;
    
    private String listExpression;
    
    @Autowired(required = false)
    private IPageParseListener pageParseListener;
    /**
     * 过滤垃圾广告
     */
    private Map<String,String> excludeExpressions;
    
    private List<String> indexs;
	
    private String seedUrl;
	
	

    public SearchModelPageProcessor(Site site ,PageProcessorConf rootCfg) {
        this.site = site;
        Map<String,Object> wxConfig = rootCfg.getMapConf();
        
        if(rootCfg.getExcludeExpressions()!=null){
        	this.excludeExpressions = rootCfg.getExcludeExpressions();
        }
        if(wxConfig.containsKey("listExpression"))
        	listExpression = MapUtils.getString(wxConfig, "listExpression"); 
        
        if(wxConfig.containsKey("pages"))
        	pages = MapUtils.getIntValue(wxConfig, "pages"); 
        
        if(wxConfig.containsKey("indexs"))
        	indexs = (List<String>)wxConfig.get("indexs"); 
        
        if(wxConfig.containsKey("seedUrl"))
        	seedUrl = MapUtils.getString(wxConfig, "seedUrl"); 
        
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
			
		//处理文章列表页
		if(type==null){
			try {
				this.processIndexs(page);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else if("list".equals(type)){
			
			String indexId = (String)req.getExtra("indexId");	
			String keyword = (String)req.getExtra("keyWord");	
			
			
			JsonPathSelector json = new JsonPathSelector(page.getRawText());
			
			List<String> jarray = json.selectList("$.pageRow[*]");
			
			List<Map<String,Object>> listObj = new ArrayList<>();
			for(String article:jarray) {
				
				JSONObject jobj = JSONObject.parseObject(article);
				Map<String,Object> process = new HashMap<>();
				process.put("abstract", jobj.get("trans_abstract"));
				process.put("title", jobj.get("trans_title"));
				process.put("id", indexId);
				process.put("keyword", keyword);
				listObj.add(process);
				this.printMap(process);
			}
			page.putField("obj", listObj);
			List<String> objectNames = new ArrayList<>();
			objectNames.add("obj");
			page.putField("objectNames", objectNames);
		//处理文章页
		}
        
	}
    
   /* private String[] getWords(String wordstr){
    	
    }*/
    
    private void processIndexs(Page page) throws UnsupportedEncodingException {
		for(int i=1;i<=this.pages;i++){
			for(String index:this.indexs){
				String[] keywords = StringUtils.split(index,",");
				String enKeywords = URLEncoder.encode(keywords[2], "UTF-8");
				String reqUrl = StringUtils.replace(seedUrl, "${searchWord}", enKeywords);
				
				Request chi_NewSeed = this.createReq(reqUrl, "list",keywords[0], keywords[2]);
				chi_NewSeed.setExtras(this.getPostFormData(enKeywords, "chi", 2018, i));
				chi_NewSeed.setMethod(HttpConstant.Method.POST);
				page.addTargetRequest(chi_NewSeed);
				
				Request eng_NewSeed = this.createReq(reqUrl, "list",keywords[0], keywords[2]);
				eng_NewSeed.setExtras(this.getPostFormData(enKeywords, "eng", 2018, i));
				eng_NewSeed.setMethod(HttpConstant.Method.POST);
				page.addTargetRequest(eng_NewSeed);
				
			}
		}
		
	}
    
    /**
     * 
     * @param keyWord
     * @param language:chi,eng
     * @param year
     * @param pageNum
     * @return
     */
    private Map getPostFormData(String keyWord,String language,int year,int pageNum){
    	String keyWordRex = "(主题:(\"${keyword}\"))";
    	StringBuilder sb = new StringBuilder();
    	sb.append(StringUtils.replace(keyWordRex, "${keyword}", keyWord));
    	if(!StringUtils.isEmpty(language)){
    		sb.append("*$language:").append(language);
    	}
    	NameValuePair[] values = new NameValuePair[6];
        values[0] = new BasicNameValuePair("paramStrs", sb.toString().replace("+", " "));
        values[1] = new BasicNameValuePair("startDate", "1900");
        values[2] = new BasicNameValuePair("endDate", year+"");
        values[3] = new BasicNameValuePair("classType", "perio-perio_artical,degree-degree_artical,conference-conf_artical,patent-patent_element,techResult-tech_result,tech-tech_report");
        values[4] = new BasicNameValuePair("pageNum", pageNum+"");
        values[5] = new BasicNameValuePair("isSearchSecond", StringUtils.isEmpty(language)?"false":"true");
    	Map nameValuePair = new HashMap();
        nameValuePair.put("nameValuePair", values);
        return nameValuePair;
    }
    private Request createReq(String seedUrl,String type,String indexId,String keyWord){
    	if(!ChineseAndEnglish.isChinese(keyWord)){
    		//seedUrl = seedUrl+"&facetField=$language:eng";
    	}
    	Request newSeed = new Request(seedUrl);
		HashMap extras = new HashMap();
		extras.put(TYPE_LABEL, type);
		extras.put("indexId", indexId);
		extras.put("keyWord", keyWord);
		newSeed.setExtras(extras);
		return newSeed;
    }
    
    

    
}
