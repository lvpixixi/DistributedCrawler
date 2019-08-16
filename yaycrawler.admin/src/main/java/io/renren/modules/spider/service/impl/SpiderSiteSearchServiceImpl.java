package io.renren.modules.spider.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bscrawler.spider.util.UUIDGenerator;
import io.renren.modules.spider.dao.SpiderSiteSearchDao;
import io.renren.modules.spider.entity.SpiderSiteSearchEntity;
import io.renren.modules.spider.service.SpiderSiteSearchService;



@Service("spiderSiteSearchService")
public class SpiderSiteSearchServiceImpl implements SpiderSiteSearchService {
	@Autowired
	private SpiderSiteSearchDao spiderSiteSearchDao;
	
	@Override
	public SpiderSiteSearchEntity queryObject(String id){
		return spiderSiteSearchDao.queryObject(id);
	}
	
	@Override
	public List<SpiderSiteSearchEntity> queryList(Map<String, Object> map){
		return spiderSiteSearchDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return spiderSiteSearchDao.queryTotal(map);
	}
	
	@Override
	public void save(SpiderSiteSearchEntity spiderSiteSearch){
		spiderSiteSearch.setId(UUIDGenerator.generate());
		spiderSiteSearch.setSearchTime(new Date());
		spiderSiteSearchDao.save(spiderSiteSearch);
	}
	
	@Override
	public void update(SpiderSiteSearchEntity spiderSiteSearch){
		spiderSiteSearch.setSearchTime(new Date());
		spiderSiteSearchDao.update(spiderSiteSearch);
	}
	
	@Override
	public void delete(String id){
		spiderSiteSearchDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		spiderSiteSearchDao.deleteBatch(ids);
	}
	
}
