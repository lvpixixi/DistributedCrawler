package bscrawler.spider.pipeline;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import bscrawler.spider.translate.GoogleTranslate;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 将采集的数据存储到带翻译的  Mongo 数据库中
 * @author wangdw
 *
 */

public class MongoTranslateDBPipeline extends AbstractPipeline implements Pipeline {
	
    private static Logger logger = LoggerFactory.getLogger(MongoTranslateDBPipeline.class);
    
    
    
    public MongoTranslateDBPipeline(MongoTemplate mongoTemplate) {
    	this.mongoTemplate = mongoTemplate;
    }
    
    private MongoTemplate mongoTemplate;
    
    @Override
	public void process(ResultItems resultItems, Task task) {
		
    	//获取通用采集基本信息
    	Map<String,Object> basicInfo = this.getBasicInfoMap(resultItems, task);
    	
    	List<String> objectNames = resultItems.get("objectNames");
        if(objectNames!=null){
	        for(String objectName:objectNames){
	        	Object obj = resultItems.get(objectName);
	        	if(obj instanceof List){
	        		List<Map<String,Object>> list = (List<Map<String,Object>>)resultItems.get(objectName);
	        		for(Map<String,Object> map:list){
	        			if(map!=null){
	        				// 添加额外的信息
	        				map.putAll(addExtraInfo(map));
			        		map.putAll(basicInfo);
			        		writeToMongoDb(objectName,map);
			        	}
	        		}
	        	}else if(obj instanceof Map){
		        	Map<String,Object> map = (Map<String,Object>)resultItems.get(objectName);
		        	if(map!=null){
		        		// 添加额外的信息
        				map.putAll(addExtraInfo(map));
		        		map.putAll(basicInfo);
		        		writeToMongoDb(objectName,map);
		        	}
	        	}
	        }
        }
	}
        
    // 将正文和标题部分进行翻译并添加到集合中
    private Map<? extends String, ? extends Object> addExtraInfo(Map<String, Object> map) {
    	Map<String, String> extraInfo = Maps.newHashMap();
    	for(Entry<String, Object> entry : map.entrySet()) {
    		if(entry.getKey().equals("title")) {
    			String titleVal = (String) entry.getValue();
    			String titleTr = new GoogleTranslate().translate(titleVal);
    			System.out.println("title: ---------------> " + titleVal + "<==============> " + titleTr);
    			extraInfo.put("title_tr", null==titleTr ? null : StringUtils.replace(titleTr, "\\n", "").trim());
    		} else if(entry.getKey().equals("content")) {
    			String contentVal = (String) entry.getValue();
    			String contentTr = new GoogleTranslate().translate(contentVal);
    			
    			extraInfo.put("content_tr", null==contentTr ? null : StringUtils.replace(contentTr, "\\n", "").trim());
    		}
    	}
		return extraInfo;
	}

	private void writeToMongoDb(String collectionName,Map<String,Object> data){
    	 try {
             mongoTemplate.save(data, collectionName);
         } catch (Exception ex) {
             logger.error(ex.getMessage());
         }
    }
	
}
