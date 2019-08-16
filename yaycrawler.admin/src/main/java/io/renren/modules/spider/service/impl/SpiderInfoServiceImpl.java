package io.renren.modules.spider.service.impl;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bscrawler.spider.util.UUIDGenerator;
import io.renren.modules.spider.dao.SpiderInfoDao;
import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.spider.entity.SpiderProjectEntity;
import io.renren.modules.spider.service.ISpiderInfoService;
import io.renren.modules.spider.service.SpiderInfoProjectService;
import io.renren.modules.spider.service.SpiderProjectService;



/**
 * 采集模板服务类
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2016年9月18日 上午9:46:09
 */
@Service("spiderInfoService")
public class SpiderInfoServiceImpl implements ISpiderInfoService {
	@Autowired
	private SpiderInfoDao spiderInfoDao;
	@Autowired
	private SpiderInfoProjectService spiderInfoProjectService;
	
	@Autowired
	private SpiderProjectService spiderProjectService;

	@Override
	public List<SpiderInfo> queryList(Map<String, Object> query) {
		return spiderInfoDao.queryList(query);
	}

	@Override
	public void deleteById(String[] ids) {
		spiderInfoDao.deleteBatch(ids);
		
	}

	@Override
	public SpiderInfo getById(String id) {
		return spiderInfoDao.queryObject(id);
	}

	@Override
	public void save(SpiderInfo spiderInfo) {
		spiderInfo.setId(UUIDGenerator.generate());
		String jsonData = spiderInfo.getJsonData().replaceAll("&amp;", "&");
		spiderInfo.setJsonData(jsonData);
		Long[] ids = new Long[spiderInfo.getProjectIdList().size()];
		List<SpiderProjectEntity> projects = spiderProjectService.queryListByIds(spiderInfo.getProjectIdList().toArray(ids));
		JSONObject jsonProject = new JSONObject();
		for(SpiderProjectEntity entity:projects){
			jsonProject.put(entity.getId()+"", entity.getName());
		}
		spiderInfo.setJsonProject(jsonProject.toString());
		spiderInfoDao.save(spiderInfo);
		
		//保存网站配置和项目关系
		//spiderInfoProjectService.saveOrUpdate(spiderInfo.getId(), spiderInfo.getProjectIdList());
	}

	@Override
	public void update(SpiderInfo spiderInfo) throws Exception {
		String jsonData = spiderInfo.getJsonData().replaceAll("&amp;", "&");
		spiderInfo.setJsonData(jsonData);
		//Long[] ids = new Long[spiderInfo.getProjectIdList().size()];
		//List<SpiderProjectEntity> projects = spiderProjectService.queryListByIds(spiderInfo.getProjectIdList().toArray(ids));
	/*	JSONObject jsonProject = new JSONObject();
		for(SpiderProjectEntity entity:projects){
			jsonProject.put(entity.getId()+"", entity.getName());
		}
		spiderInfo.setJsonProject(jsonProject.toString());*/
		spiderInfoDao.update(spiderInfo);
		//保存网站配置和项目关系
		//spiderInfoProjectService.saveOrUpdate(spiderInfo.getId(), spiderInfo.getProjectIdList());
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return spiderInfoDao.queryTotal(map);
	}
}
