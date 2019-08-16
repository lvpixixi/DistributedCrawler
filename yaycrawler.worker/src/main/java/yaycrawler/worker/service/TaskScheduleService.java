package yaycrawler.worker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import bscrawler.spider.DefaultPageSiteService;
import bscrawler.spider.IPageSiteService;
import bscrawler.spider.SpiderBuilder;
import bscrawler.spider.YaySpider;
import bscrawler.spider.listener.IPageParseListener;
import bscrawler.spider.model.CrawlerConfig;
import bscrawler.spider.scheduler.CrawlerQueueScheduler;
import bscrawler.spider.util.RequestHelper;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.worker.listener.TaskDownloadFailureListener;
import yaycrawler.worker.model.FtpBean;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Service
public class TaskScheduleService {
    private static Logger logger = LoggerFactory.getLogger(TaskScheduleService.class);

    
    private IPageSiteService pageSiteService = new DefaultPageSiteService();
    
    @Autowired
    private ICrawlerConfigService crawlerConfigService;
    
    @Value("${ftp.server.url}")
   	private String ftpUrl;
   	@Value("${ftp.server.port}")
   	private int port;
   	@Value("${ftp.server.username}")
   	private String username;
   	@Value("${ftp.server.password}")
   	private String password;
   	@Value("${ftp.server.path}")
   	private String ftpPath;
   	
    private SpiderBuilder spiderBuilder;
    
    @Autowired
	private MongoTemplate mongoTemplate;

    @Autowired
    private TaskDownloadFailureListener downloadFailureListener;

    @Autowired
    private IPageParseListener pageParseListener;

    private Map<String, YaySpider> spiderMap = new HashMap<>();

    @Autowired(required = false)
    private DownloadService downloadService;

    public void refreshSpiderSite(String domain) {
        YaySpider spider = spiderMap.get(domain);
        if (spider == null) return;
        Site newSite = pageSiteService.getSite(domain);
        spider.setSite(newSite);
    }

    private SpiderBuilder getSpiderBuilder() {
    	
    	FtpBean ftpBean = new FtpBean();
		ftpBean.setFtpUrl(ftpUrl);
		ftpBean.setUsername(username);
		ftpBean.setPassword(password);
		ftpBean.setPort(port);
		ftpBean.setFtpPath(ftpPath);
		if(spiderBuilder==null) {
			spiderBuilder = new SpiderBuilder(mongoTemplate,ftpBean);
		}
		return spiderBuilder;
	}

    public Integer getRunningTaskCount() {
        int count = 0;
        for (Map.Entry<String, YaySpider> entry : spiderMap.entrySet()) {
        	
        	if(entry.getValue().getStatus() == Spider.Status.Running) {
        		count=count+1;
        	}
            /*CrawlerQueueScheduler crawlerQueueScheduler = (CrawlerQueueScheduler) entry.getValue().getScheduler();
            count += crawlerQueueScheduler.getLeftRequestsCount(null);*/
        }
        logger.info("worker还有{}个运行中任务", count);
        return count;
    }

    public void doSchedule(List<CrawlerRequest> taskList) {
        try {
            logger.info("worker接收到{}个任务", taskList.size());
            List<CrawlerRequest> downList = Lists.newArrayList();
            for (CrawlerRequest crawlerRequest : taskList) {
                if(crawlerRequest==null) continue;
               
                //资源下载链接
                if(MapUtils.getString(crawlerRequest.getExtendMap(),"$DOWNLOAD") != null) {
                    downList.add(crawlerRequest);
                }else {
                	 String domain = crawlerRequest.getDomain();
                     String id = crawlerRequest.getId();
                     String spiderId = crawlerRequest.getHashCode();
                     YaySpider spider = spiderMap.get(spiderId);
                     if (spider == null) {
                         spider = createSpider(id,spiderId,domain);
                         spiderMap.put(spiderId, spider);
                     }
                     spider.addRequest(convertCrawlerRequestToSpiderRequest(crawlerRequest));
                     if (spider.getStatus() != Spider.Status.Running) {
                         spider.runAsync();
                     }
                }
               
               
            }
            if(downList.size() > 0 ) {
                downloadService.startCrawlerDownload(downList);
            }
            logger.info("worker任务分配完成！");
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
    }

    private Request convertCrawlerRequestToSpiderRequest(CrawlerRequest CrawlerRequest) {
        return RequestHelper.createRequest(CrawlerRequest.getUrl(), CrawlerRequest.getMethod().toUpperCase(), CrawlerRequest.getData());
    }

    private YaySpider createSpider(String id,String spiderId,String domain) {
    	
    	CrawlerConfig crawlerConfig = crawlerConfigService.getCrawlerConfig(id);
        YaySpider spider = this.getSpiderBuilder().builderByBean(crawlerConfig);
        spider.setUUID(spiderId);
       /* spider.setScheduler(new CrawlerQueueScheduler());
        spider.addPipeline(pipeline);
        spider.setDownloader(genericCrawlerDownLoader);*/
        
        
        spider.getSpiderListeners().add(downloadFailureListener);
        //禁止重新启动
        //spiderMap.put(domain, spider);
        return spider;
    }
    
    public void stopSpider(String spiderId) {
    	YaySpider spider = this.spiderMap.get(spiderId);
    	spider.stop();
    }


    /**
     * 中断Worker的所有任务
     */
    public void interruptAllTasks() {
        logger.info("Worker开始停止所有的爬虫……");
        for (YaySpider spider : spiderMap.values()) {
            try {
                spider.stop();
                spider.close();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        logger.info("Worker的所有爬虫停止完成");
    }
}
