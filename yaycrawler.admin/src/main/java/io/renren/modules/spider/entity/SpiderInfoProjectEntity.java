package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * InnoDB free: 3971072 kB
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2018-09-07 15:06:17
 */
public class SpiderInfoProjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long id;
	//
	private Long projectId;
	//
	private String spiderinfoId;

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
	public void setSpiderinfoId(String spiderinfoId) {
		this.spiderinfoId = spiderinfoId;
	}
	/**
	 * 获取：
	 */
	public String getSpiderinfoId() {
		return spiderinfoId;
	}
}
