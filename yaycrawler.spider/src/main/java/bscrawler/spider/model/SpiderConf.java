package bscrawler.spider.model;

import java.util.HashMap;
import java.util.Map;

import bscrawler.spider.SpiderConstants;

public class SpiderConf {
	
	/**
	 * 使用多少抓取线程
	 */
	private int thread = 1;
	/**
	 * 失败的网页重试次数
	 */
	private int retry = 2;
	/**
	 * 抓取每个网页睡眠时间
	 */
	private int sleep = 5000;
	/**
	 * 最大抓取网页数量,0代表不限制
	 */
	private int maxPageGather = 10;
	/**
	 * HTTP链接超时时间
	 */
	private int timeout = 50000;
	/**
	 * 网站权重
	 */
	private int priority;
	/**
	 * 网站名称
	 */
	private String siteName;
	/**
	 * 域名
	 */
	private String domain;
	/**
	 * 起始链接
	 */
	private String[] startURL;
	
	/**
	 * 编码
	 */
	private String charset = "utf-8";

	/**
	 * 爬取页数
	 */
	private int pageSize;

	/**
	 * 采集资源文件根路径
	 */
	private String rootPath;

	/**
	 * User Agent
	 */
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

	/**
	 * 请求头部信息
	 */
	private Map<String, String> headers = new HashMap<>();
	/**
	 * 请求cookies信息
	 */
	private Map<String, String> cookies = new HashMap<>();
	/**
	 * 是否保存网页快照,默认保存
	 */
	private boolean saveCapture = true;

	/**
	 * 是否使用代理,默认不需要代理
	 */
	private String proxy_type = SpiderConstants.PROXY_TYPE_NO;
	/**
	 * 代理ip
	 */
	private String proxy_Host;
	/**
	 * 代理端口
	 */
	private int proxy_port;
	/**
	 * 代理用户名
	 */
	private String proxy_user;
	/**
	 * 代理密码
	 */
	private String proxy_password;

	public int getThread() {
		return thread;
	}

	public void setThread(int thread) {
		this.thread = thread;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public int getSleep() {
		return sleep;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public int getMaxPageGather() {
		return maxPageGather;
	}

	public void setMaxPageGather(int maxPageGather) {
		this.maxPageGather = maxPageGather;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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

	public String[] getStartURL() {
		return startURL;
	}

	public void setStartURL(String[] startURL) {
		this.startURL = startURL;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public boolean isSaveCapture() {
		return saveCapture;
	}

	public void setSaveCapture(boolean saveCapture) {
		this.saveCapture = saveCapture;
	}

	public String getProxy_type() {
		return proxy_type;
	}

	public void setProxy_type(String proxy_type) {
		this.proxy_type = proxy_type;
	}

	public String getProxy_Host() {
		return proxy_Host;
	}

	public void setProxy_Host(String proxy_Host) {
		this.proxy_Host = proxy_Host;
	}

	public int getProxy_port() {
		return proxy_port;
	}

	public void setProxy_port(int proxy_port) {
		this.proxy_port = proxy_port;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public String getProxy_user() {
		return proxy_user;
	}

	public void setProxy_user(String proxy_user) {
		this.proxy_user = proxy_user;
	}

	public String getProxy_password() {
		return proxy_password;
	}

	public void setProxy_password(String proxy_password) {
		this.proxy_password = proxy_password;
	}
	
	

}
