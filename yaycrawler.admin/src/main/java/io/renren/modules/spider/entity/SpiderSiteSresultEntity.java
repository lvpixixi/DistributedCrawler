package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import bscrawler.spider.util.HtmlFormatter;
import bscrawler.spider.util.UUIDGenerator;
import io.renren.modules.site.Webpage;


/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-13 16:06:20
 */
public class SpiderSiteSresultEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private String id;
	//
	private String url;
	//
	private String domain;
	//
	private String searchName;
	//
	private String title;
	//
	private String summary;
	//
	private String useEngine;
	//
	private String keyWord;
	//
	private Date searchTime;
	//
	private String sid;
	
	private Integer hits;

	/**
	 * 设置：
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置：
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 获取：
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 设置：
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}
	/**
	 * 获取：
	 */
	public String getDomain() {
		return domain;
	}
	/**
	 * 设置：
	 */
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	/**
	 * 获取：
	 */
	public String getSearchName() {
		return searchName;
	}
	/**
	 * 设置：
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取：
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置：
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * 获取：
	 */
	public String getSummary() {
		return summary;
	}
	/**
	 * 设置：
	 */
	public void setUseEngine(String useEngine) {
		this.useEngine = useEngine;
	}
	/**
	 * 获取：
	 */
	public String getUseEngine() {
		return useEngine;
	}
	/**
	 * 设置：
	 */
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	/**
	 * 获取：
	 */
	public String getKeyWord() {
		return keyWord;
	}
	/**
	 * 设置：
	 */
	public void setSearchTime(Date searchTime) {
		this.searchTime = searchTime;
	}
	/**
	 * 获取：
	 */
	public Date getSearchTime() {
		return searchTime;
	}
	/**
	 * 设置：
	 */
	public void setSid(String sid) {
		this.sid = sid;
	}
	/**
	 * 获取：
	 */
	public String getSid() {
		return sid;
	}
	
	public Integer getHits() {
		return hits;
	}
	public void setHits(Integer hits) {
		this.hits = hits;
	}
	public static SpiderSiteSresultEntity getFromWebPage(Webpage wp) {
		SpiderSiteSresultEntity entity = new SpiderSiteSresultEntity();
		entity.setId(UUIDGenerator.generate());
		entity.setUrl(wp.getUrl());
		entity.setDomain(HtmlFormatter.getDomain(wp.getUrl()));
		entity.setSummary(wp.getSummary());
		entity.setTitle(wp.getTitle());
		entity.setSearchTime(Calendar.getInstance().getTime());
		entity.setKeyWord(wp.getSearchWord());
		return entity;
	}
}
