package io.renren.modules.spider.dao;

import io.renren.modules.spider.entity.SpiderSiteSresultEntity;
import io.renren.modules.sys.dao.BaseDao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-13 16:06:20
 */
@Mapper
public interface SpiderSiteSresultDao extends BaseDao<SpiderSiteSresultEntity> {

	List<SpiderSiteSresultEntity> queryDomain(Map<String, Object> map);
	
	int queryDomainTotal(Map<String, Object> map);
	
}
