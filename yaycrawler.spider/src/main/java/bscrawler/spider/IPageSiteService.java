package bscrawler.spider;

import bscrawler.spider.model.SpiderConf;
import us.codecraft.webmagic.Site;

public interface IPageSiteService {
	public void setConfig(SpiderConf config);
	public Site getSite(String domain);
	public Site getSite(String domain, String proxyType);

}
