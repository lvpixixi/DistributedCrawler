package io.renren.modules.spider.model;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

/**
 * Webpage
 *
 * @author Gao Shen
 * @version 16/5/6
 */
public class Webpage {
    /**
     * 附件id列表
     */
    public List<String> attachmentList;
    /**
     * 图片ID列表
     */
    public List<String> imageList;
    /**
     * jsonData
     */
    
    public String jsonData;
   
    /**
     * 链接
     */
    private String url;
    /**
     * 域名
     */
    private String domain;
    /**
     * 爬虫id,可以认为是taskid
     */
    private String spiderUUID;
    /**
     * 模板id
     */
    @SerializedName("spiderInfoId")
    private String spiderInfoId;
    /**
     * 分类
     */
    private String category;
    /**
     * 网页快照
     */
    private String rawHTML;
    
    /**
     * 抓取时间
     */
    @SerializedName("gatherTime")
    private Date gathertime;
    /**
     * 网页id,es自动分配的
     */
    private String id;    
    
    /**
     * 本网页处理时长
     */
    private long processTime;

   

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSpiderInfoId() {
        return spiderInfoId;
    }

    public void setSpiderInfoId(String spiderInfoId) {
        this.spiderInfoId = spiderInfoId;
    }

    public Date getGathertime() {
        return gathertime;
    }

    public void setGathertime(Date gathertime) {
        this.gathertime = gathertime;
    }

    public String getSpiderUUID() {
        return spiderUUID;
    }

    public void setSpiderUUID(String spiderUUID) {
        this.spiderUUID = spiderUUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

  

    public String getCategory() {
        return category;
    }

    public Webpage setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getRawHTML() {
        return rawHTML;
    }

    public Webpage setRawHTML(String rawHTML) {
        this.rawHTML = rawHTML;
        return this;
    }

   

    public List<String> getAttachmentList() {
        return attachmentList;
    }

    public Webpage setAttachmentList(List<String> attachmentList) {
        this.attachmentList = attachmentList;
        return this;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public Webpage setImageList(List<String> imageList) {
        this.imageList = imageList;
        return this;
    }

    public long getProcessTime() {
        return processTime;
    }

    public Webpage setProcessTime(long processTime) {
        this.processTime = processTime;
        return this;
    }
    

    public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	@Override
    public String toString() {
        return MoreObjects.toStringHelper(this)           
                .add("url", url)
                .add("domain", domain)
                .add("spiderUUID", spiderUUID)
                .add("spiderInfoId", spiderInfoId)
                .add("category", category)
                .add("rawHTML", rawHTML)             
                .add("gathertime", gathertime)
                .add("id", id)            
                .add("attachmentList", attachmentList)
                .add("imageList", imageList)
                .add("jsonData", jsonData)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Webpage webpage = (Webpage) o;

        return new EqualsBuilder()             
                .append(getUrl(), webpage.getUrl())
                .append(getDomain(), webpage.getDomain())
                .append(getSpiderUUID(), webpage.getSpiderUUID())
                .append(getSpiderInfoId(), webpage.getSpiderInfoId())
                .append(getCategory(), webpage.getCategory())
                .append(getRawHTML(), webpage.getRawHTML())
                .append(getGathertime(), webpage.getGathertime())
                .append(getId(), webpage.getId())              
                .append(getAttachmentList(), webpage.getAttachmentList())
                .append(getImageList(), webpage.getImageList())
                .append(getJsonData(), webpage.getJsonData())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getUrl())
                .append(getDomain())
                .append(getSpiderUUID())
                .append(getSpiderInfoId())
                .append(getCategory())
                .append(getRawHTML())
                .append(getGathertime())
                .append(getId())
                .append(getAttachmentList())
                .append(getImageList())
                .append(getJsonData())
                .toHashCode();
    }
}
