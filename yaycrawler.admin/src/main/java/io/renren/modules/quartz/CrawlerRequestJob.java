package io.renren.modules.quartz;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import io.renren.modules.spider.communication.MasterActor;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.service.ISpiderInfoService;
import yaycrawler.common.model.CrawlerRequest;
/**
 * 定时发送爬取请求任务
 * 
 * @author wangdw
 * @email wangdaweigo@gmail.com
 * @date 2018年11月27日 下午1:34:24
 */
@Component("CrawlerRequestJob")
public class CrawlerRequestJob{
    private List<CrawlerRequest> crawlerRequestList;
    @Autowired
    private MasterActor masterActor;   
    
	@Autowired
	private ISpiderInfoService spiderInfoService;
    
    public boolean execute(String spiderInfoId) {
    	
    	SpiderInfo spiderInfo = spiderInfoService.getById(spiderInfoId);
    	//根据爬取模板添加爬取请求
    	this.addCrawlerRequest(new CrawlerRequest(spiderInfo.getId(),spiderInfo.getUrl(),spiderInfo.getDomain(), ""));
        //发布任务到Master
        return masterActor.publishTasks(crawlerRequestList.toArray(new CrawlerRequest[]{}));
    }

    public List<CrawlerRequest> getCrawlerRequestList() {
        return crawlerRequestList;
    }

    public void setCrawlerRequestList(List<CrawlerRequest> crawlerRequestList) {
        this.crawlerRequestList = crawlerRequestList;
    }

    public void addCrawlerRequest(CrawlerRequest... requests) {
        if (crawlerRequestList == null) crawlerRequestList = new LinkedList<>();
        for (CrawlerRequest crawlerRequest : requests) {
            crawlerRequestList.add(crawlerRequest);
        }
    }

    @Override
    public String toString() {
        return "CrawlerRequestJob: ";
    }
}
