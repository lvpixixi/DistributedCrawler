package yaycrawler.worker.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import bscrawler.spider.model.CrawlerConfig;
import yaycrawler.dao.domain.SpiderInfo;
import yaycrawler.dao.repositories.SpiderInfoRepository;

@Service
public class CrawlerConfigService implements ICrawlerConfigService{

	@Autowired
	private SpiderInfoRepository spiderInfoRepository;
	
	@Override
	public CrawlerConfig getCrawlerConfig(String id) {
		SpiderInfo spiderInfo =spiderInfoRepository.findOne(id);
		if(spiderInfo!=null){
			 CrawlerConfig rootCfg = JSONObject.parseObject(spiderInfo.getJsonData(), CrawlerConfig.class);
			 if(rootCfg.getPageProcessorConf()!=null) {
				 JSONObject projects = JSONObject.parseObject(spiderInfo.getJsonProject());
				 Set<String> keys = projects.keySet();
				 rootCfg.getPageProcessorConf().setProjects(keys);
			 }
			 return rootCfg;
		}
		return null;
	}

}
