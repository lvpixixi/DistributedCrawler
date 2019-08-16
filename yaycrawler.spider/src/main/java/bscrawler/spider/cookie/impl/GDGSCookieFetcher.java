package bscrawler.spider.cookie.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bscrawler.spider.cookie.ICookieProvider;
import bscrawler.spider.geetest.GeetestValidation;
import bscrawler.spider.geetest.GeetestValidationProvider;
import yaycrawler.common.model.CrawlerCookie;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.dao.domain.SiteAccount;
import yaycrawler.dao.repositories.SiteAccountRepository;


/**
 * Created by  yuananyun on 2017/3/25.
 */

public class GDGSCookieFetcher implements ICookieProvider {
    private static Logger logger = LoggerFactory.getLogger(GDGSCookieFetcher.class);
    private static String DOMAIN = "gd.gsxt.gov.cn";
    private long lastFetchTime = 0;
    private List<CrawlerCookie> cachedCookieList;

   


    /**
     * 表示是否支持某个域名获取cookie
     *
     * @param domain
     * @return
     */
    @Override
    public boolean support(String domain) {
        return DOMAIN.equalsIgnoreCase(domain);
    }

    @Override
    public List<CrawlerCookie> getCookies() {
        try {
            //如果少于5分钟则返回缓存
            if (cachedCookieList != null && System.currentTimeMillis() - lastFetchTime < (1 * 60 * 1000))
                return cachedCookieList;

            SiteAccount account = null;//siteAccountRepository.findOneByDomain(DOMAIN);
            if (account == null) {
                logger.error("[fetchCookieAndSave] {} have no account!", DOMAIN);
                return null;
            }
            ArrayList<Header> headerList = new ArrayList<>();

            headerList.add(new BasicHeader("Accept", "application/json, text/javascript, */*; q=0.01"));
            headerList.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
            headerList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
            headerList.add(new BasicHeader("Host", "gd.gsxt.gov.cn"));
            headerList.add(new BasicHeader("Origin", "http://gd.gsxt.gov.cn"));
            headerList.add(new BasicHeader("Referer", "http://gd.gsxt.gov.cn"));
            headerList.add(new BasicHeader("User-Agent", "ozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"));
            headerList.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));

            HttpUtil client = HttpUtil.getInstance();

            GeetestValidation validation;
            int tryCount = 3;
            do {
                validation = GeetestValidationProvider.getValidation("http://gd.gsxt.gov.cn", account.getUserName());
            } while (validation == null && tryCount-- > 0);
            if (validation != null && validation.getGeetest_validate() != null) {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("textfield", "当乐");
                paramMap.put("geetest_challenge", validation.getGeetest_challenge());
                paramMap.put("geetest_validate", validation.getGeetest_validate());
                paramMap.put("geetest_seccode", validation.getGeetest_seccode());

                List<org.apache.http.cookie.Cookie> cookieList = client.doPostCookies("http://gd.gsxt.gov.cn/aiccips/verify/sec.html", paramMap, headerList);
                if (cookieList != null) {
                    List<CrawlerCookie> crawlerCookieList = new ArrayList<>();
                    for (Cookie cookie : cookieList) {
                        crawlerCookieList.add(new CrawlerCookie(cookie.getName(), cookie.getValue()));
                    }
                    cachedCookieList = crawlerCookieList;
                    lastFetchTime = System.currentTimeMillis();
                    return crawlerCookieList;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
