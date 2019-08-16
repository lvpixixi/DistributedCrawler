package io.renren.modules.spider.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bscrawler.spider.util.UUIDGenerator;
import io.renren.modules.spider.dao.SpiderSiteDao;
import io.renren.modules.spider.dao.SpiderSiteProjectDao;
import io.renren.modules.spider.entity.SpiderSiteEntity;
import io.renren.modules.spider.entity.SpiderSiteProjectEntity;
import io.renren.modules.spider.service.SpiderSiteService;
import io.renren.modules.spider.utils.HttpMsg;
import io.renren.modules.spider.utils.HttpUtils;
import io.renren.modules.sys.service.SysConfigService;



@Service("spiderSiteService")
public class SpiderSiteServiceImpl implements SpiderSiteService {
	@Autowired
	private SpiderSiteDao spiderSiteDao;
	
	@Autowired
	private  SpiderSiteProjectDao spiderSiteProjectDao;
	
	@Autowired
	private SysConfigService sysConfigService;
	
	@Override
	public SpiderSiteEntity queryObject(String id){
		return spiderSiteDao.queryObject(id);
	}
	
	@Override
	public List<SpiderSiteEntity> queryList(Map<String, Object> map){
		return spiderSiteDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return spiderSiteDao.queryTotal(map);
	}
	
	@Override
	public void save(SpiderSiteEntity spiderSite){
		String siteId = UUIDGenerator.generate();
		spiderSite.setId(siteId);
		spiderSiteDao.save(spiderSite);
		List<Long> projectIdList = spiderSite.getProjectIdList();
		if(projectIdList!=null) {
			if(projectIdList!=null) {
				this.updateProjects(siteId, projectIdList);
			}
		}
	}
	
	@Override
	public void update(SpiderSiteEntity spiderSite){
		spiderSiteDao.update(spiderSite);
		List<Long> projectIdList = spiderSite.getProjectIdList();
		if(projectIdList!=null) {
			this.updateProjects(spiderSite.getId(), projectIdList);
		}
	}
	
	private void updateProjects(String siteId,List<Long> projectIdList) {
		//先删除用户与角色关系
		spiderSiteProjectDao.delete(siteId);
		
		//保存用户与角色关系
		Map<String, Object> map = new HashMap<>();
		map.put("siteId", siteId);
		map.put("projectIdList", projectIdList);
		spiderSiteProjectDao.save(map);
	}
	
	@Override
	public void delete(String id){
		spiderSiteDao.delete(id);
	}
	
	@Override
	public void deleteBatch(String[] ids){
		spiderSiteDao.deleteBatch(ids);
	}

	@Override
	public List<Long> getProjectIdsBySiteId(String siteId) {
		Map<String, Object> map = new HashMap<>();
		map.put("siteId", siteId);
		List<SpiderSiteProjectEntity> list = spiderSiteProjectDao.queryList(map);
		List<Long> ids = Lists.newArrayList();
		for(SpiderSiteProjectEntity entity:list) {
			ids.add(entity.getProjectId());
		}
		return ids;
	}

	@Override
	public void checkUrls(String[] ids) {
		
		String proxyHost = sysConfigService.getValue("proxyHost");
		int proxyPort= Integer.parseInt(sysConfigService.getValue("proxyPort"));
		List<SpiderSiteEntity> list = null;
		if(ids!=null&&ids.length>0) {
			list = spiderSiteDao.queryListByIds(ids);
		}else {
			list = spiderSiteDao.queryList(Maps.newHashMap());
		}
		
		for(SpiderSiteEntity entity:list) {
			String url = entity.getUrl();
			//不使用代理
			HttpMsg rspMsg = HttpUtils.doCheck(url,null,"",0);
			entity.setRspCode(rspMsg.getResponseCode());
			//访问正常
			if(rspMsg.isReachAble()) {
				//正常访问1
				entity.setStatus(1);
				spiderSiteDao.update(entity);
			}else {
				//代理访问2
				rspMsg = HttpUtils.doCheck(url,null,proxyHost,proxyPort);
				if(rspMsg.isReachAble()) {
					entity.setStatus(2);
					spiderSiteDao.update(entity);
				//访问失败3
				}else {
					entity.setStatus(0);
					
					entity.setNote(rspMsg.getErrorMsg());
					spiderSiteDao.update(entity);
				}
			}
		}
	}
	
}
