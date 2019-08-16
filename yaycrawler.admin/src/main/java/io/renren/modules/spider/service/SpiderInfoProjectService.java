package io.renren.modules.spider.service;

import java.util.List;

import io.renren.modules.spider.entity.SpiderProjectEntity;



/**
 * 项目和网站配置对应关系
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2018年9月07日 上午9:43:24
 */
public interface SpiderInfoProjectService {
	
	void saveOrUpdate(String spiderinfoId, List<Long> projectIdList);
	
	/**
	 * 根据用户ID，获取角色ID列表
	 */
	List<Long> queryProjectIdList(String spiderId);

	
	//void delete(Long userId);

}
