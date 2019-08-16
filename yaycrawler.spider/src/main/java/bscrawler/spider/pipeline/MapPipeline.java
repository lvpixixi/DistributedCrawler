package bscrawler.spider.pipeline;

import java.util.List;
import java.util.Map;

import org.python.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 
 * @author wangdw
 *
 */
public class MapPipeline extends AbstractPipeline implements Pipeline {
	
    private static Logger logger = LoggerFactory.getLogger(MapPipeline.class);
    
    private List<Map<String,Object>> list = Lists.newArrayList();

    public MapPipeline(List<Map<String,Object>> records) {
    	this.list = null;
    	this.list = records;
    }
    
    public MapPipeline() {
    	
    }
    
    public List<Map<String,Object>> getRecords() {
    	return this.list;
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
			        		list.add(map);
			        	}
	        		}
	        	}else if(obj instanceof Map){
		        	Map<String,Object> map = (Map<String,Object>)resultItems.get(objectName);
		        	if(map!=null){
		        		map.putAll(basicInfo);
		        		list.add(map);
		        	}
	        	}
	        }
        }
	}
}
