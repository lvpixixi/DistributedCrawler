package yaycrawler.worker.service;

import bscrawler.spider.model.CrawlerConfig;

public interface ICrawlerConfigService {
	
	public CrawlerConfig getCrawlerConfig(String id);

}
