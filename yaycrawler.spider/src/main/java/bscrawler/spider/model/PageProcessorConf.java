package bscrawler.spider.model;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class PageProcessorConf {
	
	private String name;
	private Set<String> projects;
	private List extractors;
	private PagingModel pagingModel;	
	private Map<String,Object> mapConf;
    /**
     * a过滤垃圾广告
     */
    private Map<String,String> excludeExpressions;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List getExtractors() {
		return extractors;
	}
	public void setExtractors(List extractors) {
		this.extractors = extractors;
	}
	public Map<String, Object> getMapConf() {
		return mapConf;
	}
	public void setMapConf(Map<String, Object> mapConf) {
		this.mapConf = mapConf;
	}
	public Map<String, String> getExcludeExpressions() {
		return excludeExpressions;
	}
	public void setExcludeExpressions(Map<String, String> excludeExpressions) {
		this.excludeExpressions = excludeExpressions;
	}
	public PagingModel getPagingModel() {
		return pagingModel;
	}
	public void setPagingModel(PagingModel pagingModel) {
		this.pagingModel = pagingModel;
	}
	public Set<String> getProjects() {
		return projects;
	}
	public void setProjects(Set<String> projects) {
		this.projects = projects;
	}

	
}
