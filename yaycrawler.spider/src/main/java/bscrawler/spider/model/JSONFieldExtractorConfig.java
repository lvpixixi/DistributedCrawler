package bscrawler.spider.model;

import java.util.List;
import java.util.Map;


/**
 * 对象字段抽取配置
 * @author wangdw
 *
 */
public class JSONFieldExtractorConfig {
	
	/**
	 * 字段名
	 */
	private String fieldName;
	/**
	 * 抽取表达式
	 */
	private String fieldExtractorExp;
	/**
	 * 过滤表达式
	 */
	private Map<String,List<String>> excludeRegionExp;
	
	/**
	 * dom元素
	 */
	private DomModel removeDom;
	/**
	 * 过滤表达式类型
	 *//*
	private String excludeExpType;*/
	
	/**
	 * 从html抽取，还是从json抽取
	 */
	private String sourceType;
	
	/**
	 * 是否不为空
	 */
	private boolean notNull;
	/**
	 * 是否包含多个值
	 */
	private boolean multi;
/*	*//**
	 * 字段抽取类型
	 *//*
	private String fieldExtractorType;*/
	
	/**
	 * 字段内容内部的资源文件抽取配置
	 */
	private List<InterFileExtractorConfig> innerFileConfigs;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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
	/*public String getFieldExtractorType() {
		return fieldExtractorType;
	}
	public void setFieldExtractorType(String fieldExtractorType) {
		this.fieldExtractorType = fieldExtractorType;
	}*/
	
	public String getFieldExtractorExp() {
		return fieldExtractorExp;
	}
	public void setFieldExtractorExp(String fieldExtractorExp) {
		this.fieldExtractorExp = fieldExtractorExp;
	}
	public Map<String, List<String>> getExcludeRegionExp() {
		return excludeRegionExp;
	}
	public void setExcludeRegionExp(Map<String, List<String>> excludeRegionExp) {
		this.excludeRegionExp = excludeRegionExp;
	}
	public List<InterFileExtractorConfig> getInnerFileConfigs() {
		return innerFileConfigs;
	}
	public void setInnerFileConfigs(List<InterFileExtractorConfig> innerFileConfigs) {
		this.innerFileConfigs = innerFileConfigs;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public DomModel getRemoveDom() {
		return removeDom;
	}
	public void setRemoveDom(DomModel removeDom) {
		this.removeDom = removeDom;
	}
	
}
