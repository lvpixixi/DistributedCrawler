package io.renren.modules.spider.service;

import java.util.List;
import java.util.Map;

import io.renren.common.utils.PageUtils;

public interface IUnpackRecordService {

	PageUtils searchByQuery(Map<String, Object> params);

	void send(String params) throws Exception;
	
}
