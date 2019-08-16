package yaycrawler.master.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.WorkerHeartbeat;
import yaycrawler.common.model.WorkerRegistration;
import yaycrawler.master.communication.WorkerActor;
import yaycrawler.master.model.MasterContext;
import yaycrawler.master.service.ICrawlerQueueService;

/**
 * 全局任务调度器
 * Created by ucs_yuananyun on 2016/5/17.
 */
@Component
public class CrawlerTaskDispatcher {

    private static Logger logger = LoggerFactory.getLogger(CrawlerTaskDispatcher.class);

    @Value("${worker.task.batchSize}")
    private Integer batchSize;


    @Autowired
    private ICrawlerQueueService queueService;

    @Autowired
    private WorkerActor workerActor;

    public void dealResultNotify(CrawlerResult crawlerResult) {
        if(crawlerResult==null) return;
        //crawlerResult.getStatus();
        switch (crawlerResult.getStatus()) {
        	 //插入新的任务
        	 case CrawlerResult.RESULT_CODE_NEW:
        		 queueService.pushTasksToWaitingQueue(crawlerResult.getCrawlerRequestList(), false);
        		 break;
        	 //更新运行中任务的执行信息
			 case CrawlerResult.RESULT_CODE_UPDATECOUNT:
				 queueService.updateRunningTask(crawlerResult);
			     break;
			 //任务完成后，更新运行中任务到成功队列
			 case CrawlerResult.RESULT_CODE_FINISH: 
				 queueService.moveRunningTaskToSuccessQueue(crawlerResult);
			     break;
			 //更新任务到失败队列
			 case CrawlerResult.RESULT_CODE_FAIL: 
				 queueService.moveRunningTaskToFailQueue(crawlerResult.getKey(), crawlerResult.getMessage());
			     break;
       
        }
       
    }



    /**
     * 分派任务
     */
    public void assignTasks(WorkerHeartbeat workerHeartbeat) {
    	
        ConcurrentHashMap<String, WorkerRegistration> workerListMap = MasterContext.workerRegistrationMap;
        WorkerRegistration workerRegistration = workerListMap.get(workerHeartbeat.getWorkerContextPath());
        if (workerRegistration == null) return;

        logger.info("worker:{}剩余任务数:{}", workerHeartbeat.getWorkerId(), workerHeartbeat.getWaitTaskCount());
        int canAssignCount = batchSize - workerHeartbeat.getWaitTaskCount();
        if (canAssignCount <= 0) return;
        //1-从等待队列中取出N条采集请求。
        List<CrawlerRequest> crawlerRequests = queueService.fetchTasksFromWaitingQueue(canAssignCount);
        if (crawlerRequests.size() == 0) return;
        
        List<CrawlerRequest> ableRunReqs = new ArrayList<CrawlerRequest>();
        for(CrawlerRequest req :crawlerRequests) {
    		if(req.getWorker()==null||req.getWorker().equals(workerHeartbeat.getWorkerId())) {
    			ableRunReqs.add(req);
    		}
        }
        boolean flag = workerActor.assignTasks(workerRegistration, ableRunReqs);
        if (flag) {
            logger.info("给worker:{}分派了{}个任务", workerHeartbeat.getWorkerId(), ableRunReqs.size());
            queueService.moveWaitingTaskToRunningQueue(workerHeartbeat.getWorkerId(), ableRunReqs);
        }
    }


}
