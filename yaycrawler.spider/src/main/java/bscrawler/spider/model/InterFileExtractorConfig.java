package bscrawler.spider.model;

import java.util.List;

/**
 * 内容内嵌文件抽取配置
 * @author wangdw
 *
 */
public class InterFileExtractorConfig {
	/**
	 * 不允许为空
	 */
	private boolean notNull = false;
	/**
	 * 是否是多值，多值对应list抽取，单值对应对象抽取
	 */
	private boolean multi = true;
	/**
	 * 资源名称
	 */
	private String resourceName;
	/**
	 * 区域表达式,资源的内容的区域
	 */
	private String resourceRegionExp;
	/**
	 * 资源链接匹配规则
	 */
	private List<String> resourceUrlPatterns;
	/**
	 * 排除资源链接匹配规则
	 */
	private List<String> excludeUrlPatterns;	
	
	
	public List<String> getResourceUrlPatterns() {
		return resourceUrlPatterns;
	}
	public void setResourceUrlPatterns(List<String> resourceUrlPatterns) {
		this.resourceUrlPatterns = resourceUrlPatterns;
	}
	public List<String> getExcludeUrlPatterns() {
		return excludeUrlPatterns;
	}
	public void setExcludeUrlPatterns(List<String> excludeUrlPatterns) {
		this.excludeUrlPatterns = excludeUrlPatterns;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceRegionExp() {
		return resourceRegionExp;
	}
	public void setResourceRegionExp(String resourceRegionExp) {
		this.resourceRegionExp = resourceRegionExp;
	}
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
	public boolean isMulti() {
		return multi;
	}
	public void setMulti(boolean multi) {
		this.multi = multi;
	}
}
