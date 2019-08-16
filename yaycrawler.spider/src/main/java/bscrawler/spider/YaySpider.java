package bscrawler.spider;

import java.util.ArrayList;

import bscrawler.spider.IPageSiteService;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by yuananyun on 2016/5/2.
 */
public class YaySpider extends Spider {
    private IPageSiteService pageSiteService;
    private String domain;
    
    public YaySpider(String domain, IPageSiteService pageSiteService, PageProcessor pageProcessor) {
        super(pageProcessor);
        this.pageSiteService = pageSiteService;
        this.site = pageSiteService.getSite(domain);
        if(site==null) site = Site.me();
        this.setSpiderListeners(new ArrayList<SpiderListener>());
        spawnUrl=true;
        this.domain = domain;
    }

    public void setSite(Site site)
    {
        this.site = site;
    }
    
    public String getDomain() {
        return domain;
    }

}
