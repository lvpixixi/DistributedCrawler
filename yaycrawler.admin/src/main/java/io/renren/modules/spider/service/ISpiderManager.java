package io.renren.modules.spider.service;

import java.util.List;
import java.util.Map;

import javax.management.JMException;

import bscrawler.spider.model.CrawlerConfig;
import io.renren.modules.spider.model.SchedulerInfo;
import io.renren.modules.spider.model.SpiderRuntimeInfo;
import io.renren.modules.spider.model.Webpage;

/**
 * 爬虫管理服务接口
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */
public interface ISpiderManager {
	
    /**
     * 停止爬虫
     *
     * @param uuid
     */
	public void stop(String uuid);
	/**
     * 删除爬虫
     *
     * @param uuid
     */
	public void delete(String uuid);
	
    /**
     * 删除全部爬虫
     */
    public void deleteAll();
    
    /**
     * 启动爬虫
     * @param info
     * @return
     */
    public String start(CrawlerConfig info);
    
    /**
     * 测试爬虫模板
     *
     * @param info
     * @return
     */
    public List<Map<String,Object>> testSpiderInfo(CrawlerConfig info) throws JMException;
    
    /**
     * 列出所有爬虫的运行时信息
     *
     * @return
     */
    public List<SpiderRuntimeInfo> listAllSpiders(boolean containsExtraInfo);
    
    /**
     * 获取爬虫运行时信息
     *
     * @param uuid 爬虫uuid 任务id
     * @return
     */
    public SpiderRuntimeInfo getSpiderRuntimeInfo(String uuid, boolean containsExtraInfo);
    
    /**
     * 创建定时任务，每几个小时运行一次
     * @param spiderInfoId
     * @param hoursInterval
     * @return
     */
    public String createQuartzJob(String spiderInfoId, String cronExp);
    
    /**
     * 获取所有的定时任务列表
     * @return
     */
    public List<SchedulerInfo> listAllQuartzJobs();
    
    /**
     * 删除定时任务
     * @param spiderInfoId
     * @return
     */
    public String removeQuartzJob(String spiderInfoId);
    
    /**
     * 检查定时任务
     * @param spiderInfoId
     * @return
     */
    public String checkQuartzJob(String spiderInfoId);
    
    /**
     * 导出定时任务
     * @return
     */
    public String exportQuartz();
    
    /**
     * 导入定时任务
     * @param json
     */
    public void importQuartz(String json);

}
