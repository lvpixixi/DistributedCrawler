package com.mss.crawler.custom.weapon;

import java.util.Map;

public class WeaponBean {
	
	private String id;
	private String name;
	private String country;
	private String cover;
	private String summary;
	private String content;
	private String infos;
	private String classify;
	private Map<String,String> keys;
	private String contentHtml;
	private String datainfoHtml;
	private String classifyCode;
	// 同义词
	private String synonymTitle;
	
	public String getClassifyCode() {
		return classifyCode;
	}
	public void setClassifyCode(String classifyCode) {
		this.classifyCode = classifyCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSynonymTitle() {
			return synonymTitle;
		}
		public void setSynonymTitle(String synonymTitle) {
			this.synonymTitle = synonymTitle;
		}
	public String getDatainfoHtml() {
		return datainfoHtml;
	}
	public void setDatainfoHtml(String datainfoHtml) {
		this.datainfoHtml = datainfoHtml;
	}
	private String url;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getContentHtml() {
		return contentHtml;
	}
	public void setContentHtml(String contentHtml) {
		this.contentHtml = contentHtml;
	}
	public String getBigclassify() {
		return bigclassify;
	}
	public void setBigclassify(String bigclassify) {
		this.bigclassify = bigclassify;
	}
	private String bigclassify;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getInfos() {
		return infos;
	}
	public void setInfos(String infos) {
		this.infos = infos;
	}
	public Map<String, String> getKeys() {
		return keys;
	}
	public void setKeys(Map<String, String> keys) {
		this.keys = keys;
	}
	
}
