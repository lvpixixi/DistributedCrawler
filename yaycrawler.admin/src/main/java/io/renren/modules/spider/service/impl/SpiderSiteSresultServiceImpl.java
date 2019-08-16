package io.renren.modules.spider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.renren.modules.spider.dao.SpiderSiteSresultDao;
import io.renren.modules.spider.entity.SpiderSiteSresultEntity;
import io.renren.modules.spider.service.SpiderSiteSresultService;



@Service("spiderSiteSresultService")
public class SpiderSiteSresultServiceImpl implements SpiderSiteSresultService {
	@Autowired
	private SpiderSiteSresultDao spiderSiteSresultDao;
	
	@Override
	public SpiderSiteSresultEntity queryObject(String id){
		return spiderSiteSresultDao.queryObject(id);
	}
	
	@Override
	public List<SpiderSiteSresultEntity> queryList(Map<String, Object> map){
		return spiderSiteSresultDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return spiderSiteSresultDao.queryTotal(map);
	}
	
	@Override
	public void save(SpiderSiteSresultEntity spiderSiteSresult){
		spiderSiteSresultDao.save(spiderSiteSresult);
	}
	
	@Override
	public void update(SpiderSiteSresultEntity spiderSiteSresult){
		spiderSiteSresultDao.update(spiderSiteSresult);
	}
	
	@Override
	public void delete(String id){
		spiderSiteSresultDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		spiderSiteSresultDao.deleteBatch(ids);
	}

	@Override
	public List<SpiderSiteSresultEntity> getHitSites(Map<String, Object> map) {
		return this.spiderSiteSresultDao.queryDomain(map);
	}

	@Override
	public int getHitSiteTotal(Map<String, Object> map) {
		return this.spiderSiteSresultDao.queryDomainTotal(map);
	}
	
}
