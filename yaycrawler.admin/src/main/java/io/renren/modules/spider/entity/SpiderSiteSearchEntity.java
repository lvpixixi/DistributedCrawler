package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.Date;

import bscrawler.spider.util.UUIDGenerator;
import io.renren.modules.site.Webpage;


/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-13 16:06:19
 */
public class SpiderSiteSearchEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private String id;
	//
	private String searchName;
	//
	private String keyWord;
	//
	private Date searchTime;
	//
	private String useEngine;

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
	public void setUseEngine(String useEngine) {
		this.useEngine = useEngine;
	}
	/**
	 * 获取：
	 */
	public String getUseEngine() {
		return useEngine;
	}
	

}
