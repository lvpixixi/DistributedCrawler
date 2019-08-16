package io.renren.modules.spider.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.renren.common.utils.R;
import io.renren.modules.spider.communication.MasterActor;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.quartz.QuartzScheduleService;
import io.renren.modules.spider.service.ISpiderInfoService;
import io.renren.modules.spider.service.impl.CrawlerResultRetrivalService;
import io.renren.modules.spider.service.impl.TaskService;
import io.renren.modules.sys.controller.AbstractController;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.QueueQueryParam;
import yaycrawler.common.utils.UrlUtils;

/**
 * 工作节点监控控制器
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2018-09-11 20:24:12
 */
@RestController
@RequestMapping("/task")
public class TaskController extends AbstractController {

    @Autowired
    private MasterActor masterActor;

    @Autowired
    private CrawlerResultRetrivalService resultRetrivalService;

    @Autowired
    private QuartzScheduleService quartzScheduleService;

    @Autowired
    private TaskService taskService;
    
	@Autowired
	private ISpiderInfoService spiderInfoService;
	
	

    @RequestMapping("/publishTask")
    public R postPublishTask(@RequestBody Map<String, Object> data) {
        String id = MapUtils.getString(data, "spiderInfoId");
        String workerId = MapUtils.getString(data, "workerId");
        
        SpiderInfo spiderInfo = spiderInfoService.getById(id);
        
    	String domain= spiderInfo.getDomain();//MapUtils.getString(taskMap, "pageUrl");
        String method = "get";//MapUtils.getString(taskMap, "method");
        
        List<CrawlerRequest> reqs = new ArrayList<>();
        //String paramJson = MapUtils.getString(taskMap, "paramsJson");
       /* Map<String, Object> data = new HashMap<>();
        if (!StringUtils.isBlank(paramJson)) {
            data = JSON.parseObject(paramJson, Map.class);
        }*/
        CrawlerRequest crawlerRequest = new CrawlerRequest(id,domain,domain, method.toUpperCase());
        //不选择自动分配
        if(!workerId.equals("0")) {
        	crawlerRequest.setWorker(workerId);	
        }
        
        reqs.add(crawlerRequest);
        


        boolean flag = masterActor.publishTasks(reqs.toArray(new CrawlerRequest[] {}));
        if(flag) {
        	return R.ok();
        }else {
        	return R.error("执行失败！");
        }
    }
    
    @RequestMapping("/createQuartzJob")
    public R createQuartzJob(@RequestBody Map<String, Object> data) {
    	String spiderInfoId = (String)data.get("spiderInfoId");
    	String cronExp = (String)data.get("cronExp");
    	
    	SpiderInfo spiderInfo =spiderInfoService.getById(spiderInfoId);
        taskService.createQuartzJob(spiderInfo, cron2QuartzCron(cronExp));
        return R.ok();
    }
    
    private String cron2QuartzCron(String cronExp){
    	String[] crons = ("0 "+cronExp).split(" ");
    	if(crons[3].equals("*")&&!crons[5].equals("*")){
    		crons[3] = "?";
    	}else if(crons[3].equals("*")&&crons[5].equals("*")){
    		crons[5] = "?";
    	}else{
    		crons[5] = "?";
    	}
    	
    	return crons[0]+" "+crons[1]+" "+crons[2]+" "+crons[3]+" "+crons[4]+" "+crons[5];
    }
    
    	
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
        Object data = null;
        String name = MapUtils.getString(params,"name");
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        
        QueueQueryParam queryParam = new QueueQueryParam();
        queryParam.setName(name);
        queryParam.setPageIndex(page);
        queryParam.setPageSize(limit);
        
        if (StringUtils.equalsIgnoreCase(name, "fail")) {
            data = masterActor.retrievedFailQueueRegistrations(queryParam);
        } else if (StringUtils.equalsIgnoreCase(name, "success")) {
            data = masterActor.retrievedSuccessQueueRegistrations(queryParam);
        } else if (StringUtils.equalsIgnoreCase(name, "waiting")) {
            data = masterActor.retrievedWaitingQueueRegistrations(queryParam);
        } else if (StringUtils.equalsIgnoreCase(name, "running")) {
            data = masterActor.retrievedRunningQueueRegistrations(queryParam);
        }
        return R.ok().put("page", data);
    }

    @RequestMapping(value = "/viewCrawlerResult", method = RequestMethod.POST)
    @ResponseBody
    public Object viewCrawlerResult(@RequestBody Map map) {
        String pageUrl = MapUtils.getString(map, "pageUrl");
        String taskId = MapUtils.getString(map, "taskId");
        Assert.notNull(pageUrl);
        Assert.notNull(taskId);

        return resultRetrivalService.retrivalByTaskId(UrlUtils.getDomain(pageUrl).replace(".", "_"), taskId);
    }

    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    @ResponseBody
    public Object upload(HttpServletRequest request, HttpServletResponse response, @RequestParam("files") MultipartFile[] files)
    {
        MultipartFile file = files[0];
        if(file==null) return false;
        String filename = file.getOriginalFilename();
        if (filename == null || "".equals(filename))
        {
            return null;
        }
        Map map;
        if(StringUtils.endsWithAny(file.getOriginalFilename(),".csv",".txt"))
            map = taskService.insertCSV(file);
        else
            map = taskService.insertExcel(file);
        return map;
    }



}
