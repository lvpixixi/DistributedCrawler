package io.renren.modules.spider.dao;

import io.renren.modules.spider.entity.SpiderInfoProjectEntity;
import io.renren.modules.spider.entity.SpiderProjectEntity;
import io.renren.modules.sys.dao.BaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * InnoDB free: 3971072 kB
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2018-09-07 15:06:17
 */
@Mapper
public interface SpiderInfoProjectDao extends BaseDao<SpiderInfoProjectEntity> {
	
	/**
	 * 根据网站配置信息ID，获取项目ID列表
	 */
	List<Long> queryProjectIdList(String spiderInfoId);

}
