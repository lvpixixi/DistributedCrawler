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

import io.renren.modules.spider.entity.SpiderSiteSresultEntity;
import io.renren.modules.spider.service.SpiderSiteSresultService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;




/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-13 16:06:20
 */
@RestController
@RequestMapping("/spider/spidersitesresult")
public class SpiderSiteSresultController {
	@Autowired
	private SpiderSiteSresultService spiderSiteSresultService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<SpiderSiteSresultEntity> spiderSiteSresultList = spiderSiteSresultService.getHitSites(query);
		int total = spiderSiteSresultService.getHitSiteTotal(query);
		PageUtils pageUtil = new PageUtils(spiderSiteSresultList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
}
