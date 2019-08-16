package com.mss.crawler.custom.weapon;

public class Weapon {

	private String id;
	private String content;
	private String classify;
	private String bigclassify;
	private String url;
	private String title;
	private String classifyCode;
	
	public String getClassifyCode() {
		return classifyCode;
	}
	public void setClassifyCode(String classifyCode) {
		this.classifyCode = classifyCode;
	}
	// 同义词
	private String synonymTitle;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSynonymTitle() {
		return synonymTitle;
	}
	public void setSynonymTitle(String synonymTitle) {
		this.synonymTitle = synonymTitle;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getBigclassify() {
		return bigclassify;
	}
	public void setBigclassify(String bigclassify) {
		this.bigclassify = bigclassify;
	}
	
}
