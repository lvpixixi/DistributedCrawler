package io.renren.modules.spider.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by wangdw on 16/1/14.
 */
@Component
@ConfigurationProperties(prefix="spiderConfig")
public class CrawlerBaseConfig {
    public String esHost;
    public int esPort;
    private Logger LOG = LoggerFactory.getLogger(CrawlerBaseConfig.class);
    private String commonsIndex;
    private int commonSpiderTaskManagerPort;
    private long maxHttpDownloadLength;
    private boolean commonsSpiderDebug;
    private String esClusterName;
    /**
     * 删除任务延时,单位为小时
     */
    private int taskDeleteDelay;
    /**
     * 删除任务时间间隔,单位为小时
     */
    private int taskDeletePeriod;
    /**
     * 普通网页下载器队列最大长度限制
     */
    private int limitOfCommonWebpageDownloadQueue;
    /**
     * 是否需要Redis
     */
    private boolean needRedis;
    private boolean needEs;
    private int redisPort;
    private String redisHost;
    private String webpageRedisPublishChannelName;
    /**
     * 抓取页面比例,如果抓取页面超过最大抓取数量ratio倍的时候仍未达到最大抓取数量爬虫也退出
     */
    private int commonsWebpageCrawlRatio;
    private String ajaxDownloader;
	public String getEsHost() {
		return esHost;
	}
	public void setEsHost(String esHost) {
		this.esHost = esHost;
	}
	public int getEsPort() {
		return esPort;
	}
	public void setEsPort(int esPort) {
		this.esPort = esPort;
	}
	public Logger getLOG() {
		return LOG;
	}
	public void setLOG(Logger lOG) {
		LOG = lOG;
	}
	public String getCommonsIndex() {
		return commonsIndex;
	}
	public void setCommonsIndex(String commonsIndex) {
		this.commonsIndex = commonsIndex;
	}
	public int getCommonSpiderTaskManagerPort() {
		return commonSpiderTaskManagerPort;
	}
	public void setCommonSpiderTaskManagerPort(int commonSpiderTaskManagerPort) {
		this.commonSpiderTaskManagerPort = commonSpiderTaskManagerPort;
	}
	public long getMaxHttpDownloadLength() {
		return maxHttpDownloadLength;
	}
	public void setMaxHttpDownloadLength(long maxHttpDownloadLength) {
		this.maxHttpDownloadLength = maxHttpDownloadLength;
	}
	public boolean isCommonsSpiderDebug() {
		return commonsSpiderDebug;
	}
	public void setCommonsSpiderDebug(boolean commonsSpiderDebug) {
		this.commonsSpiderDebug = commonsSpiderDebug;
	}
	public String getEsClusterName() {
		return esClusterName;
	}
	public void setEsClusterName(String esClusterName) {
		this.esClusterName = esClusterName;
	}
	public int getTaskDeleteDelay() {
		return taskDeleteDelay;
	}
	public void setTaskDeleteDelay(int taskDeleteDelay) {
		this.taskDeleteDelay = taskDeleteDelay;
	}
	public int getTaskDeletePeriod() {
		return taskDeletePeriod;
	}
	public void setTaskDeletePeriod(int taskDeletePeriod) {
		this.taskDeletePeriod = taskDeletePeriod;
	}
	public int getLimitOfCommonWebpageDownloadQueue() {
		return limitOfCommonWebpageDownloadQueue;
	}
	public void setLimitOfCommonWebpageDownloadQueue(int limitOfCommonWebpageDownloadQueue) {
		this.limitOfCommonWebpageDownloadQueue = limitOfCommonWebpageDownloadQueue;
	}
	public boolean isNeedRedis() {
		return needRedis;
	}
	public void setNeedRedis(boolean needRedis) {
		this.needRedis = needRedis;
	}
	public boolean isNeedEs() {
		return needEs;
	}
	public void setNeedEs(boolean needEs) {
		this.needEs = needEs;
	}
	public int getRedisPort() {
		return redisPort;
	}
	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}
	public String getRedisHost() {
		return redisHost;
	}
	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}
	public String getWebpageRedisPublishChannelName() {
		return webpageRedisPublishChannelName;
	}
	public void setWebpageRedisPublishChannelName(String webpageRedisPublishChannelName) {
		this.webpageRedisPublishChannelName = webpageRedisPublishChannelName;
	}
	public int getCommonsWebpageCrawlRatio() {
		return commonsWebpageCrawlRatio;
	}
	public void setCommonsWebpageCrawlRatio(int commonsWebpageCrawlRatio) {
		this.commonsWebpageCrawlRatio = commonsWebpageCrawlRatio;
	}
	public String getAjaxDownloader() {
		return ajaxDownloader;
	}
	public void setAjaxDownloader(String ajaxDownloader) {
		this.ajaxDownloader = ajaxDownloader;
	}
}
