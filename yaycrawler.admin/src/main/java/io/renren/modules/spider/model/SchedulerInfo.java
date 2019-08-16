package io.renren.modules.spider.model;

import java.util.Date;

public class SchedulerInfo {
	
	private String spiderInfoId;
	
	private String siteName;
	
	private Date previousFireTime;
	
	private Date nextFireTime;
	
	private Date startTime;

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getSpiderInfoId() {
		return spiderInfoId;
	}

	public void setSpiderInfoId(String spiderInfoId) {
		this.spiderInfoId = spiderInfoId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}


}
