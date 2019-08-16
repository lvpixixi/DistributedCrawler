package io.renren.modules.spider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.entity.SpiderInfo;
import yaycrawler.common.utils.CSVUtils;
import yaycrawler.common.utils.ExcelUtils;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2016/6/8.
 */
@Service
public class TaskService {
	
	@Autowired
	private ScheduleJobService scheduleJobService;
	
	/**
     * 创建定时任务
     *
     * @param spiderInfoId  爬虫模板id
     * @param hoursInterval 每几小时运行一次
     */
    public String createQuartzJob(SpiderInfo spiderInfo, String cronExp) {
        ScheduleJobEntity scheduleJob = new ScheduleJobEntity();
        scheduleJob.setBeanName("CrawlerRequestJob");
        scheduleJob.setMethodName("execute");
        scheduleJob.setParams(spiderInfo.toString());
        scheduleJob.setCronExpression(cronExp);
        scheduleJob.setRemark(spiderInfo.getSiteName());
        scheduleJobService.save(scheduleJob);
        return spiderInfo.getId()+"";
    }

    

    public Map insertExcel(MultipartFile file) {
        Map result = new HashMap();
        try {
            InputStream input = file.getInputStream();
            List<Map> requestList = ExcelUtils.importExcel(input);
            List<Map> columns = new ArrayList<>();
            if(requestList != null && requestList.size() > 0) {
                Map<String,String> request = requestList.get(0);
                for (String key:request.keySet()) {
                    Map column = new HashMap();
                    column.put("field", key);
                    column.put("title",key);
                    columns.add(column);
                }
                result.put("total",requestList.size());
            } else {
                result.put("total",0);
            }
            result.put("columns",columns);
            result.put("data",requestList);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", true);
            e.printStackTrace();
        }
        return result;
    }

    public Map insertCSV(MultipartFile file) {
        Map result = new HashMap();
        try {
            InputStream input = file.getInputStream();
            List<Map> requestList = CSVUtils.importCsv(input);
            List<Map> columns = new ArrayList<>();
            if(requestList != null && requestList.size() > 0) {
                Map<String,String> request = requestList.get(0);
                for (String key:request.keySet()) {
                    Map column = new HashMap();
                    column.put("field", key);
                    column.put("title",key);
                    columns.add(column);
                }
                result.put("total",requestList.size());
            } else {
                result.put("total",0);
            }
            result.put("columns",columns);
            result.put("data",requestList);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
