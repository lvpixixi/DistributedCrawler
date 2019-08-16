package yaycrawler.master.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Maps;

import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.master.MasterApplication;

/**
 * Created by yuananyun on 2016/8/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MasterApplication.class)
@WebAppConfiguration
public class RedisCrawlerQueueServiceTest {

    @Autowired
    private ICrawlerQueueService crawlerQueueService;
    
    
    @Test
    public void testTaskQueue() {
    	//1-发布任务
    	 List<CrawlerRequest> requestsList=new ArrayList<>();
         for(int i=0;i<100;i++) {
              CrawlerRequest request = new CrawlerRequest(i+"","www.baidu.com/id="+i, "baidu.com", "post");
              Map data = new HashedMap();
              data.put("name", "liushuishang");
              request.setData(data);
              System.out.println(request.getHashCode());
              requestsList.add(request);
        }
         
         
        Boolean flag = crawlerQueueService.pushTasksToWaitingQueue(requestsList,true);
        
        String workerid = "worker-1";
        
        //2-1 分配任务
        List<CrawlerRequest> crawlerRequests = crawlerQueueService.fetchTasksFromWaitingQueue(2);
        crawlerQueueService.moveWaitingTaskToRunningQueue(workerid, crawlerRequests);
        
        crawlerRequests = crawlerQueueService.fetchTasksFromWaitingQueue(4);
        crawlerQueueService.moveWaitingTaskToRunningQueue(workerid, crawlerRequests);
        
        crawlerRequests = crawlerQueueService.fetchTasksFromWaitingQueue(3);
        crawlerQueueService.moveWaitingTaskToRunningQueue(workerid, crawlerRequests);
        
        crawlerRequests = crawlerQueueService.fetchTasksFromWaitingQueue(18);
        crawlerQueueService.moveWaitingTaskToRunningQueue(workerid, crawlerRequests);
        
        crawlerRequests = crawlerQueueService.fetchTasksFromWaitingQueue(3);
        crawlerQueueService.moveWaitingTaskToRunningQueue(workerid, crawlerRequests);
        
        crawlerRequests = crawlerQueueService.fetchTasksFromWaitingQueue(4);
        crawlerQueueService.moveWaitingTaskToRunningQueue(workerid, crawlerRequests);
        
        crawlerRequests = crawlerQueueService.fetchTasksFromWaitingQueue(5);
        crawlerQueueService.moveWaitingTaskToRunningQueue(workerid, crawlerRequests);
        
        //3-1更新运行中的任务
        String spiderId = crawlerRequests.get(0).getHashCode();
        CrawlerResult crawlerResult = new CrawlerResult(CrawlerResult.RESULT_CODE_UPDATECOUNT, spiderId, null,"任务更新");
        Map<String,Object> data = Maps.newHashMap();
		data.put("count", 5);
		crawlerResult.setData(data);
        crawlerQueueService.updateRunningTask(crawlerResult);
        
        //3-2更新运行中的任务
        crawlerResult = new CrawlerResult(CrawlerResult.RESULT_CODE_UPDATECOUNT, spiderId, null,"任务更新");
        data = Maps.newHashMap();
		data.put("count", 10);
		crawlerResult.setData(data);
        crawlerQueueService.updateRunningTask(crawlerResult);
        
        
        //4-更新运行中任务为完成任务
        crawlerResult = new CrawlerResult(CrawlerResult.RESULT_CODE_FINISH, spiderId, null,"任务完成");
        crawlerQueueService.moveRunningTaskToSuccessQueue(crawlerResult);
        
        //5-添加失败任务链接
        crawlerResult = new CrawlerResult(CrawlerResult.RESULT_CODE_FAIL, spiderId, null,"任务失败");
        crawlerQueueService.moveRunningTaskToFailQueue(crawlerResult.getKey(), crawlerResult.getMessage());
    	
    	
    }
    

   /* @Test
    public  void testAcceptAdminTask()
    {
        List<CrawlerRequest> requestsList=new ArrayList<>();
       // for(int i=0;i<300;i++) {
            CrawlerRequest request = new CrawlerRequest("","www.baidu.com/id="+System.currentTimeMillis() , "baidu.com", "post");
            Map data = new HashedMap();
            data.put("name", "liushuishang");
            request.setData(data);
            requestsList.add(request);
            crawlerQueueService.pushTasksToWaitingQueue(requestsList, false);
           // requestsList.clear();
      //  }
    }*/

    /*@Test
    public void fetchTasksFromWaitingQueueTest()
    {
        Assert.assertEquals(crawlerQueueService.fetchTasksFromWaitingQueue(1).size(), 1);
    }
     
    @Test
    public void moveWaitingTaskToRunningQueueTest()
    {
        Assert.assertTrue(crawlerQueueService.moveWaitingTaskToRunningQueue("192.168.1.2", crawlerQueueService.fetchTasksFromWaitingQueue(100)));
    }

    @Test
    public void moveRunningTaskToFailQueueTest()
    {
        Assert.assertTrue(crawlerQueueService.moveRunningTaskToFailQueue("769b077660d7b995f9a84de53da67554c0c70238","执行失败！"));
    }
    @Test
    public void moveRunningTaskToSuccessQueueTest()
    {

    	CrawlerResult cr = new CrawlerResult();
    	cr.setKey("");
        Assert.assertTrue(crawlerQueueService.moveRunningTaskToSuccessQueue(cr));
    }
    @Test
    public void refreshBreakedQueueTest()
    {
        crawlerQueueService.refreshBreakedQueue(60000L);
    }*/

}
