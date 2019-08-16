package io.renren.modules.spider.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.spider.entity.SpiderProjectEntity;
import io.renren.modules.spider.entity.SpiderSiteEntity;
import io.renren.modules.spider.service.SpiderSiteService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;




/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-09 09:08:25
 */
@RestController
@RequestMapping("/spider/spidersite")
public class SpiderSiteController {
	@Autowired
	private SpiderSiteService spiderSiteService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("spider:spidersite:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
        //query.put("order", "desc");
		//query.put("sidx", "create_time");

		List<SpiderSiteEntity> spiderSiteList = spiderSiteService.queryList(query);
		int total = spiderSiteService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(spiderSiteList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 网站列表
	 */
	@RequestMapping("/select")
	public R select(){
		Map<String, Object> map = new HashMap<>();
		List<SpiderSiteEntity> list = spiderSiteService.queryList(map);
		return R.ok().put("list", list);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("spider:spidersite:info")
	public R info(@PathVariable("id") String id){
		SpiderSiteEntity spiderSite = spiderSiteService.queryObject(id);
		List<Long> projectIds = spiderSiteService.getProjectIdsBySiteId(id);
		spiderSite.setProjectIdList(projectIds);
		return R.ok().put("spiderSite", spiderSite);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("spider:spidersite:save")
	public R save(@RequestBody SpiderSiteEntity spiderSite){
		spiderSiteService.save(spiderSite);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("spider:spidersite:update")
	public R update(@RequestBody SpiderSiteEntity spiderSite){
		spiderSiteService.update(spiderSite);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("spider:spidersite:delete")
	public R delete(@RequestBody String[] ids){
		spiderSiteService.deleteBatch(ids);
		return R.ok();
	}
	
	/**
	 * 检查网站
	 */
	@RequestMapping("/checkUrls")
	public R checkUrls(@RequestBody String[] ids){
		spiderSiteService.checkUrls(ids);
		return R.ok();
	}
	
}
