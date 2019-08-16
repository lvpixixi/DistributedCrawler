package bscrawler.spider.pipeline;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import yaycrawler.common.utils.MySimHash;

/**
 * 
 * @author wangdw
 *
 */

public class MongDBPipeline extends AbstractPipeline implements Pipeline {
	
    private static Logger logger = LoggerFactory.getLogger(MongDBPipeline.class);
    
    
    private MongoTemplate mongoTemplate;
    
    public MongDBPipeline(MongoTemplate mongoTemplate) {
    	this.mongoTemplate = mongoTemplate;
    }

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
			        		map.putAll(basicInfo);
			        		writeToMongoDb(objectName,map);
			        	}
	        		}
	        	}else if(obj instanceof Map){
		        	Map<String,Object> map = (Map<String,Object>)resultItems.get(objectName);
		        	if(map!=null){
		        		map.putAll(basicInfo);
		        		writeToMongoDb(objectName,map);
		        	}
	        	}
	        }
        }
	}
        
    private void writeToMongoDb(String collectionName,Map<String,Object> data){
    	 try {
    		 //TODO 根据内容生成simHash，用于去除重复
    		 if(data.containsKey("content") && data.get("content")!=null) {
    			 MySimHash simHash = new MySimHash(data.get("content").toString());
    			 data.put("simhash", simHash.getSimHash()+"");
    		 }
             mongoTemplate.save(data, collectionName);
         } catch (Exception ex) {
             logger.error(ex.getMessage());
         }
    }
}
