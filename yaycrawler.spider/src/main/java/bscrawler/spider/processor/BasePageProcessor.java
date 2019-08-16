package bscrawler.spider.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import bscrawler.spider.extractor.ObjectModelExtractor;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;

public abstract class BasePageProcessor{
	/**
	 * 请求类型
	 */
	public final static String TYPE_LABEL = "type";
	
	
	/**
	 * 采集任务属于哪个项目
	 */
    protected Set<String> projects;    
    protected String spiderId;
    protected Site site;    
    protected Set<ObjectModelExtractor> extractors = new HashSet<>();  
    public Set<String> getProjects() {
		return projects;
	}
	public void setProjects(Set<String> projects) {
		this.projects = projects;
	}
	
	public void iniPageModel(List extractors) {
    	for(int i=0;i<extractors.size();i++){
    		JSONObject json = (JSONObject)extractors.get(i);
    		ObjectModelExtractor extractor = JSONObject.toJavaObject(json, ObjectModelExtractor.class);
    		extractor.setDomain(this.site.getDomain());
    		this.extractors.add(extractor);
    	}
    }
	
	public void iniPageField(Page page) {
    	page.putField("url", page.getRequest().getUrl());
    	page.putField("rawText", page.getRawText());
    	page.putField("domain", site.getDomain());
    	
    	if(this.getProjects()!=null&&this.getProjects().size()>0) {
    		page.putField("project", StringUtils.join(this.getProjects(), ","));
        }else {
        	page.putField("project", "");
        }
	}
	

	/**
     * 1基于主键列md5方式生产主键
     * @param pks
     * @param obj
     * @return
     */
    public String generatorId(List<String> pks,Map<String, Object> obj) {
		StringBuilder sb = new StringBuilder();
		for(String pkName:pks) {
			Object value = obj.get(pkName);
			if(value!=null) {
				sb.append(value);
			}
		}
		return DigestUtils.md5Hex(sb.toString());
	}
    
    protected void onGetOneRecord(String spiderId,int count,List<SpiderListener> listeners) {
        if (CollectionUtils.isNotEmpty(listeners)) {
            for (SpiderListener spiderListener : listeners) {
                spiderListener.onGetOneRecord(spiderId,count);
            }
        }
    }
  
    
    
    
    public Site getSite() {
	      return site;
	}
    public void printMap(Object obj){
    	System.out.println(JSON.toJSON(obj));
    }
	public String getSpiderId() {
		return spiderId;
	}
	public void setSpiderId(String spiderId) {
		this.spiderId = spiderId;
	}
    
    
	
}
