package bscrawler.spider.pipeline;

import java.util.Map;

import com.google.common.collect.Maps;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;

public abstract class AbstractPipeline{
	
	public Map<String,Object> getBasicInfoMap(ResultItems resultItems, Task task){
		Map<String,Object> basicInfo = Maps.newHashMap();
		basicInfo.put("crawler_url", resultItems.getRequest().getUrl());
		basicInfo.put("crawler_date", new Long(System.currentTimeMillis()));
		basicInfo.put("crawler_site", resultItems.get("domain"));
		Spider ys = null;
		if(task instanceof Spider) {
			ys = (Spider) task;
		}
		if(null != ys) {
			basicInfo.put("crawler_project", ys.getProject()==null?"":ys.getProject());
		}
		basicInfo.put("status", resultItems.get("status")==null?"0":resultItems.get("status"));
    	return basicInfo;
	}

}
