package io.renren.modules.spider.entity;

import java.io.Serializable;
import java.util.Date;

public class News implements Serializable{
	
	private static final long serialVersionUID = 4212164452199968156L;
	//标识
    private String id;
    //标题
    private String title;

    //发布日期
    private String pubdate;
    
    //url
    private String url;
    
    //内容
    private String content;
    
    //附件
    private String attchfiles;    
    
    //来源
    private String src;  
    
    //发布者
    private String author;
    
    //域名
    private String siteDomain;
    
    //备注
    private String note;
    
    //采集时间
    private Date crawlerdate;
    
    // 针对外文网站的 内容文本翻译
    private String contentTr;
    
    // 格式化后的日期
    private String formattedContent;
    
    // 新闻摘要
    private String summary;
    
    // 查询文本
    private String searchText;
    
    private String h1;
    
    private String h2;
    
    private String h3;
    
    private String h4;

    
	public String getH1() {
		return h1;
	}

	public void setH1(String h1) {
		this.h1 = h1;
	}

	public String getH2() {
		return h2;
	}

	public void setH2(String h2) {
		this.h2 = h2;
	}

	public String getH3() {
		return h3;
	}

	public void setH3(String h3) {
		this.h3 = h3;
	}

	public String getH4() {
		return h4;
	}

	public void setH4(String h4) {
		this.h4 = h4;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFormattedContent() {
		return formattedContent;
	}

	public void setFormattedContent(String formattedContent) {
		this.formattedContent = formattedContent;
	}

	public String getContentTr() {
		return contentTr;
	}

	public void setContentTr(String contentTr) {
		this.contentTr = contentTr;
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



	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttchfiles() {
		return attchfiles;
	}

	public void setAttchfiles(String attchfiles) {
		this.attchfiles = attchfiles;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPubdate() {
		return pubdate;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getSiteDomain() {
		return siteDomain;
	}

	public void setSiteDomain(String siteDomain) {
		this.siteDomain = siteDomain;
	}

	public Date getCrawlerdate() {
		return crawlerdate;
	}

	public void setCrawlerdate(Date crawlerdate) {
		this.crawlerdate = crawlerdate;
	}

	
}
