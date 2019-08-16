package io.renren.modules.spider.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.spider.quartz.QuartzScheduleService;
import io.renren.modules.spider.quartz.model.ScheduleJobInfo;
import io.renren.modules.spider.utils.Sublist;
import io.renren.modules.sys.controller.AbstractController;


/**
 * Created by ucs_yuananyun on 2016/6/6.
 */
@RestController
@RequestMapping("/spider/job")
public class JobController extends AbstractController {

    @Autowired
    private QuartzScheduleService quartzScheduleService;
  

    @RequestMapping(value = "/getPlannedJobList", method = RequestMethod.GET)
    @ResponseBody
    public Object getPlannedJobList() {
        return quartzScheduleService.getScheduledJobList();
    }
    
	/**
	 * 获取待运行的作业列表
	 * @param params
	 * @return
	 */
	@RequestMapping("/getPlannedJobList")
	public R getPlannedJobList(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
        Object data = quartzScheduleService.getScheduledJobList();
        PageUtils pageUtil = null;
        if(data instanceof List){
        	List list = (List)data;
        	int total = list.size();
        	List pageList = Sublist.getPage(list, query.getPage(), query.getLimit());
    		pageUtil = new PageUtils(pageList, total, query.getLimit(), query.getPage());
        }else{
        	pageUtil = new PageUtils(new ArrayList(), 0, query.getLimit(), query.getPage());
        }
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 获取待运行的作业列表
	 * @param params
	 * @return
	 */
	@RequestMapping("/getRunningJobList")
	public R getRunningJobList(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
        Object data = quartzScheduleService.getRunningJobList();
        PageUtils pageUtil = null;
        if(data instanceof List){
        	List list = (List)data;
        	int total = list.size();
        	List pageList = Sublist.getPage(list, query.getPage(), query.getLimit());
    		pageUtil = new PageUtils(pageList, total, query.getLimit(), query.getPage());
        }else{
        	pageUtil = new PageUtils(new ArrayList(), 0, query.getLimit(), query.getPage());
        }
		return R.ok().put("page", pageUtil);
	}

	
	/**
	 * 保存调度信息
	 */
	@RequestMapping("/saveScheduleJob")
	public R saveScheduleJob(@RequestBody ScheduleJobInfo jobInfo){
		quartzScheduleService.updateJob(jobInfo);
		return R.ok();
	}

  
	/**
	 * 暂停调度
	 */
	@RequestMapping("/pauseJob")
	public R pauseJob(@RequestBody ScheduleJobInfo jobInfo){
		quartzScheduleService.pauseJob(jobInfo.getJobName(), jobInfo.getJobGroup());
		return R.ok();
	}
	
	/**
	 * 恢复调度
	 */
	@RequestMapping("/resumeJob")
	public R resumeJob(@RequestBody ScheduleJobInfo jobInfo){
		quartzScheduleService.resumeJob(jobInfo.getJobName(), jobInfo.getJobGroup());
		return R.ok();
	}

	/**
	 * 删除调度
	 */
	@RequestMapping("/deleteJob")
	public R deleteJob(@RequestBody ScheduleJobInfo jobInfo){
		quartzScheduleService.deleteJob(jobInfo.getJobName(), jobInfo.getJobGroup());
		return R.ok();
	}
}
