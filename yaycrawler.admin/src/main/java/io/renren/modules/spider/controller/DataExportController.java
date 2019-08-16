package io.renren.modules.spider.controller;

import com.google.common.collect.Maps;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.spider.service.IImport2CloudService;
import io.renren.modules.spider.service.INewsQueryService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author wgshb
 *
 */
@Controller
@RequestMapping("/dataexport")
public class DataExportController {

	private Logger LOG = LoggerFactory.getLogger(DataExportController.class);
	
	@Autowired
	private IImport2CloudService import2CloudService;
	
	@Autowired
	private INewsQueryService newsQueryService;
	
	/**
	 * 获取mongo数据库的对象列表
	 */
	@RequestMapping("/select")
	@ResponseBody
	public R select(){
		Set<String> entitys = newsQueryService.getEntitys();
		List list = new ArrayList();
		for(String entity:entitys) {
			Map<String,Object> obj = Maps.newHashMap();
			obj.put("name", entity);
			obj.put("code", entity);
			list.add(obj);
		}
		return R.ok().put("list", list);
	}
	
	/**
	 * 获取mongo数据库对应的项目
	 */
	@RequestMapping("/getProject")
	@ResponseBody
	public R getProject(@RequestBody String params){
		String project = newsQueryService.getProject(params);
		return R.ok().put("project", project);
	}
	
	/**
	 * 查询符合条件的数据信息
	 */
	@RequestMapping("/searchByQuery")
	@ResponseBody
	public R searchByQuery(@RequestParam Map<String, Object> params) {
		int pageNumber = Integer.parseInt(params.get("page").toString());
    	int pageSize = Integer.parseInt(params.get("limit").toString());
    	int offset = (pageNumber-1)*pageSize;
    	String entityName = MapUtils.getString(params, "entity");
    	
    	// 添加额外的查询条件
    	params.put("status", "1");
    	
    	List searchByQuery = newsQueryService.searchByQuery(entityName, params, offset, pageSize);
    	long count = newsQueryService.searchCountByQuery(entityName,params);
    	PageUtils pageUtil = new PageUtils(searchByQuery, (int)count, pageSize, pageNumber);
    	return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 打包数据到本地
	 */
	@RequestMapping("/down2local")
	@ResponseBody
	public R down2local(@RequestBody String params){
		String project = newsQueryService.down2local(params);
		if("1".equals(project)) {
			return R.ok();
		}else{
			return R.error();
		}
	}
	
	/**
	 * 打包数据到本地
	 */
	@RequestMapping("/extract")
	@ResponseBody
	public Map extract(@RequestBody String params){
		return newsQueryService.extract(params);
	}
	
	/**
	 * 生成报告
	 */
	@RequestMapping("/report")
	@ResponseBody
	public R report(@RequestBody String params){
		String project = newsQueryService.report(params);
		if("1".equals(project)) {
			return R.ok();
		}else{
			return R.error();
		}
	}
	
}
