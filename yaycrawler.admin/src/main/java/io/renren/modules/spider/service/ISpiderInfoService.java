package io.renren.modules.spider.service;

import java.util.List;
import java.util.Map;

import io.renren.modules.spider.entity.SpiderInfo;


/**
 * 采集模板服务接口
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */
public interface ISpiderInfoService {
	
	
	/**
	 * 查询记录数
	 * @param query
	 * @return
	 */
	public List<SpiderInfo> queryList(Map<String, Object> query);	
	
	/**
	 * 根据id删除数据
	 * @param domain
	 * @return
	 */
	public void deleteById(String[] ids);
	
	/**
     * 根据爬虫模板id获取指定爬虫模板
     * @param id 爬虫模板id
     * @return
     */
    public SpiderInfo getById(String id);
    
    /**
     * 创建爬虫模板
     *
     * @param spiderInfo 爬虫模板实体
     * @return 爬虫模板id
     */
    public void save(SpiderInfo spiderInfo);
    
    /**
     * 更新爬虫模板
     *
     * @param spiderInfo 爬虫模板实体
     * @return 爬虫模板id
     */
    public void update(SpiderInfo spiderInfo)throws Exception;	

	/**
	 * 查询记录数
	 * @param map
	 * @return
	 */
	public int queryTotal(Map<String, Object> map);
   
}
