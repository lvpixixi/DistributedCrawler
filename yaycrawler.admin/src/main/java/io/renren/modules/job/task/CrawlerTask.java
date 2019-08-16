package io.renren.modules.job.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import bscrawler.spider.model.CrawlerConfig;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.service.ISpiderInfoService;
import io.renren.modules.spider.service.ISpiderManager;

/**
 * 测试定时任务(演示Demo，可删除)
 * 
 * testTask为spring bean的名称
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月30日 下午1:34:24
 */
@Component("CrawlerTask")
public class CrawlerTask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ISpiderManager spiderManager ;
	
	@Autowired
	private ISpiderInfoService spiderInfoService;

	//定时任务只能接受一个参数；如果有多个参数，使用json数据即可
	public void executeInternal(String spiderInfoId){
		SpiderInfo spiderInfo = spiderInfoService.getById(spiderInfoId);
		logger.info("开始定时网页采集任务，网站：{}，模板ID：{}", spiderInfo.getSiteName(), spiderInfo.getId());
		CrawlerConfig rootCfg = JSONObject.parseObject(spiderInfo.getJsonData(), CrawlerConfig.class);
	    String uuid = spiderManager.start(rootCfg);
	    logger.info("定时网页采集任务完成，网站：{}，模板ID：{},任务ID：{}", spiderInfo.getSiteName(), spiderInfo.getId(), uuid);
	}

}
