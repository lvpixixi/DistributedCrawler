package io.renren.modules.spider.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.renren.modules.spider.entity.SpiderSiteSresultEntity;

/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-13 16:06:20
 */
public interface SpiderSiteSresultService {
	
	SpiderSiteSresultEntity queryObject(String id);
	
	List<SpiderSiteSresultEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SpiderSiteSresultEntity spiderSiteSresult);
	
	void update(SpiderSiteSresultEntity spiderSiteSresult);
	
	void delete(String id);
	
	void deleteBatch(String[] ids);
	/**
	 * 获取命中的网站
	 * @param sid
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	List<SpiderSiteSresultEntity> getHitSites(Map<String, Object> map);
	/**
	 * 获取命中网站的数量
	 * @param sid
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	int getHitSiteTotal(Map<String, Object> map);
	
}
