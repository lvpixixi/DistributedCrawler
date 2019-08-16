package io.renren.modules.spider.service;

import io.renren.modules.spider.entity.SpiderProjectEntity;

import java.util.List;
import java.util.Map;

/**
 * InnoDB free: 3971072 kB
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2018-09-06 20:24:12
 */
public interface SpiderProjectService {
	
	SpiderProjectEntity queryObject(Long id);
	
	List<SpiderProjectEntity> queryListByIds(Long[] ids);
	
	List<SpiderProjectEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SpiderProjectEntity spiderProject);
	
	void update(SpiderProjectEntity spiderProject);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
	
	/**
	 * 查询所有的项目
	 */
	List<SpiderProjectEntity> getProjects();
}
