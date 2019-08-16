package io.renren.modules.spider.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.renren.modules.spider.entity.UnpackRecord;
import io.renren.modules.sys.dao.BaseDao;

@Mapper
public interface UnpackRecordDao extends BaseDao<UnpackRecord> {

	List getContent(Map<String, Object> params);

	long getCount(Map<String, Object> params);

	UnpackRecord getOneById(@Param("id")String id);

}
