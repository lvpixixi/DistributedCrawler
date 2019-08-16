package io.renren.modules.spider.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import io.renren.modules.site.JSoupBaiduSearcher;
import io.renren.modules.site.SearchResult;
import io.renren.modules.site.Searcher;
import io.renren.modules.site.Webpage;
import io.renren.modules.spider.entity.SpiderSiteSearchEntity;
import io.renren.modules.spider.entity.SpiderSiteSresultEntity;
import io.renren.modules.spider.service.ISiteService;
import io.renren.modules.spider.service.SpiderSiteSearchService;
import io.renren.modules.spider.service.SpiderSiteSresultService;


@Service("SiteService")
public class SiteService implements ISiteService {

	@Autowired
	private SpiderSiteSearchService spiderSiteSearchService;
	
	@Autowired
	private SpiderSiteSresultService spiderSiteSresultService;
	
	private Map<String,Searcher> searcherMap = Maps.newHashMap();
	
	@Override
	public void executeSiteSearch(String searchId) {
		// 1-)获取search信息
		
		SpiderSiteSearchEntity entity = spiderSiteSearchService.queryObject(searchId);
		
		// 2-)获取search信息
		
		Searcher searcher = new JSoupBaiduSearcher();//searcherMap.get(entity.getUseEngine());
	
		List<Webpage> webPages = Lists.newArrayList();
		String[] keywords = StringUtils.split(entity.getKeyWord(),",");
		for(String keyword:keywords) {
			SearchResult sr = searcher.search(keyword, 1);
			webPages.addAll(sr.getWebpages());
		}
		
		//3-)批量保存search-result
		
		//清空执行结果
		this.spiderSiteSresultService.delete(searchId);
		List<SpiderSiteSresultEntity> searchResults = Lists.newArrayList();
		for(Webpage webPage:webPages) {
			//searchResults.add(SpiderSiteSresultEntity.getFromWebPage(webPage));
			SpiderSiteSresultEntity ssrEntity = SpiderSiteSresultEntity.getFromWebPage(webPage);
			ssrEntity.setSid(searchId);
			this.spiderSiteSresultService.save(ssrEntity);
		}
	}

}
