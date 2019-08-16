package io.renren.modules.spider.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.renren.modules.spider.dao.SpiderInfoProjectDao;
import io.renren.modules.spider.entity.SpiderProjectEntity;
import io.renren.modules.spider.service.SpiderInfoProjectService;



/**
 * 项目和网站配置对应关系
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2018年9月07日 上午9:43:24
 */
@Service("projectSpiderInfoService")
public class SpiderInfoProjectServiceImpl implements SpiderInfoProjectService {
	@Autowired
	private  SpiderInfoProjectDao spiderInfoProjectDao;

	@Override
	public void saveOrUpdate(String spiderinfoId, List<Long> projectIdList) {
		if(projectIdList.size() == 0){
			return ;
		}
		
		//先删除网站配置和项目关系
		spiderInfoProjectDao.delete(spiderinfoId);
		
		//保存网站配置和项目关系
		Map<String, Object> map = new HashMap<>();
		map.put("spiderinfoId", spiderinfoId);
		map.put("projectIdList", projectIdList);
		spiderInfoProjectDao.save(map);
	}

	@Override
	public List<Long> queryProjectIdList(String spiderInfoId) {
		return spiderInfoProjectDao.queryProjectIdList(spiderInfoId);
	}


}
