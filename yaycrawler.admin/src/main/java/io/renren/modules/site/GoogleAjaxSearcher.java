/**
 *
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.renren.modules.site;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.renren.modules.spider.utils.HttpUtils;

public class GoogleAjaxSearcher implements Searcher{
    private static final Logger LOG = LoggerFactory.getLogger(GoogleAjaxSearcher.class);

    @Override
    public SearchResult search(String keyword) {
        return search(keyword, 1);
    }
    @Override
    public SearchResult search(String keyword, int page) {
        int pageSize = 8;
        //谷歌搜索结果每页大小为8，start参数代表的是返回结果的开始数
        //如获取第一页则start=0，第二页则start=10，第三页则start=20，以此类推，抽象出模式：(page-1)*pageSize
        String url = "http://ajax.googleapis.com/ajax/services/search/web?start="+(page-1)*pageSize+"&rsz=large&v=1.0&q=" + keyword;
        
        SearchResult searchResult = new SearchResult();
        searchResult.setPage(page);
        List<Webpage> webpages = new ArrayList<>();
        try {
        	String content = HttpUtils.doGet(url, null, "127.0.0.1", 51279);
            JSONObject json = new JSONObject(content);
            String totalResult = json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount");
            int totalResultCount = Integer.parseInt(totalResult);
            LOG.info("搜索返回记录数： " + totalResultCount);
            searchResult.setTotal(totalResultCount);

            JSONArray results = json.getJSONObject("responseData").getJSONArray("results");

            LOG.debug("搜索结果:");
            for (int i = 0; i < results.length(); i++) {
                Webpage webpage = new Webpage();
                JSONObject result = results.getJSONObject(i);
                //提取标题
                String title = result.getString("titleNoFormatting");
                LOG.debug("标题：" + title);
                webpage.setTitle(title);
                //提取摘要
                String summary = result.get("content").toString();
                summary = summary.replaceAll("<b>", "");
                summary = summary.replaceAll("</b>", "");
                summary = summary.replaceAll("\\.\\.\\.", "");
                LOG.debug("摘要：" + summary);
                webpage.setSummary(summary);
               /* //从URL中提取正文
                String _url = result.get("url").toString();
                webpage.setUrl(_url);
                String content = Tools.getHTMLContent(_url);
                LOG.debug("正文：" + content);
                webpage.setContent(content);*/
                webpages.add(webpage);
            }
        } catch (JSONException | NumberFormatException e) {
            LOG.error("执行搜索失败：", e);
        }
        searchResult.setWebpages(webpages);
        return searchResult;
    }

    public static void main(String args[]) {
        String keyword = "杨尚川";
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("url构造失败", e);
            return;
        }
        
        Searcher searcher = new GoogleAjaxSearcher();
        SearchResult searchResult = searcher.search(keyword, 1);
        List<Webpage> webpages = searchResult.getWebpages();
        if (webpages != null) {
            int i = 1;
            LOG.info("搜索结果 当前第 " + searchResult.getPage() + " 页，页面大小为：" + searchResult.getPageSize() + " 共有结果数：" + searchResult.getTotal());
            for (Webpage webpage : webpages) {
                LOG.info("搜索结果 " + (i++) + " ：");
                LOG.info("标题：" + webpage.getTitle());
                LOG.info("URL：" + webpage.getUrl());
                LOG.info("摘要：" + webpage.getSummary());
                LOG.info("正文：" + webpage.getContent());
                LOG.info("");
            }
        } else {
            LOG.error("没有搜索到结果");
        }
    }
}