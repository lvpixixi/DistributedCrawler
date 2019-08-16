package bscrawler.spider.model;

import java.util.List;


public class JSONExtractorConfig {
	
	 private String domain;
	 
	 private String objName;
	 
	 private String[] fileNameRules;
	    
	 private String objXpath="//html";
	    
	 private String sourceType;
	    
	 private boolean isMulti = false;
	 
	 private String[] targetUrlPatterns;

	 private String targetUrlXPath;

	 private String[] helpUrlPatterns;

	 private String helpUrlXPath;
	 
	 private PagingModel pagingModel;	 
	 
	 private List<JSONFieldExtractorConfig> fieldList;
	 
	 


	public String[] getFileNameRules() {
		return fileNameRules;
	}

	public void setFileNameRules(String[] fileNameRules) {
		this.fileNameRules = fileNameRules;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getObjXpath() {
		return objXpath;
	}

	public void setObjXpath(String objXpath) {
		this.objXpath = objXpath;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public boolean isMulti() {
		return isMulti;
	}

	public void setMulti(boolean isMulti) {
		this.isMulti = isMulti;
	}

	public List<JSONFieldExtractorConfig> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<JSONFieldExtractorConfig> fieldList) {
		this.fieldList = fieldList;
	}

	public String[] getTargetUrlPatterns() {
		return targetUrlPatterns;
	}

	public void setTargetUrlPatterns(String[] targetUrlPatterns) {
		this.targetUrlPatterns = targetUrlPatterns;
	}

	public String getTargetUrlXPath() {
		return targetUrlXPath;
	}

	public void setTargetUrlXPath(String targetUrlXPath) {
		this.targetUrlXPath = targetUrlXPath;
	}

	public String[] getHelpUrlPatterns() {
		return helpUrlPatterns;
	}

	public void setHelpUrlPatterns(String[] helpUrlPatterns) {
		this.helpUrlPatterns = helpUrlPatterns;
	}

	public String getHelpUrlXPath() {
		return helpUrlXPath;
	}

	public void setHelpUrlXPath(String helpUrlXPath) {
		this.helpUrlXPath = helpUrlXPath;
	}

	public PagingModel getPagingModel() {
		return pagingModel;
	}

	public void setPagingModel(PagingModel pagingModel) {
		this.pagingModel = pagingModel;
	}

	
	
	
	
}
