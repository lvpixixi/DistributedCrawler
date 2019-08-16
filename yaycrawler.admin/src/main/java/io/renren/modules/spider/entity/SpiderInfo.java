package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.alibaba.fastjson.JSONObject;

/**
 * 网页抽取模板
 *
 * @author Gao Shen
 * @version 16/4/12
 */
public class SpiderInfo implements Serializable{

	private static final long serialVersionUID = -2211329133190737811L;
	/**
     * 抓取模板id
     */
    private String id;
    
    /**
     * 网页抓取Url
     */
    @NotBlank(message="参数名不能为空")
    private String url;
    
    /**
     * 网站ID
     */
    @NotBlank(message="参数名不能为空")
    private String siteId;
    /**
     * 网站名称
     */
    @NotBlank(message="参数名不能为空")
    private String siteName;
    /**
     * 域名
     */
    @NotBlank(message="参数名不能为空")
    private String domain;
    /**
     * json配置
     */
    @NotBlank(message="参数名不能为空")
    private String jsonData;
    
    /**
     * 模板运行状态
     */
    private Integer status;
    /**
     * 模板运行信息
     */
    private String runmsg;
    
    /**
	 * 项目信息
	 */
    private String jsonProject;
    
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 项目ID列表
	 */
	private List<Long> projectIdList;
    
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
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
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getJsonData() {
		return jsonData;
	}
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public List<Long> getProjectIdList() {
		return projectIdList;
	}
	public void setProjectIdList(List<Long> projectIdList) {
		this.projectIdList = projectIdList;
	}
	public String getJsonProject() {
		return jsonProject;
	}
	public void setJsonProject(String jsonProject) {
		this.jsonProject = jsonProject;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getRunmsg() {
		return runmsg;
	}
	public void setRunmsg(String runmsg) {
		this.runmsg = runmsg;
	}
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
