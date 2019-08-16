package io.renren.modules.spider.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import bscrawler.spider.download.IFtpConfig;

@Component
public class FtpBean implements IFtpConfig {
	
	@Value("${ftp.server.url}")
	private String ftpUrl;
	@Value("${ftp.server.port}")
	private int port;
	@Value("${ftp.server.username}")
	private String username;
	@Value("${ftp.server.password}")
	private String password;
	@Value("${ftp.server.path}")
	private String ftpPath;
	public String getFtpUrl() {
		return ftpUrl;
	}
	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFtpPath() {
		return ftpPath;
	}
	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}
	
	

}
