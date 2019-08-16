package io.renren.modules.spider.entity;

import java.io.Serializable;

public class UnpackRecord implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 打包记录的 id
	 */
	private String id;
	/**
	 * 压缩包的文件名
	 */
	private String zipname;
	
	/**
	 * 临时文件的父目录
	 */
	private String filestorePath;
	/**
	 * 压缩包的父目录
	 */
	private String zipPath;
	/**
	 * 打包的开始时间
	 */
	private String createTime;
	/**
	 * 数据所属的项目
	 */
	private String project;
	
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getZipname() {
		return zipname;
	}
	public void setZipname(String zipname) {
		this.zipname = zipname;
	}
	public String getFilestorePath() {
		return filestorePath;
	}
	public void setFilestorePath(String filestorePath) {
		this.filestorePath = filestorePath;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getZipPath() {
		return zipPath;
	}
	public void setZipPath(String zipPath) {
		this.zipPath = zipPath;
	}
	
	
	
	
}
