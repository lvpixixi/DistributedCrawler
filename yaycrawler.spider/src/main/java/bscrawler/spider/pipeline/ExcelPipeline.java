package bscrawler.spider.pipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import bscrawler.spider.util.DateUtil;
import bscrawler.spider.util.excel.ChatExcel;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;
 
/**
 * 主对象内容json形式存储管道 
 * @author wangdw
 *
 */

public class ExcelPipeline extends FilePersistentBase implements Pipeline {
 
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String JSON_LABEL = "json";
    private final String line_separator = System.getProperty("line.separator");
    public ExcelPipeline() {
        setPath("D:/data/webmagic/");
    }
 
    public ExcelPipeline(String path) {
        setPath(path);
    }
    
    @Override
    public void process(ResultItems resultItems, Task task) {
    	String fileStorePath = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        List<String> objectNames = resultItems.get("objectNames");
        if(objectNames!=null){
	        for(String objectName:objectNames){
	        	Object obj = resultItems.get(objectName);
	        	if(obj instanceof List){
	        		List<Map<String,Object>> list = (List<Map<String,Object>>)resultItems.get(objectName);
	        		for(Map<String,Object> map:list){
	        			if(map!=null){
			        		map.put("url", resultItems.getRequest().getUrl());
			        		writeToExcel(map,fileStorePath);
			        	}
	        		}
	        	}else if(obj instanceof Map){
		        	Map<String,Object> map = (Map<String,Object>)resultItems.get(objectName);
		        	if(map!=null){
		        		map.put("url", resultItems.getRequest().getUrl());
		        		writeToExcel(map,fileStorePath);
		        	}
	        	}
	        }
        }
    }
    
    private void writeToExcel(Map<String,Object> obj,String fileStorePath){
    		String json = JSON.toJSONString(obj);
            String filename;
            filename = fileStorePath + DateUtil.getCurrentDateStr() + ".xlsx";
            ChatExcel eu =new ChatExcel(filename,new ArrayList<String>(obj.keySet()));
            eu.append(obj);
            eu.flush();
            //FileUtils.writeFile(json+line_separator, filename);
           /* PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(filename)));
            printWriter.write(json);
            printWriter.close();*/
    }
}