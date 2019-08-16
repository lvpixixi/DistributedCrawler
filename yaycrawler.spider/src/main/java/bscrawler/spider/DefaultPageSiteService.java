package bscrawler.spider;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.stereotype.Service;

import bscrawler.spider.model.SpiderConf;
import bscrawler.spider.util.MayiProxyUtil;
import us.codecraft.webmagic.Site;

/**
 * Created by yuananyun on 2016/5/2.
 */
public class DefaultPageSiteService implements IPageSiteService{
	
	private SpiderConf config;
	
	public SpiderConf getConfig() {
		return config;
	}

	public void setConfig(SpiderConf config) {
		this.config = config;
	}

    public Site getSite(String domain) {
        return getSite(domain, config.getProxy_type());
    }

    /**
     * 获取Site
     *
     * @param domain
     * @param needProxy 是否需要切换代理
     * @return
     */
    public Site getSite(String domain, String proxyType) {
		Site site = Site.me();
		site.setDomain(config.getDomain());
		site.setSleepTime(config.getSleep());
		site.setTimeOut(config.getTimeout());
		site.setRetryTimes(config.getRetry());
		site.setCharset(config.getCharset());
		//site.addHeader("host", config.getDomain());
		site.setUserAgent(config.getUserAgent());
		site.addHeader("User-Agent", config.getUserAgent());
	    //默认重试3次
	    site.setCycleRetryTimes(4);
        
    	if(StringUtils.isEmpty(config.getUserAgent())) {
			site.addHeader("User-Agent", config.getUserAgent());
		}
	    //处理header
	    Map<String, String> headMap = config.getHeaders();
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            site.addHeader(entry.getKey(), entry.getValue());
        }
	    
        //处理Cookies
        Map<String, String> cookiesMap = config.getCookies();
        for (Map.Entry<String, String> entry : cookiesMap.entrySet()) {
            site.addCookie(config.getDomain(), entry.getKey(), entry.getValue());
        }
        HttpHost httpProxy;
        switch(proxyType) {
        	case SpiderConstants.PROXY_TYPE_LOCAL:
        		httpProxy = new HttpHost(config.getProxy_Host(), config.getProxy_port());
        		site.setHttpProxy(httpProxy);
        		break;
        	case SpiderConstants.PROXY_TYPE_MAYI:
        		httpProxy = new HttpHost(config.getProxy_Host(), config.getProxy_port());
        		site.setHttpProxy(httpProxy);
        		String authHeader = MayiProxyUtil.getAuthHeader(config.getProxy_user(), config.getProxy_password());
        		site.addHeader("Proxy-Authorization", authHeader);
        		break;
        	default:
        }
        
		return site;
    }
}
