package io.renren.modules.spider.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.renren.modules.spider.model.Webpage;

/**
 * 采集网页查询服务接口
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */
public interface IWebPageService {
	
	/**
     * 根据spiderUUID返回该spider抓取到的文章
     *
     * @param spiderUUID
     * @return
     */
    public List<Webpage> getWebpageListBySpiderUUID(String spiderUUID, int size, int page);
    
    
    /**
     * 根据domain获取结果,按照抓取时间排序
     *
     * @param domain 网站域名
     * @param size   每页数量
     * @param page   页码
     * @return
     */
    public List<Webpage> getWebpageByDomain(String domain, int size, int page);
    
    
    /**
     * 根据domain列表获取结果
     *
     * @param domain 网站域名列表
     * @param size   每页数量
     * @param page   页码
     * @return
     */
    public List<Webpage> getWebpageByDomains(Collection<String> domain, int size, int page);
    
    
    /**
     * 根据关键词搜索网页
     *
     * @param query 关键词
     * @param size  每页数量
     * @param page  页码
     * @return
     */
    public List<Webpage> searchByQuery(String query, int size, int page);
    
    /**
     * 根据ES中的id获取网页
     *
     * @param id 网页id
     * @return
     */
    public List<Webpage> getWebpageById(String id);
    
    /**
     * 根据id删除网页
     *
     * @param id 网页id
     * @return 是否删除
     */
    public List<Boolean> deleteById(String id);
    
    /**
     * 获取所有网页,并按照抓取时间排序
     *
     * @param size 每页数量
     * @param page 页码
     * @return
     */
    public List<Webpage> listAll(int size, int page);
    
    /**
     * 聚合所有网页的Domain信息
     *
     * @param size 大小
     * @return
     */
    public List<Map<String, Long>> countDomain(int size);
    
    /**
     * 根据网站的文章ID获取相似网站的文章
     *
     * @param id   文章ID
     * @param size 页面容量
     * @param page 页码
     * @return
     */
    public List<Webpage> moreLikeThis(String id, int size, int page);

}
