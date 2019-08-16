package io.renren.modules.spider.service;

import io.renren.modules.spider.entity.SpiderSiteEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-09 09:08:25
 */
public interface SpiderSiteService {
	
	List<Long> getProjectIdsBySiteId(String id);
	
	SpiderSiteEntity queryObject(String id);
	
	List<SpiderSiteEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SpiderSiteEntity spiderSite);
	
	void update(SpiderSiteEntity spiderSite);
	
	void delete(String id);
	
	void deleteBatch(String[] ids);
	
	//检查url是否有效
	void checkUrls(String[] ids);
}
