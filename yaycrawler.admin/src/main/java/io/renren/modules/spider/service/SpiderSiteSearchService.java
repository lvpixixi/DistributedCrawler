package io.renren.modules.spider.service;

import io.renren.modules.spider.entity.SpiderSiteSearchEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-13 16:06:19
 */
public interface SpiderSiteSearchService {
	
	SpiderSiteSearchEntity queryObject(String id);
	
	List<SpiderSiteSearchEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SpiderSiteSearchEntity spiderSiteSearch);
	
	void update(SpiderSiteSearchEntity spiderSiteSearch);
	
	void delete(String id);
	
	void deleteBatch(String[] ids);
}
