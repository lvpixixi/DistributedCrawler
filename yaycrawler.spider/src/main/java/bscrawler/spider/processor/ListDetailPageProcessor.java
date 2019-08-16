package bscrawler.spider.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import bscrawler.spider.SpiderConstants;
import bscrawler.spider.extractor.ObjectModelExtractor;
import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.model.PageProcessorConf;
import bscrawler.spider.resolver.SelectorExpressionResolver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 列表详情页采集模式
 * 
 * @author wangdw
 *
 */
public class ListDetailPageProcessor extends BasePageProcessor implements PageProcessor {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	public final static String LIST_OBJECT_LABEL = "listObject";
	private PageProcessorConf config;
	private List<String> pagingRules;
	private String listRule;

	public ListDetailPageProcessor(Site site, PageProcessorConf config) {
		this.site = site;
		this.config = config;
		this.setProjects(config.getProjects());
		this.pagingRules = (List<String>) config.getMapConf().get("pagingRules");
		this.listRule = (String) config.getMapConf().get("listRule");
		this.iniPageModel(config.getExtractors());
	}

	public void iniPageModel(List extractors) {
		for (int i = 0; i < extractors.size(); i++) {
			JSONObject json = (JSONObject) extractors.get(i);
			ObjectModelExtractor extractor = JSONObject.toJavaObject(json, ObjectModelExtractor.class);
			extractor.setDomain(this.site.getDomain());
			this.extractors.add(extractor);
		}
	}

	@Override
	public void process(Page page, List<SpiderListener> listeners) {
		// 获取页面抽取类型
		Request req = page.getRequest();
		Object type = req.getExtra(TYPE_LABEL);
		Html html = page.getHtml();

		// 第一页列表页
		if (type == null) {
			logger.info("starturl=" + page.getUrl());
			// 处理翻页
			this.processPageTurning(page);
			// 处理列表详情页
			parseListSeed(page);

			// 第二页至最后一页列表页
		} else if ("article_list".equals(type)) {
			// 处理列表详情页
			parseListSeed(page);
			// 处理文章页
		} else if ("article".equals(type)) {
			// 设置运行环境变量
			/*
			 * page.putField("url", page.getRequest().getUrl());
			 * page.putField("rawText", page.getRawText());
			 * page.putField("domain", site.getDomain());
			 * page.putField(LIST_OBJECT_LABEL,
			 * req.getExtra(LIST_OBJECT_LABEL));
			 */
			this.iniPageField(page);
			List<String> objectNames = Lists.newArrayList();

			List<ResourceFile> resources = Lists.newArrayList();
			// 循环解析
			for (ObjectModelExtractor extractor : this.extractors) {
				List<ResourceFile> extractorRes = Lists.newArrayList();
				List<String> pageLinks = Lists.newArrayList();
				// 解析页面
				Object process = extractor.getObject(page, pageLinks, resources);
				if (process == null || (process instanceof List && ((List) process).size() == 0)) {
					continue;
				}
				resources.addAll(extractorRes);
				objectNames.add(extractor.getObjName());
				page.putField(extractor.getObjName(), process);
				// 内容页带分页，处理分页
				if (pageLinks.size() > 0) {
					List<String> pageList = Lists.newArrayList();
					pageList.addAll(pageLinks);
					Request pageReq = this.processObjPaging(pageList, extractor.getObjName(), (Map) process);
					if (pageReq != null) {
						page.addTargetRequest(pageReq);
					}
				}
			}
			if (objectNames.size() > 0) {
				page.putField(SpiderConstants.PAGE_OBJECT_LABEL, objectNames);
			}
			page.putField(SpiderConstants.PAGE_RESOURCE_LABEL, resources);
			// 采集成功反馈
			this.onGetOneRecord(this.getSpiderId(), 1, listeners);

			// 处理文章分页
		} else if ("article_paging".equals(type)) {
			// 设置运行环境变量
			this.iniPageField(page);
			List<ResourceFile> resources = Lists.newArrayList();
			List<String> objectNames = new ArrayList<String>();
			for (ObjectModelExtractor extractor : this.extractors) {
				List<ResourceFile> extractorRes = Lists.newArrayList();
				Map<String, Object> oldEntity = (Map) req.getExtra(extractor.getObjName());

				// 解析页面
				Map<String, Object> process = (Map) extractor.getObject(page, Lists.newArrayList(), extractorRes);
				resources.addAll(extractorRes);
				if (process == null || (process instanceof List && ((List) process).size() == 0)) {
					continue;
				}

				// 合并对象
				process = extractor.mergeEntity(oldEntity, process);
				objectNames.add(extractor.getObjName());
				page.putField(extractor.getObjName(), process);

				// 内容页带分页，处理分页
				if (req.getExtras().containsKey("pageLinks")) {
					List<String> pageList = (List) req.getExtra("pageLinks");
					if (pageList != null && pageList.size() > 0) {
						Request pageReq = this.processObjPaging(pageList, extractor.getObjName(), (Map) process);
						if (pageReq != null) {
							page.addTargetRequest(pageReq);
						}
					}
				}
			}

			page.putField(SpiderConstants.PAGE_RESOURCE_LABEL, resources);

			if (objectNames.size() > 0) {
				page.putField(SpiderConstants.PAGE_OBJECT_LABEL, objectNames);
			}

			// 采集成功反馈
			this.onGetOneRecord(this.getSpiderId(), 1, listeners);

		}

		if (page.getResultItems().getAll().size() == 0) {
			page.getResultItems().setSkip(true);
		}

	}

	private Request processObjPaging(List<String> pageLinks, String objName, Map process) {
		if (pageLinks == null || pageLinks.size() == 0) {
			return null;
		}
		String pageLink = pageLinks.get(0);
		pageLinks.remove(0);
		Request pageReq = new Request(pageLink).putExtra(TYPE_LABEL, "article_paging");
		// 传递分页对象
		Map<String, Object> extras = Maps.newHashMap();
		extras.put(objName, process);
		extras.put("pageLinks", pageLinks);
		pageReq.setExtras(extras);
		return pageReq;
	}

	/**
	 * 解析列表頁的url 通过 XPath/Regex/JSONPath 进行定位, 通过 Regex 进行定位
	 * 
	 * @param page
	 */
	private void parseListSeed(Page page) {
		Html html = page.getHtml();

		Selectable rootEle = SelectorExpressionResolver.getPageSelectable(page, page.getRequest(), this.listRule);
		List<Object> links = SelectorExpressionResolver.resolve(page.getRequest(), rootEle, this.listRule);

		for (Object node : links) {
			String detailurl = this.urlParse(site.getDomain(), node + "", page.getRequest().getUrl());
			logger.info("detailurl=" + detailurl);
			if(!StringUtils.isEmpty(detailurl)) {
				page.addTargetRequest(new Request(detailurl).putExtra(TYPE_LABEL, "article"));
			}
		}

	}

	/**
	 * a解析列表页抽取的链接信息
	 * 
	 * @param domain
	 *            域名
	 * @param url
	 *            解析出URL
	 * @param startURL
	 *            请求地址Url
	 * @return
	 */
	private String urlParse(String domain, String url, String startURL) {
		if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
			return url;
		}
		if (url.toLowerCase().startsWith("//")) {
			return "http:" + url;
		}
		if (!url.startsWith("/")) {
			String prefix = "http://" + domain + "/";
			startURL = startURL.subSequence(startURL.indexOf(prefix) + prefix.length(), startURL.length()).toString();
			if (startURL == null || startURL.equals("")) {
				return prefix + url;
			}
			File file = new File(startURL);
			File parentUrl = file.getParentFile();
			if (parentUrl != null) {
				if (!url.contains("..")) {
					return prefix + parentUrl.toString().replace("\\", "/") + "/" + url;
				}
				String[] temp = url.split("/");
				int count = 0;
				for (int i = 0; i < temp.length; i++) {
					if (temp[i].equals("..")) {
						count++;
					}
				}
				while (count > 0) {
					parentUrl = parentUrl.getParentFile();
					count--;
				}
				url = url.replace("../", "");
				if (parentUrl == null) {
					return prefix + url;
				}
				return prefix + parentUrl + "/" + url;
			} else {
				return "http://" + domain + "/" + url;
			}

		}

		return "http://" + domain + url;

	}

	/**
	 * 处理翻页链接-处理下一页
	 * 
	 * @param page
	 */
	private void processPageTurning(Page page) {
		for (String pageRule : this.pagingRules) {
			Selectable rootEle = SelectorExpressionResolver.getPageSelectable(page, page.getRequest(), pageRule);
			List<String> links = SelectorExpressionResolver.resolve(page.getRequest(), rootEle, pageRule);
			for (String link : links) {
				if (!StringUtils.isEmpty(link)) {
					logger.info("pagingurl=" + link);
					page.addTargetRequest(new Request(link).putExtra(TYPE_LABEL, "article_list"));
				}
			}
		}

	}

}
