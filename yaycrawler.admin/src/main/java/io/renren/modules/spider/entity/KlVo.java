package io.renren.modules.spider.entity;

import java.io.Serializable;

public class KlVo implements Serializable{
	
	//标识
    private String id;
    //标题
    private String title;

    //发布日期
    private String pubdate;
    
    //内容
    private String content;
    
    private Integer doubleStatus;
    
    private String doubleKey;
    
    private String simhash;
    
	public String getSimhash() {
		return simhash;
	}

	public void setSimhash(String simhash) {
		this.simhash = simhash;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPubdate() {
		return pubdate;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	public Integer getDoubleStatus() {
		return doubleStatus;
	}

	public void setDoubleStatus(Integer doubleStatus) {
		this.doubleStatus = doubleStatus;
	}

	public String getDoubleKey() {
		return doubleKey;
	}

	public void setDoubleKey(String doubleKey) {
		this.doubleKey = doubleKey;
	}
}
