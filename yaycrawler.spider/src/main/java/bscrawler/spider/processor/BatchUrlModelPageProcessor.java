package bscrawler.spider.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import bscrawler.spider.SpiderConstants;
import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.util.CrawlerConstant;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * json模型的页面解析处理器 支持json配置文件的页面解析处理器
 * 
 * @author wangdw
 *
 */
public class BatchUrlModelPageProcessor extends BasePageProcessor implements PageProcessor {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	private PageProcessorConf config;

	public BatchUrlModelPageProcessor(Site site, PageProcessorConf config) {
		this.site = site;
		this.config = config;
		this.setProjects(config.getProjects());
		this.iniPageModel(config.getExtractors());
	}

	@Override
	public void process(Page page, List<SpiderListener> listeners) {
		Request req = page.getRequest();
		// 判断请求类型
		String requestType = (String) req.getExtra(TYPE_LABEL);
		// 1) 抽取内容页
		if (requestType == null) {
			// 定义采集图片资源
			List<ResourceFile> resources = Lists.newArrayList();
			// 抽取对象
			List<String> objectNames = new ArrayList<String>();
			for (ObjectModelExtractor extractor : this.extractors) {
				List<ResourceFile> extractorRes = Lists.newArrayList();
				// 对象、新闻的分页链接
				List<String> pageLinks = Lists.newArrayList();
				// 解析页面
				Map<String, Object> process = (Map) extractor.getObject(page, pageLinks, extractorRes);
				resources.addAll(extractorRes);
				if (process == null || (process instanceof List && ((List) process).size() == 0)) {
					continue;
				}
				// 内容页带分页，处理分页
				if (pageLinks != null && pageLinks.size() > 0) {
					List<String> pageList = Lists.newArrayList();
					pageList.addAll(pageLinks);
					Request pageReq = this.processObjPaging(pageList, extractor.getObjName(), (Map) process);
					if (pageReq != null) {
						page.addTargetRequest(pageReq);
					}
					// 内容不分页，设置对象信息
				} else {
					objectNames.add(extractor.getObjName());
					page.putField(extractor.getObjName(), process);
					// 采集成功反馈
					this.onGetOneRecord(this.getSpiderId(), 1, listeners);
				}
			}

			// 设置通用信息
			this.setPageField(page, resources, objectNames);

			// 处理文章分页
		} else if ("article_paging".equals(requestType)) {
			
			List<ResourceFile> resources = Lists.newArrayList();
			List<String> objectNames = new ArrayList<String>();
			
			for (ObjectModelExtractor extractor : this.extractors) {
				List<ResourceFile> extractorRes = Lists.newArrayList();
				Map<String, Object> oldEntity = (Map) req.getExtra(extractor.getObjName());
				// 解析页面
				Map<String, Object> process = (Map) extractor.getObject(page, Lists.newArrayList(),extractorRes);
				
				resources.addAll(extractorRes);
				if (process == null || (process instanceof List && ((List) process).size() == 0)) {
					continue;
				}

				// 合并对象
				process = extractor.mergeEntity(oldEntity, process);
				page.putField(extractor.getObjName(), process);
				objectNames.add(extractor.getObjName());

				// 内容页带分页，处理分页
				if (req.getExtras().containsKey("pageLinks")) {
					List<String> pageList = (List) req.getExtra("pageLinks");
					if (pageList != null && pageList.size() > 0) {
						Request pageReq = this.processObjPaging(pageList, extractor.getObjName(), (Map) process);
						if (pageReq != null) {
							page.addTargetRequest(pageReq);
						}
					}else {
						// 采集成功反馈
						this.onGetOneRecord(this.getSpiderId(), 1, listeners);
					}
				}
			}
			//设置页面对象
			this.setPageField(page, resources, objectNames);
		}

	}

	/**
	 * 设置页面对象，需要输出资源信息
	 * 
	 * @param page
	 * @param resources   待下载资源列表
	 * @param objectNames 待保存的对象列表
	 */
	public void setPageField(Page page, List<ResourceFile> resources, List<String> objectNames) {
		// 设置通用信息
		page.putField("url", page.getRequest().getUrl());
		page.putField("rawText", page.getRawText());
		page.putField("domain", site.getDomain());

		// 设置项目信息
		if (this.getProjects() != null && this.getProjects().size() > 0) {
			page.putField("project", StringUtils.join(this.getProjects(), ","));
		} else {
			page.putField("project", "");
		}

		// 设置下载资源
		page.putField(SpiderConstants.PAGE_RESOURCE_LABEL, resources);

		// 设置下载对象信息
		if (objectNames.size() > 0) {
			page.putField(SpiderConstants.PAGE_OBJECT_LABEL, objectNames);
		}
	}

	private Request processObjPaging(List<String> pageLinks, String objName, Map process) {
		if (pageLinks == null || pageLinks.size() == 0) {
			return null;
		}
		String pageLink = pageLinks.get(0);
		pageLinks.remove(0);
		Request pageReq = new Request(pageLink);
		// 传递分页对象
		pageReq.putExtra(TYPE_LABEL, "article_paging");
		pageReq.putExtra(objName, process);
		pageReq.putExtra("pageLinks", pageLinks);
		return pageReq;
	}
}
