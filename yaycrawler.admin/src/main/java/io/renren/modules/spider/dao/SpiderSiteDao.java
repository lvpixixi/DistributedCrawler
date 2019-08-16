package io.renren.modules.spider.dao;

import io.renren.modules.spider.entity.SpiderSiteEntity;
import io.renren.modules.sys.dao.BaseDao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-09 09:08:25
 */
@Mapper
public interface SpiderSiteDao extends BaseDao<SpiderSiteEntity> {

	List<SpiderSiteEntity> queryListByIds(String[] ids);
	
}
