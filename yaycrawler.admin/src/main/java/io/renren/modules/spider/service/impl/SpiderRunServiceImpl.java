package io.renren.modules.spider.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import bscrawler.spider.SpiderBuilder;
import bscrawler.spider.download.IFtpConfig;
import bscrawler.spider.listener.RunTestSpiderListener;
import bscrawler.spider.pipeline.MapPipeline;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.service.ISpiderInfoService;
import io.renren.modules.spider.service.ISpiderRunService;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.pipeline.Pipeline;

@Service("SpiderRunServiceImpl")
public class SpiderRunServiceImpl implements ISpiderRunService{

	 private SpiderBuilder spiderBuilder;
	 
	 @Autowired
	 private MongoTemplate mongoTemplate;
		
	 @Autowired
	 private IFtpConfig ftpBean;
	 
	 @Autowired
	 private ISpiderInfoService spiderInfoService;
	 
	 private SpiderBuilder getSpiderBuilder() {
		if(spiderBuilder==null) {
			spiderBuilder = new SpiderBuilder(mongoTemplate,ftpBean);
		}
		return spiderBuilder;
	}
	    
	@Override
	public List<Map<String, Object>> runSpiderTest(String spiderId) throws Exception {
		SpiderInfo info = spiderInfoService.getById(spiderId);
		Spider spider = getSpiderBuilder().builderByJson(info.getJsonData());
		List<Map<String,Object>> list = Lists.newArrayList();
    	List<String> errorMsgs = Lists.newArrayList();
    	
    	List<Pipeline> pipelines = Lists.newArrayList();
        pipelines.add(new MapPipeline(list));
          
        List<SpiderListener> spiderListeners = Lists.newArrayList();
        spiderListeners.add(new RunTestSpiderListener(spider,5,errorMsgs));
        spider.setPipelines(pipelines);
        spider.setSpiderListeners(spiderListeners);   
        spider.close();
        
		return list;
	}

	@Override
	public void runAllSpiderTest() {
		
		List<SpiderInfo> infos = spiderInfoService.queryList(Maps.newHashMap());
		List<Map<String,Object>> list;
    	List<String> errorMsgs;
    	List<Pipeline> pipelines;
    	List<SpiderListener> spiderListeners;
		for(SpiderInfo info:infos) {
			
			try {
				Spider spider = getSpiderBuilder().builderByJson(info.getJsonData());
				list = Lists.newArrayList();
				errorMsgs = Lists.newArrayList();
				pipelines = Lists.newArrayList();
				spiderListeners = Lists.newArrayList();
				
				
				spiderListeners.add(new RunTestSpiderListener(spider,5,errorMsgs));
				pipelines.add(new MapPipeline(list));
				spider.setPipelines(pipelines);
		        spider.setSpiderListeners(spiderListeners);   
		        spider.close();
		        
		        spiderInfoService.update(info);
				
			} catch (Exception e) {
				e.printStackTrace();
				try {
					spiderInfoService.update(info);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		}
		
	}

}
