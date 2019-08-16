package io.renren.modules.spider.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;

import bscrawler.spider.util.UUIDGenerator;
import io.renren.modules.spider.entity.SpiderSiteSearchEntity;
import io.renren.modules.spider.service.ISiteService;
import io.renren.modules.spider.service.SpiderSiteSearchService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;




/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-13 16:06:19
 */
@RestController
@RequestMapping("/spider/spidersitesearch")
public class SpiderSiteSearchController {
	@Autowired
	private SpiderSiteSearchService spiderSiteSearchService;
	
	@Autowired
	private ISiteService siteService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("spider:spidersitesearch:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<SpiderSiteSearchEntity> spiderSiteSearchList = spiderSiteSearchService.queryList(query);
		int total = spiderSiteSearchService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(spiderSiteSearchList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("spider:spidersitesearch:info")
	public R info(@PathVariable("id") String id){
		SpiderSiteSearchEntity spiderSiteSearch = spiderSiteSearchService.queryObject(id);
		
		return R.ok().put("spiderSiteSearch", spiderSiteSearch);
	}
	
	/**
	 *执行网站检索
	 */
	@RequestMapping("/executeSearchById/{id}")
	public R executeSearchById(@PathVariable("id") String id){
		this.siteService.executeSiteSearch(id);
		return R.ok().put("id", id);
	}
	
	/**
	 *执行网站检索
	 */
	@RequestMapping("/executeSearch")
	public R executeSearch(@RequestBody SpiderSiteSearchEntity spiderSiteSearch){
		String id = spiderSiteSearch.getId();
		if(StringUtils.isEmpty(id)) {
			id = UUIDGenerator.generate();
			this.save(spiderSiteSearch);
		}else {
			this.update(spiderSiteSearch);
		}
		this.siteService.executeSiteSearch(id);
		return R.ok().put("id", id);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("spider:spidersitesearch:save")
	public R save(@RequestBody SpiderSiteSearchEntity spiderSiteSearch){
		spiderSiteSearchService.save(spiderSiteSearch);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("spider:spidersitesearch:update")
	public R update(@RequestBody SpiderSiteSearchEntity spiderSiteSearch){
		spiderSiteSearchService.update(spiderSiteSearch);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("spider:spidersitesearch:delete")
	public R delete(@RequestBody String[] ids){
		spiderSiteSearchService.deleteBatch(ids);
		
		return R.ok();
	}
	
}
