package io.renren.modules.spider.service.impl;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.JMException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

import bscrawler.spider.SpiderBuilder;
import bscrawler.spider.download.FtpBean;
import bscrawler.spider.listener.RunTestSpiderListener;
import bscrawler.spider.model.CrawlerConfig;
import bscrawler.spider.pipeline.MapPipeline;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.gather.AsyncGather;
import io.renren.modules.spider.gather.model.State;
import io.renren.modules.spider.gather.model.Task;
import io.renren.modules.spider.model.SchedulerInfo;
import io.renren.modules.spider.model.SpiderRuntimeInfo;
import io.renren.modules.spider.model.Webpage;
import io.renren.modules.spider.service.ISpiderInfoService;
import io.renren.modules.spider.service.ISpiderManager;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.scheduler.QueueScheduler;

@Service("defaultSpiderManager")
public class DefaultSpiderManager extends AsyncGather implements ISpiderManager,Serializable {

	private static final long serialVersionUID = 400524272685070043L;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSpiderManager.class);
	
	private final String QUARTZ_JOB_GROUP_NAME = "webpage-spider-job";
	private final String QUARTZ_TRIGGER_GROUP_NAME = "webpage-spider-trigger";
	private final String QUARTZ_TRIGGER_NAME_SUFFIX = "-hours";
	private static final String SPIDER_INFO = "spiderInfo";
	private Map<String, Spider> spiderMap = new HashMap<>();
	
	@Autowired
	private ScheduleJobService scheduleJobService;
	
	@Autowired
	private ISpiderInfoService spiderInfoService;
	
	
	private SpiderBuilder spiderBuilder;
	
	@Autowired
	private MongoTemplate mongoTemplate;
    
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
	
	public SpiderBuilder getSpiderBuilder() {
		
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
	
	@Override
	public String start(CrawlerConfig info) {
		 
		 boolean running = taskManager.findTaskById(info.getId());
	     Preconditions.checkArgument(!running, "已经提交了这个任务,模板编号%s,请勿重复提交", info.getId());
	        
	     final String uuid = UUID.randomUUID().toString();
	     //初始化任务
	     Task task = taskManager.initTask(uuid, info.getSpiderConf().getDomain(), "", "spiderInfoId=" + info.getId() + "&spiderUUID=" + uuid);
	     task.addExtraInfo(SPIDER_INFO, info);
        
         Spider spider = (Spider) makeSpider(info, task);
     
         //慎用爬虫监控,可能导致内存泄露
         //spiderMonitor.register(spider);
         spiderMap.put(uuid, spider);
         spider.run();
         taskManager.getTaskById(uuid).setState(State.RUNNING);
         return uuid;
	}
	
	/**
     * 生成爬虫
     *
     * @param info 抓取模板
     * @param task 任务实体
     * @return
     */
    private Spider makeSpider(CrawlerConfig info, Task task) {
    	Spider spider = this.getSpiderBuilder().builderByBean(info);
		spider.setUUID(task.getTaskId());
		return spider;
    }
    
	@Override
	public void stop(String uuid) {
		Preconditions.checkArgument(spiderMap.containsKey(uuid), "找不到UUID为%s的爬虫,请检查参数", uuid);
		spiderMap.get(uuid).stop();
		spiderMap.get(uuid).close();
	    taskManager.getTaskById(uuid).setState(State.STOP);
		
	}

	@Override
	public void delete(String uuid) {
		Preconditions.checkArgument(spiderMap.containsKey(uuid) || taskManager.getTaskById(uuid) != null, "找不到UUID为%s的爬虫,请检查参数", uuid);
	    Preconditions.checkArgument(taskManager.getTaskById(uuid).getState() == State.STOP, "爬虫" + uuid + "尚未停止,不能删除任务");
	    deleteTaskById(uuid);
	    spiderMap.remove(uuid);
		
	}

	@Override
	public void deleteAll() {
		for(Map.Entry<String,Spider> entry:spiderMap.entrySet()){
			if(entry.getValue().getStatus() == Spider.Status.Stopped){
				try {
					
	                deleteTaskById(entry.getKey());
	                spiderMap.remove(entry.getKey());
	            } catch (Exception e) {
	                LOG.error("删除任务ID:{}出错,{}", entry.getKey(), e.getLocalizedMessage());
	            }
			}
			
		}
        taskManager.deleteTasksByState(State.STOP);
		
	}

	@Override
	public List<Map<String,Object>> testSpiderInfo(CrawlerConfig info) throws JMException {
		  final MapPipeline mapPipeline = new MapPipeline();
	      final String uuid = UUID.randomUUID().toString();
	      Task task = taskManager.initTask(uuid, info.getSpiderConf().getDomain(), "", "spiderInfoId=" + info.getId() + "&spiderUUID=" + uuid);
	      task.addExtraInfo("spiderInfo", info);
	      QueueScheduler queueScheduler = new QueueScheduler();
	      
	     
	      Spider spider = makeSpider(info, task).addPipeline(mapPipeline)
	                .setScheduler(queueScheduler);

	      List<SpiderListener> spiderListeners = Lists.newArrayList();
	      List<String> errorMsgs = Lists.newArrayList();
	      spiderListeners.add(new RunTestSpiderListener(spider,5,errorMsgs));
	      spider.setSpiderListeners(spiderListeners);
	      
	      spiderMap.put(uuid, spider);
	      taskManager.getTaskById(uuid).setState(State.RUNNING);
	      spider.run();
	      return mapPipeline.getRecords();
	}
	
	 /**
     * 将webmagic的resultItems转换成webpage对象
     *
     * @param resultItems
     * @return
     */
    public static Webpage convertResultItems2Webpage(ResultItems resultItems) {
    	
    	
        Webpage webpage = new Webpage();      
        webpage.setUrl(resultItems.get("url"));
        webpage.setId(Hashing.md5().hashString(webpage.getUrl(), Charset.forName("utf-8")).toString());
        webpage.setDomain(resultItems.get("domain"));
        webpage.setSpiderInfoId(resultItems.get("spiderInfoId"));
        webpage.setGathertime(resultItems.get("gatherTime"));
        webpage.setSpiderUUID(resultItems.get("spiderUUID"));        
        webpage.setCategory(resultItems.get("category"));
        webpage.setRawHTML(resultItems.get("rawHTML"));              
        webpage.setAttachmentList(resultItems.get("attachmentList"));
        webpage.setImageList(resultItems.get("imageList"));
        webpage.setProcessTime(resultItems.get("processTime"));        
        webpage.setJsonData(resultItems.get("objects"));
        return webpage;
    }

	@Override
	public List<SpiderRuntimeInfo> listAllSpiders(boolean containsExtraInfo) {
		List<SpiderRuntimeInfo> result = new ArrayList<>();
		 for(Map.Entry<String, Spider> spiderEntry:spiderMap.entrySet()){
			 Spider spider = spiderEntry.getValue();
			 result.add(makeSpiderRuntimeInfo(spider, containsExtraInfo));
		 }
	     return result;
	}
	
	/**
     * 根据ID获取爬虫对象
     *
     * @param uuid
     * @return
     */
    public Spider getSpiderById(String uuid) {
        Preconditions.checkArgument(spiderMap.containsKey(uuid), "找不到UUID为%s的爬虫,请检查参数", uuid);
        return spiderMap.get(uuid);
    }

	@Override
	public SpiderRuntimeInfo getSpiderRuntimeInfo(String uuid, boolean containsExtraInfo) {
		return makeSpiderRuntimeInfo(getSpiderById(uuid), containsExtraInfo);
	}
	 /**
     * 获取爬虫运行时信息
     *
     * @param spider
     * @return
     */
    private SpiderRuntimeInfo makeSpiderRuntimeInfo(Spider spider, boolean containsExtraInfo) {
    	SpiderRuntimeInfo infoMap = new SpiderRuntimeInfo();
    	
    	Task task =getTaskById(spider.getUUID(), true);
    	infoMap.setTaskName(task.getName());
    	infoMap.setId(task.getTaskId());
    	infoMap.setPageCount(spider.getPageCount());
    	infoMap.setStartTime(spider.getStartTime());
    	infoMap.setThreadAlive(spider.getThreadAlive());
    	infoMap.setStatus(spider.getStatus());
    	//TODO
    	//infoMap.setSpiderInfo(spider.get);
       
        if (containsExtraInfo) {
           // infoMap.put("Links", getTaskById(spider.getUUID(), true).getExtraInfoByKey(LINK_KEY));
        }
        return infoMap;
    }

    
    /**
     * 创建定时任务
     *
     * @param spiderInfoId  爬虫模板id
     * @param hoursInterval 每几小时运行一次
     */
    public String createQuartzJob(String spiderInfoId, String cronExp) {
        SpiderInfo spiderInfo = spiderInfoService.getById(spiderInfoId);
        ScheduleJobEntity scheduleJob = new ScheduleJobEntity();
        scheduleJob.setBeanName("CrawlerTask");
        scheduleJob.setMethodName("executeInternal");
        scheduleJob.setParams(spiderInfoId+"");
        scheduleJob.setCronExpression(cronExp);
        scheduleJob.setRemark(spiderInfo.getSiteName());
        scheduleJobService.save(scheduleJob);
        return spiderInfoId+"";
    }

	@Override
	public List<SchedulerInfo> listAllQuartzJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String removeQuartzJob(String spiderInfoId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkQuartzJob(String spiderInfoId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportQuartz() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void importQuartz(String json) {
		// TODO Auto-generated method stub
		
	}

   

}
