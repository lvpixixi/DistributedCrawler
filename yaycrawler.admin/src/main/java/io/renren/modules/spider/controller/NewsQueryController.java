package io.renren.modules.spider.controller;


import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.spider.service.IAutoClassification;
import io.renren.modules.spider.service.INewsQueryService;
import io.renren.modules.spider.service.IRemoveDouble;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * NewsQueryController
 *
 * @author mfq
 * @version 18/4/2
 */
@RequestMapping("/newsquery")
@Controller
public class NewsQueryController {
	@Autowired
	private INewsQueryService newsQueryService;
	
	@Autowired
	private IRemoveDouble removeDouble;
	
	@Autowired
	private IAutoClassification utoClassification;
	
    private Logger LOG = LoggerFactory.getLogger(NewsQueryController.class);
    @RequestMapping("/searchByQuery")
    @ResponseBody
    public R searchByQuery(@RequestParam Map<String, Object> params){
    	int pageNumber = Integer.parseInt(params.get("page").toString());
    	int pageSize = Integer.parseInt(params.get("limit").toString());
    	int offset = (pageNumber-1)*pageSize;
    	String entityName = MapUtils.getString(params, "entity");
    	List searchByQuery = newsQueryService.searchByQuery(entityName, params, offset, pageSize);
    	long count = newsQueryService.searchCountByQuery(entityName,params);
    	PageUtils pageUtil = new PageUtils(searchByQuery, (int)count, pageSize, pageNumber);
    	return R.ok().put("page", pageUtil);
    }
    
    @RequestMapping("/searchDoubleList")
    @ResponseBody
    public R searchDoubleList(@RequestParam Map<String, Object> params){
    	int pageNumber = Integer.parseInt(params.get("page").toString());
    	int pageSize = Integer.parseInt(params.get("limit").toString());
    	int offset = (pageNumber-1)*pageSize;
    	String keyWord =  MapUtils.getString(params, "keyWord");
    	String entityName = MapUtils.getString(params, "entity");
    	params.put("status", removeDouble.STATUS_DOUBLE);
    	params.put("desc", "double_key");
    	
    	List<Map> searchByQuery = removeDouble.searchDoubleList(entityName,keyWord,params,offset,pageSize);
    	long count = removeDouble.searchDoubleCount(entityName,keyWord,params);
    	
    	PageUtils pageUtil = new PageUtils(searchByQuery, (int)count, pageSize, pageNumber);
    	return R.ok().put("page", pageUtil);
    }
    
    
    @RequestMapping("/searchByid")
    @ResponseBody
    public Map searchByid(@RequestBody String str){
    	Map<String, Object> params = (Map<String, Object>) JSON.parse(str);
    	String id = MapUtils.getString(params, "id");
    	String entity = MapUtils.getString(params, "entity");
    	return newsQueryService.searchByid(entity,id);
    }
    
    /**
	 * 对象列表
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
	 * 执行去重
	 */
	@RequestMapping("/removeDouble")
	@ResponseBody
	public R removeDouble(@RequestBody Map<String, Object> jsonParams) {
		
    	String entity = MapUtils.getString(jsonParams, "entity");
    	String begindateStr = MapUtils.getString(jsonParams, "begindate");
    	String enddateStr = MapUtils.getString(jsonParams, "enddate");
    	Date begindate = null;
    	Date enddate = null;
    	Integer distance = MapUtils.getInteger(jsonParams, "distance");
    	
    	//默认的相似距离
    	if(distance==null||distance==0) {
    		distance = 15;
    	}
    	int count =0;
		try{
			   if(!StringUtils.isEmpty(begindateStr)&&!StringUtils.isEmpty(enddateStr)) {
				   begindate = DateUtils.strToDate(begindateStr,DateUtils.DATE_PATTERN_yyyy_MM_dd);
				   enddate = DateUtils.strToDate(enddateStr,DateUtils.DATE_PATTERN_yyyy_MM_dd);
				}
				count = removeDouble.executeCompute(entity, begindate, enddate,distance);
			}catch(Exception e){
				e.printStackTrace();
				return R.error(e.getMessage());
			}
		
		return R.ok().put("count", count);
	}
	
	/**
	 * 执行自动分类
	 */
	@RequestMapping("/autoCategory")
	@ResponseBody
	public R autoCategory(@RequestBody Map<String, Object> jsonParams) {
		
    	String entity = MapUtils.getString(jsonParams, "entity");
    	String begindateStr = MapUtils.getString(jsonParams, "begindate");
    	String enddateStr = MapUtils.getString(jsonParams, "enddate");
    	Date begindate = null;
    	Date enddate = null;
    	
		try{
			   if(!StringUtils.isEmpty(begindateStr)&&!StringUtils.isEmpty(enddateStr)) {
				   begindate = DateUtils.strToDate(begindateStr,DateUtils.DATE_PATTERN_yyyy_MM_dd);
				   enddate = DateUtils.strToDate(enddateStr,DateUtils.DATE_PATTERN_yyyy_MM_dd);
				}
			   utoClassification.executeClassification(entity, begindate, enddate);
				//removeDouble.executeCompute(entity, fromdate, todate);
			}catch(Exception e){
				e.printStackTrace();
				return R.error(e.getMessage());
			}
		
		return R.ok();
	}
	
	/**
	 * 下载数据到本地
	 */
	@RequestMapping("/down2local")
	@ResponseBody
	public String down2Local(@RequestBody String jsonParams) {
		Map<String, Object> params = (Map<String, Object>) JSON.parse(jsonParams);
    	JSONArray arraies = (JSONArray) params.get("ids");
    	List<String> ids = new ArrayList<>();
    	for(int i = 0; i < arraies.size(); i++) {
    		ids.add(arraies.getString(i));
    	}
    	String entity = (String) params.get("entity");
    	String[] id = ids.toArray(new String[ids.size()]);
		return newsQueryService.down2Local(id, entity);
	}
	
	/**
	 * 修改数据
	 */
	@RequestMapping("/update")
	@ResponseBody
	public R update(@RequestBody String jsonParams){
		try{
	    	newsQueryService.update(jsonParams);
			}catch(Exception e){
				e.printStackTrace();
				return R.error(e.getMessage());
			}
		
		return R.ok();
	}
	
	/**
	 * 审核单条数据
	 */
	@RequestMapping("/check")
	@ResponseBody
	public R check(@RequestBody String jsonParams){
		try{
	    	newsQueryService.check(jsonParams);
			}catch(Exception e){
				e.printStackTrace();
				return R.error(e.getMessage());
			}
		
		return R.ok();
	}
	
	/**
	 * 批量审核数据
	 */
	@RequestMapping("/multiCheck")
	@ResponseBody
	public R multiCheck(@RequestBody String jsonParams){
		try{
			newsQueryService.multiCheck(jsonParams);
		}catch(Exception e){
			e.printStackTrace();
			return R.error(e.getMessage());
		}
		
		return R.ok();
	}
	
	/**
	 * 批量确认非重复数据
	 */
	@RequestMapping("/multiVerifyNODouble")
	@ResponseBody
	public R multiVerifyNODouble(@RequestBody String jsonParams){
		
		JSONObject params = JSONObject.parseObject(jsonParams);
		JSONArray arraies = params.getJSONArray("ids");
    	List<String> ids = new ArrayList<>();
    	for(int i = 0; i < arraies.size(); i++) {
    		ids.add(arraies.getString(i));
    	}
    	String entity = (String) params.getString("entity");
		try{
			removeDouble.multiVerifyNODouble(entity,ids);
		}catch(Exception e){
			e.printStackTrace();
			return R.error(e.getMessage());
		}
		return R.ok();
	}
	
}
