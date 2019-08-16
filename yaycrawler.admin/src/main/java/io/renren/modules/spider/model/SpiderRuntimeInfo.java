package io.renren.modules.spider.model;

import java.util.Date;



import us.codecraft.webmagic.Spider.Status;

public class SpiderRuntimeInfo {
	private String id;
	private String taskName;
	private long pageCount;
	private Date startTime;
	private int threadAlive;
	private int status;

	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public long getPageCount() {
		return pageCount;
	}
	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public int getThreadAlive() {
		return threadAlive;
	}
	public void setThreadAlive(int threadAlive) {
		this.threadAlive = threadAlive;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		if(Status.Running ==status){
			this.status = 1;
		}else{
			this.status = 0;
		}
		
	}

}
