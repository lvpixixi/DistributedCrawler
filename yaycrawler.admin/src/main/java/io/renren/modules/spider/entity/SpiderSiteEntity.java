package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * 
 * @author wangdawei
 * @email dawei.happy@gmail.com
 * @date 2019-04-10 10:02:14
 */
public class SpiderSiteEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private String id;
	//
	private String url;
	//
	private String siteName;
	//
	private String domain;
	//
	private String charset;
	//
	private Integer cycleRetryTimes;
	//
	private String defaultCookies;
	//
	private String headers;
	//
	private Integer retryTimes;
	//
	private Integer sleepTime;
	//
	private Integer timeOut;
	//
	private String userAgent;
	//
	private String loginJsFileName;
	//
	private String loginJudgeExpression;
	//
	private String note;
	//
	private Integer status;
	
	private Integer rspCode;
	
	private List<Long> projectIdList;

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
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	/**
	 * 获取：
	 */
	public String getSiteName() {
		return siteName;
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
	public void setCharset(String charset) {
		this.charset = charset;
	}
	/**
	 * 获取：
	 */
	public String getCharset() {
		return charset;
	}
	/**
	 * 设置：
	 */
	public void setCycleRetryTimes(Integer cycleRetryTimes) {
		this.cycleRetryTimes = cycleRetryTimes;
	}
	/**
	 * 获取：
	 */
	public Integer getCycleRetryTimes() {
		return cycleRetryTimes;
	}
	/**
	 * 设置：
	 */
	public void setDefaultCookies(String defaultCookies) {
		this.defaultCookies = defaultCookies;
	}
	/**
	 * 获取：
	 */
	public String getDefaultCookies() {
		return defaultCookies;
	}
	/**
	 * 设置：
	 */
	public void setHeaders(String headers) {
		this.headers = headers;
	}
	/**
	 * 获取：
	 */
	public String getHeaders() {
		return headers;
	}
	/**
	 * 设置：
	 */
	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}
	/**
	 * 获取：
	 */
	public Integer getRetryTimes() {
		return retryTimes;
	}
	/**
	 * 设置：
	 */
	public void setSleepTime(Integer sleepTime) {
		this.sleepTime = sleepTime;
	}
	/**
	 * 获取：
	 */
	public Integer getSleepTime() {
		return sleepTime;
	}
	/**
	 * 设置：
	 */
	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}
	/**
	 * 获取：
	 */
	public Integer getTimeOut() {
		return timeOut;
	}
	/**
	 * 设置：
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	/**
	 * 获取：
	 */
	public String getUserAgent() {
		return userAgent;
	}
	/**
	 * 设置：
	 */
	public void setLoginJsFileName(String loginJsFileName) {
		this.loginJsFileName = loginJsFileName;
	}
	/**
	 * 获取：
	 */
	public String getLoginJsFileName() {
		return loginJsFileName;
	}
	/**
	 * 设置：
	 */
	public void setLoginJudgeExpression(String loginJudgeExpression) {
		this.loginJudgeExpression = loginJudgeExpression;
	}
	/**
	 * 获取：
	 */
	public String getLoginJudgeExpression() {
		return loginJudgeExpression;
	}
	/**
	 * 设置：
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * 获取：
	 */
	public String getNote() {
		return note;
	}
	/**
	 * 设置：
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：
	 */
	public Integer getStatus() {
		return status;
	}
	
	public Integer getRspCode() {
		return rspCode;
	}
	public void setRspCode(Integer rspCode) {
		this.rspCode = rspCode;
	}
	public List<Long> getProjectIdList() {
		return projectIdList;
	}
	public void setProjectIdList(List<Long> projectIdList) {
		this.projectIdList = projectIdList;
	}
	
}
