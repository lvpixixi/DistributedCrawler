package io.renren.modules.spider.quartz;

import java.util.LinkedList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import io.renren.modules.spider.communication.MasterActor;
import io.renren.modules.spider.quartz.model.AbstractExecutableJob;
import io.renren.modules.spider.quartz.model.ScheduleJobInfo;
import yaycrawler.common.model.CrawlerRequest;

/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
public class CrawlerRequestJob extends AbstractExecutableJob {
    private static Logger logger = LoggerFactory.getLogger(CrawlerRequestJob.class);
    private List<CrawlerRequest> crawlerRequestList;

    public CrawlerRequestJob(ScheduleJobInfo jobInfo) {
        super(jobInfo);
    }

    @Override
    public boolean execute(JobExecutionContext context) {
        //发布任务到Master
        try {
            ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContextKey");
            MasterActor masterActor = appContext.getBean("masterActor", MasterActor.class);
            if (masterActor == null) throw new RuntimeException("获取MasterActor失败！");
            return masterActor.publishTasks(crawlerRequestList.toArray(new CrawlerRequest[]{}));
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
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
        return "CrawlerRequestJob: " + this.getJobInfo().toString();
    }
}
