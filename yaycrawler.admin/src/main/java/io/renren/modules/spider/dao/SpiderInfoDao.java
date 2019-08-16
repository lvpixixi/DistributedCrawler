package io.renren.modules.spider.dao;

import org.apache.ibatis.annotations.Mapper;

import io.renren.modules.spider.entity.SpiderInfo;
import io.renren.modules.sys.dao.BaseDao;




/**
 * 爬取信息
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */

@Mapper
public interface SpiderInfoDao extends BaseDao<SpiderInfo> {

}
