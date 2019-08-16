package io.renren.modules.spider.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.spider.entity.SpiderProjectEntity;
import io.renren.modules.spider.service.SpiderProjectService;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.service.SysConfigService;




/**
 * InnoDB free: 3971072 kB
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2018-09-06 20:24:12
 */
@RestController
@RequestMapping("/spider/spiderproject")
public class SpiderProjectController {
	@Autowired
	private SpiderProjectService spiderProjectService;
	
	@Autowired
	private SysConfigService sysConfigService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("spider:spiderproject:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<SpiderProjectEntity> spiderProjectList = spiderProjectService.queryList(query);
		int total = spiderProjectService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(spiderProjectList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 项目列表
	 */
	@RequestMapping("/select")
	public R select(){
		Map<String, Object> map = new HashMap<>();
		List<SpiderProjectEntity> list = spiderProjectService.queryList(map);
		return R.ok().put("list", list);
	}
	
	/**
	 * model列表
	 */
	@RequestMapping("/selectModels")
	public R selectModels(){
		Map<String, Object> map = new HashMap<>();
		map.put("key", "model_");
		List<SysConfigEntity> list = sysConfigService.queryList(map);
		return R.ok().put("list", list);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	//@RequiresPermissions("spider:spiderproject:info")
	public R info(@PathVariable("id") Long id){
		SpiderProjectEntity spiderProject = spiderProjectService.queryObject(id);
		if(!StringUtils.isEmpty(spiderProject.getModels())){
			JSONArray jarray = JSONArray.parseArray(spiderProject.getModels());
			List<Long> modelIdList = new ArrayList<>();
			for(Object value:jarray){
				modelIdList.add(Long.parseLong(value+""));
			}
			spiderProject.setModelIdList(modelIdList);
		}
		
		return R.ok().put("spiderProject", spiderProject);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	//@RequiresPermissions("spider:spiderproject:save")
	public R save(@RequestBody SpiderProjectEntity spiderProject){
		spiderProject.setCreateTime(new Date());
		spiderProjectService.save(spiderProject);
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	//@RequiresPermissions("spider:spiderproject:update")
	public R update(@RequestBody SpiderProjectEntity spiderProject){
		spiderProjectService.update(spiderProject);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	//@RequiresPermissions("spider:spiderproject:delete")
	public R delete(@RequestBody Long[] ids){
		spiderProjectService.deleteBatch(ids);
		
		return R.ok();
	}
	
}
