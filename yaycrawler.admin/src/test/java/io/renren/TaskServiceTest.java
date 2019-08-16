package io.renren;

import javax.management.JMException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;

import bscrawler.spider.model.CrawlerConfig;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.service.ISpiderInfoService;
import io.renren.modules.spider.service.ISpiderManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {
	
	@Autowired
	private ISpiderInfoService spiderInfoService;
	
	@Autowired
	private ISpiderManager spiderManager;

	@Test
	public void crawlerTest() {
		String spiderInfoId = "07f9e2857e7d406d8ffea0a68e685c53";
		SpiderInfo spiderInfo = spiderInfoService.getById(spiderInfoId);
		CrawlerConfig rootCfg = JSONObject.parseObject(spiderInfo.getJsonData(), CrawlerConfig.class);
		try {
			spiderManager.testSpiderInfo(rootCfg);
		} catch (JMException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

}
