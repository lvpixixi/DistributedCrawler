package bscrawler.spider.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import bscrawler.spider.SpiderConstants;
import bscrawler.spider.resolver.SelectorExpressionResolver;
import bscrawler.spider.util.DateUtil;
import bscrawler.spider.util.HtmlFormatter;
import bscrawler.spider.util.UUIDGenerator;
import bscrawler.spider.util.UrlParseUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.dao.domain.FieldParseRule;
import yaycrawler.dao.domain.UrlParseRule;
import yaycrawler.dao.domain.UrlRuleParam;

/**
 * 模型文件抽取器
 * 
 * @author wangdw
 *
 */
public class ObjectModelExtractor implements IModelExtractor {

	private Logger logger = LoggerFactory.getLogger(ObjectModelExtractor.class);

	private static final String DEFAULT_PAGE_SELECTOR = "page";

	/**
	 * 主键列设置
	 */
	private List<String> pks;
	/**
	 * 对象名称
	 */
	private String objName;

	/**
	 * 域名
	 */
	private String domain;

	/**
	 * 图片发布服务器根路径
	 */
	private String baseUrl = "http://localhost";

	/**
	 * 区域选择表达式
	 */
	private String selectExpression;

	/**
	 * 过滤垃圾广告
	 */
	private Map<String, String> excludeExpressions;

	/**
	 * 是否抽取多个
	 */
	private boolean isMulti = false;

	/**
	 * 抽取类型
	 */
	private String dataType;

	/**
	 * 获取对象分页数量规则
	 */
	private String objPageCountRule;

	/**
	 * 构造对象分页请求规则
	 */
	private String pageUrlRule;

	/**
	 * 匹配抽取Url规则
	 */
	private String targetUrl;

	/**
	 * 列抽取规则
	 */
	private Set<FieldParseRule> fieldParseRules;

	/**
	 * url抽取规则
	 */
	private Set<UrlParseRule> urlParseRules;

	/**
	 * 资源url抽取规则
	 */
	private Set<UrlParseRule> resUrlParseRules;

	public Set<UrlParseRule> getResUrlParseRules() {
		return resUrlParseRules;
	}

	public void setResUrlParseRules(Set<UrlParseRule> resUrlParseRules) {
		this.resUrlParseRules = resUrlParseRules;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getDomain() {
		return domain;
	}

	public String getPageUrlRule() {
		return pageUrlRule;
	}

	public void setPageUrlRule(String pageUrlRule) {
		this.pageUrlRule = pageUrlRule;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getSelectExpression() {
		return selectExpression;
	}

	public void setSelectExpression(String selectExpression) {
		this.selectExpression = selectExpression;
	}

	public boolean isMulti() {
		return isMulti;
	}

	public void setMulti(boolean isMulti) {
		this.isMulti = isMulti;
	}

	public Set<FieldParseRule> getFieldParseRules() {
		return fieldParseRules;
	}

	public void setFieldParseRules(Set<FieldParseRule> fieldParseRules) {
		this.fieldParseRules = fieldParseRules;
	}

	public Set<UrlParseRule> getUrlParseRules() {
		return urlParseRules;
	}

	public void setUrlParseRules(Set<UrlParseRule> urlParseRules) {
		this.urlParseRules = urlParseRules;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Map<String, String> getExcludeExpressions() {
		return excludeExpressions;
	}

	public void setExcludeExpressions(Map<String, String> excludeExpressions) {
		this.excludeExpressions = excludeExpressions;
	}

	@Override
	public Object getObject(Page page, List<String> pageLinks, List<ResourceFile> resources) {

		Request request = page.getRequest();
		String selectExpression = getSelectExpression();

		Selectable context = getPageRegionContext(page, request, selectExpression);
		if (context == null)
			return null;

		/*
		 * Set<UrlParseRule> urlParseRules = getUrlParseRules(); if
		 * (urlParseRules != null && urlParseRules.size() > 0) {
		 * resources.addAll(parseUrlRules(context, request, urlParseRules)); }
		 */
		if (!StringUtils.isEmpty(this.getPageUrlRule())) {
			pageLinks.addAll(parsePageLink(context, request, this.getPageUrlRule()));
		}
		Set<FieldParseRule> fieldParseRules = getFieldParseRules();
		if (fieldParseRules != null && fieldParseRules.size() > 0) {
			return parseFieldRules(context, request, fieldParseRules, getDataType(), this.isMulti(), resources);
		}

		/*
		 * //处理下载资源 List<ResourceFile> resources = new ArrayList<>(); //处理资源超链接
		 * Set<UrlParseRule> resUrlParseRules = this.getResUrlParseRules(); if
		 * (resUrlParseRules != null && resUrlParseRules.size() > 0) {
		 * resources.addAll(parseResFiles(context, request, urlParseRules)); }
		 * page.putField("resources", resources);
		 */
		return null;
	}

	/**
	 * 合并Content
	 * 
	 * @return
	 */
	public Map<String, Object> mergeEntity(Map<String, Object> oldEntity, Map<String, Object> newEntity) {
		List<String> fields = this.getHTMLFields();
		for (String field : fields) {
			Object oldField = oldEntity.get(field);
			Object newField = newEntity.get(field);
			oldEntity.put(field, this.mergeField(oldField, newField));
		}
		return oldEntity;
	}

	public Object mergeField(Object oldField, Object newField) {
		Object value = null;
		if (oldField instanceof String) {
			value = oldField + "" + newField;
		}
		if (oldField instanceof List) {
			value = ((List) oldField).addAll((List) newField);
		}

		return value;

	}

	/**
	 * 获取一个region的上下文
	 *
	 * @param page
	 * @param request
	 * @param regionSelectExpression
	 * @return
	 */
	public Selectable getPageRegionContext(Page page, Request request, String regionSelectExpression) {
		Selectable context;
		if (StringUtils.isBlank(regionSelectExpression)
				|| DEFAULT_PAGE_SELECTOR.equalsIgnoreCase(regionSelectExpression)) {
			context = page.getHtml();
		} else if (regionSelectExpression.toLowerCase().contains("getjson()")
				|| regionSelectExpression.toLowerCase().contains("jsonpath")) {
			context = SelectorExpressionResolver.resolve(request, page.getJson(), regionSelectExpression);
		} else {
			context = SelectorExpressionResolver.resolve(request, page.getHtml(), regionSelectExpression);
		}
		return context;
	}

	/**
	 * 解析一个字段抽取规则
	 *
	 * @param context
	 * @param request
	 * @param fieldParseRuleList
	 * @return
	 */
	public Object parseFieldRules(Selectable context, Request request, Collection<FieldParseRule> fieldParseRuleList,
			String dataType, boolean isMulti, List<ResourceFile> resources) {
		List<Selectable> nodes = getNodes(context);
		List<Map<String, Object>> results = new ArrayList<>();
		String content = null;
		String key = null;
		for (Selectable node : nodes) {
			HashedMap childMap = new HashedMap();
			for (FieldParseRule fieldParseRule : fieldParseRuleList) {
				Object datas = childMap.get(fieldParseRule.getFieldName());
				// 同一个列名可以配置多个。
				datas = SelectorExpressionResolver.resolve(request, node, fieldParseRule.getRule());

				Object value = this.processField(request, fieldParseRule.getValueType(), datas, dataType, resources);
				// 如果是新闻类型需要替换图片路径
				if (SpiderConstants.FIELD_DATATYPE_HTML.equalsIgnoreCase(fieldParseRule.getValueType())) {
					key = fieldParseRule.getFieldName();
					content = value + "";
				}
				// Map初始化完成
				childMap.put(fieldParseRule.getFieldName(), value);
			}

			childMap.put("_id", getPKID(childMap));

			if (!StringUtils.isEmpty(key) && (resources != null && resources.size() > 0)) {
				childMap.put(key, processResLink(content, resources));
			}

			// 根据数据类型，处理列信息
			// String value = this.processField(dataType, datas,
			// excludeExpressions, dataType, resources);
			results.add(childMap);
		}

		if (isMulti) {
			return results;
		} else {
			return results != null && results.size() > 0 ? results.get(0) : null;
		}
	}

	public Object getPKID(Map map) {
		List<String> pks = this.getPks();
		if (pks != null && pks.size() > 0) {
			List<String> values = new ArrayList<String>();
			for (String pkKey : pks) {
				values.add(map.get(pkKey) + "");
			}

			String valueStr = StringUtils.join(values.toArray());

			return DigestUtils.md5Hex(valueStr);
		} else {
			String uuid = UUIDGenerator.generate();

			return uuid;
		}
	}

	public List<String> parsePageLink(Selectable context, Request request, String pageRule) {
		List<String> pageLinks = Lists.newArrayList();
		Object u = SelectorExpressionResolver.resolve(request, context, pageRule);
		if (u instanceof Collection) {
			Collection<String> urlList = (Collection<String>) u;
			if (urlList.size() > 0)
				for (String url : urlList)
					pageLinks.add(url);
		} else {
			pageLinks.add(u + "");
		}

		// 去重
		removeDuplicateWithOrder(pageLinks);
		return pageLinks;
	}

	public void removeDuplicateWithOrder(List list) {
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		list.clear();
		list.addAll(newList);
	}

	/**
	 * 解析一个Url抽取规则
	 *
	 * @param context
	 * @param request
	 * @param urlParseRuleList
	 * @return
	 */
	public List<CrawlerRequest> parseUrlRules(Selectable context, Request request,
			Collection<UrlParseRule> urlParseRuleList) {
		List<CrawlerRequest> childRequestList = new LinkedList<>();
		List<Selectable> nodes = getNodes(context);

		for (Selectable node : nodes) {
			if (node == null)
				continue;

			for (UrlParseRule urlParseRule : urlParseRuleList) {
				// 解析url
				Object u = SelectorExpressionResolver.resolve(request, node, urlParseRule.getRule());
				// 解析Url的参数
				Map<String, Object> urlParamMap = new HashMap<>();
				if (urlParseRule.getUrlRuleParams() != null)
					for (UrlRuleParam ruleParam : urlParseRule.getUrlRuleParams()) {
						urlParamMap.put(ruleParam.getParamName(),
								SelectorExpressionResolver.resolve(request, node, ruleParam.getExpression()));
					}
				// 组装成完整的URL
				if (u instanceof Collection) {
					Collection<String> urlList = (Collection<String>) u;
					if (urlList.size() > 0)
						for (String url : urlList)
							childRequestList.add(new CrawlerRequest(url, urlParseRule.getMethod(), urlParamMap));
				} else
					childRequestList.add(new CrawlerRequest((String) u, urlParseRule.getMethod(), urlParamMap));
			}
		}
		return childRequestList;
	}

	/* *//**
			 * 解析一个Url抽取规则
			 *
			 * @param context
			 * @param request
			 * @param urlParseRuleList
			 * @return
			 *//*
			 * public List<ResourceFile> parseResFiles(Selectable context,
			 * Request request, Collection<UrlParseRule> urlParseRuleList) {
			 * List<ResourceFile> resources = new ArrayList<>();
			 * List<Selectable> nodes = getNodes(context);
			 * 
			 * for (Selectable node : nodes) { if (node == null) continue;
			 * 
			 * for (UrlParseRule urlParseRule : urlParseRuleList) { //解析url
			 * Object u = SelectorExpressionResolver.resolve(request, node,
			 * urlParseRule.getRule()); //解析Url的参数 Map<String, Object>
			 * urlParamMap = new HashMap<>(); if
			 * (urlParseRule.getUrlRuleParams() != null) for (UrlRuleParam
			 * ruleParam : urlParseRule.getUrlRuleParams()) {
			 * urlParamMap.put(ruleParam.getParamName(),
			 * SelectorExpressionResolver.resolve(request, node,
			 * ruleParam.getExpression())); } //组装成完整的URL if (u instanceof
			 * Collection) { Collection<String> urlList = (Collection<String>)
			 * u; if (urlList.size() > 0) for (String url : urlList)
			 * resources.add(new ResourceFile(url,this.getDomain())); } else
			 * resources.add(new ResourceFile(u.toString(),this.getDomain())); }
			 * } return resources; }
			 */

	public List<ResourceFile> parseResFiles(String reqUrl, Object images) {

		List<ResourceFile> resources = new ArrayList<>();
		if (images instanceof List) {
			for (Object imageUrl : (List) images) {
				if("" == imageUrl) {
					continue;
				}
				imageUrl = formatDownloadUrl(imageUrl.toString());
				String downUrl = UrlParseUtils.getFullUrl(reqUrl, imageUrl + "");
				resources.add(new ResourceFile(imageUrl + "", downUrl, this.getDomain(), baseUrl));
			}
		} else {
			String downUrl = UrlParseUtils.getFullUrl(reqUrl, images + "");
			resources.add(new ResourceFile(images + "", downUrl, this.getDomain(), baseUrl));
		}
		return resources;
	}

	// 格式化文件的下载链接, 部分的链接存在多个指向同一张图片的链接, 仅保留第一个链接
	private Object formatDownloadUrl(String imageUrl) {
		if(imageUrl.split(", ").length > 1) {
			imageUrl = imageUrl.split(", ")[0];
			String imgName = imageUrl.substring(imageUrl.lastIndexOf("/"), imageUrl.length());
			System.out.println(imgName);
			if(imgName.contains(".") && imgName.contains(" ") && (imgName.indexOf(".") < imgName.indexOf(" "))) {
				String tempName = imgName.substring(0, imgName.indexOf(" "));
				System.out.println(tempName);
				imageUrl = imageUrl.substring(0, imageUrl.indexOf(imgName)) + tempName;
			}
		}
		return imageUrl;
	}

	/**
	 * 字段字符串处理
	 * 
	 * @param page
	 * @param html
	 * @param fieldExtractor
	 * @return
	 */
	private Object processField(Request request, String valueType, Object content, String domain,
			List<ResourceFile> resources) {
		Object value = null;
		switch (valueType) {
		// 新闻类型处理
		case SpiderConstants.FIELD_DATATYPE_HTML:
			value = processNewContent((String) content);
			break;
		// 文件类型处理
		case SpiderConstants.FIELD_DATATYPE_FILE:
			value = content;
			List<ResourceFile> _resources = parseResFiles(request.getUrl(), value);
			resources.addAll(_resources);
			break;
		// 日期类型处理
		case SpiderConstants.FIELD_DATATYPE_DATE:
			value = DateUtil.convertPubDate(content + "");
			break;
		case SpiderConstants.FIELD_DATATYPE_STRING:
			value = content;
			break;
		default:
			value = content;
		}
		return value;
	}

	protected List<Selectable> getNodes(Selectable context) {
		List<Selectable> nodes = new LinkedList<>();
		if (context instanceof Json) {
			nodes.add(context);
		} else
			nodes.addAll(context.nodes());
		return nodes;
	}

	/**
	 * 处理新闻内容字段
	 * 
	 * @param page
	 * @param text
	 * @param fieldExtractor
	 * @param context
	 * @return
	 */
	private String processNewContent(String text) {
		String value = text;

		// 通过dom方式去除广告等干扰
		if (this.excludeExpressions != null && this.excludeExpressions.size() > 0) {
			for (Map.Entry<String, String> entry : this.excludeExpressions.entrySet()) {
				value = SelectorExpressionResolver.removeByExpression(value, entry.getValue());
			}
		}
		value = HtmlFormatter.html2textWithOutCssandStyle(value);
		return value;
	}

	public String processResLink(String content, List<ResourceFile> resources) {
		String resultHtml = content;
		for (ResourceFile rf : resources) {
			resultHtml = StringUtils.replace(resultHtml, rf.getOriginalUrl(), rf.getPublicUrl());
		}

		return resultHtml;
	}

	/**
	 * 获取资源类型字段
	 * 
	 * @return
	 */
	public List<String> getResourceFields() {
		return this.getFieldsByValueType(SpiderConstants.FIELD_DATATYPE_FILE);
	}

	/**
	 * 获取html类型字段
	 * 
	 * @return
	 */
	public List<String> getHTMLFields() {
		return this.getFieldsByValueType(SpiderConstants.FIELD_DATATYPE_HTML);
	}

	public List<String> getPks() {
		return pks;
	}

	public void setPks(List<String> pks) {
		this.pks = pks;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * 获取指定值类型字段
	 * 
	 * @param valueType
	 * @return
	 */
	private List<String> getFieldsByValueType(String valueType) {
		List<String> fieldNames = new ArrayList<>();
		for (FieldParseRule field : this.getFieldParseRules()) {
			if (field.getValueType().equalsIgnoreCase(valueType)) {
				fieldNames.add(field.getFieldName());
			}
		}
		return fieldNames;
	}
	
}
