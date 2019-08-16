package io.renren.modules.spider.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.model.CrawlerConfig;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.model.SchedulerInfo;
import io.renren.modules.spider.model.SpiderRuntimeInfo;
import io.renren.modules.spider.service.ISpiderInfoService;
import io.renren.modules.spider.service.ISpiderService;
import yaycrawler.dao.domain.FieldParseRule;

/**
 * CommonsWebpageDownloadController
 *
 * @author Gao Shen
 * @version 16/4/8
 */
@RestController
@RequestMapping("/spider")
public class SpiderController{
	private static final Logger LOG = LoggerFactory.getLogger(SpiderController.class);
    @Autowired
    private ISpiderService spiderService;
    
    @Autowired
    private ISpiderInfoService spiderInfoService;

    /**
     * 启动爬虫
     *
     * @param spiderInfoJson 使用json格式进行序列化的spiderinfo
     * @return 任务id
     */
    @RequestMapping("/start")
    public R start(@RequestBody String spiderInfoId) {
        spiderService.start(spiderInfoId);
        return R.ok();
    }
    
    /**
     * 根据爬虫模板ID批量启动任务
     *
     * @param spiderInfoIdList 爬虫模板ID列表
     * @return 任务id列表
     */
    @RequestMapping("/startAll")
    public R startAll(String spiderInfoIdList) {
        spiderService.startAll(Lists.newArrayList(spiderInfoIdList.split(",")));
        return R.ok();
    }
    

    /**
     * 停止爬虫
     *
     * @param uuid 任务id(爬虫uuid)
     * @return
     */
    @RequestMapping("/stop")    
    public R stop(@RequestBody String taskId) {
    	spiderService.stop(taskId);
        return R.ok();
    }

    /**
     * 获取爬虫运行时信息
     *
     * @param uuid 爬虫uuid 任务id
     * @return
     */
    @RequestMapping(value = "runtimeInfo", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public R runtimeInfo(String uuid, @RequestParam(value = "containsExtraInfo", required = false, defaultValue = "false") boolean containsExtraInfo) {
    	SpiderRuntimeInfo spider =  spiderService.runtimeInfo(uuid, containsExtraInfo);
        return R.ok().put("spider", spider);
    }

    /**
     * 列出所有爬虫的运行时信息
     *
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public R list(@RequestParam(value = "containsExtraInfo", required = false, defaultValue = "false") boolean containsExtraInfo) {
    	List<SpiderRuntimeInfo> spiders = spiderService.listRunSpider(containsExtraInfo);
    	int total = spiders.size();
		
		PageUtils pageUtil = new PageUtils(spiders, total, 100, 1);
    	return R.ok().put("page", pageUtil);
    }   
 
    /**
     * 测试爬虫模板
     *
     * @param spiderInfoJson
     * @return
     */
	@RequestMapping("/testSpiderInfo")
    public R testSpiderInfo(@RequestBody String[] spiderInfoIds) {
    	List<Map<String,Object>> records = null;
        List<String> columns = Lists.newArrayList();
        String spiderInfoId = spiderInfoIds[0];
		try {
			SpiderInfo spiderInfo = this.spiderInfoService.getById(spiderInfoId);
			CrawlerConfig cfg = CrawlerConfig.fromJSON(spiderInfo.getJsonData());
			records = spiderService.testSpiderInfo(cfg);
			List extractors = cfg.getPageProcessorConf().getExtractors();
			JSONObject json = (JSONObject)extractors.get(0);
			ObjectModelExtractor extractor = JSONObject.toJavaObject(json, ObjectModelExtractor.class);
			Set<FieldParseRule> fields = extractor.getFieldParseRules();
			for(FieldParseRule field:fields) {
				columns.add(field.getFieldName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("执行失败！");
		}
		R r = R.ok();
		r.put("records", records);
		r.put("columns", columns);
        return r;
    }


    @RequestMapping(value = "listScheduler", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public R listScheduler(@RequestParam(value = "containsExtraInfo", required = false, defaultValue = "false") boolean containsExtraInfo) {
    	List<SchedulerInfo> infos = spiderService.listAllQuartzJobs();
    	int total = infos.size();
		PageUtils pageUtil = new PageUtils(infos, total, 100, 1);
    	return R.ok().put("page", pageUtil);
    }   
 

    @RequestMapping("/createQuartzJob")
    public R createQuartzJob(@RequestBody Map<String, Object> data) {
    	String spiderInfoId = (String)data.get("spiderInfoId");
    	String cronExp = (String)data.get("cronExp");
        spiderService.createQuartzJob(spiderInfoId, cron2QuartzCron(cronExp));
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

    @RequestMapping("/removeQuartzJob")
    public R removeQuartzJob(String spiderInfoId) {
        spiderService.removeQuartzJob(spiderInfoId);
        return R.ok();
    }

    @RequestMapping("/checkQuartzJob")
    public R checkQuartzJob(String spiderInfoId) {
        spiderService.checkQuartzJob(spiderInfoId);
        return R.ok();
    }

    @RequestMapping("/exportQuartz")
    public void exportQuartz(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=commons-spider.quartz");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(spiderService.exportQuartz().getBytes());
        outputStream.close();
    }

    @RequestMapping("/importQuartz")
    public R importQuartz(String json) {
        spiderService.importQuartz(json);
        return R.ok();
    }
    
    @RequestMapping("/CalcRunTime")
    @ResponseBody
    public R CalcRunTime(@RequestBody String cron) {
        
        try {
        	CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
			cronTriggerImpl.setCronExpression(cron);
			Calendar calendar = Calendar.getInstance();
	        Date now = calendar.getTime();
	        calendar.add(Calendar.MONTH, 1);// 把统计的区间段设置为从现在到1月后的今天（主要是为了方法通用考虑)

	        // 这里的时间是根据corn表达式算出来的值
	        List<Date> dates = TriggerUtils.computeFireTimesBetween(
	                cronTriggerImpl, null, now,
	                calendar.getTime());
	        
	        SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
	        int index = 0;
	        List<String> dateList = new ArrayList<>();
            for (Date date : dates) {
            	dateList.add(dateFormat.format(date));
            	if(index++>5){
            		break;
            	}
            }
            return R.ok().put("data", dateList);
            
		} catch (ParseException e) {
			e.printStackTrace();
			return R.error(e.toString());
		}
    }
}
