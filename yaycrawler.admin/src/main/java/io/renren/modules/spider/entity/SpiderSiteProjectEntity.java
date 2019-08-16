package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-09 15:03:23
 */
public class SpiderSiteProjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long id;
	//
	private Long projectId;
	//
	private String siteId;

	/**
	 * 设置：
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	/**
	 * 获取：
	 */
	public Long getProjectId() {
		return projectId;
	}
	/**
	 * 设置：
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * 获取：
	 */
	public String getSiteId() {
		return siteId;
	}
}
