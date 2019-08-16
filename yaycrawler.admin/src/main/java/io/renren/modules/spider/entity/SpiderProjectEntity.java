package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 采集项目
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-09 10:17:51
 */
public class SpiderProjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long id;
	//
	private String name;
	//
	private String code;
	//
	private String contactor;
	//
	private Date createTime;
	//
	private String email;
	//
	private String mobile;
	//
	private String models;
	
	private List<Long> modelIdList;

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
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取：
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置：
	 */
	public void setContactor(String contactor) {
		this.contactor = contactor;
	}
	/**
	 * 获取：
	 */
	public String getContactor() {
		return contactor;
	}
	/**
	 * 设置：
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置：
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * 获取：
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * 设置：
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * 获取：
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * 设置：
	 */
	public void setModels(String models) {
		this.models = models;
	}
	/**
	 * 获取：
	 */
	public String getModels() {
		return models;
	}
	public List<Long> getModelIdList() {
		return modelIdList;
	}
	public void setModelIdList(List<Long> modelIdList) {
		this.modelIdList = modelIdList;
	}
	
}
