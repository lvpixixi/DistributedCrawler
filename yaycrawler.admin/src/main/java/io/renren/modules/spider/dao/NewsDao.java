package io.renren.modules.spider.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.renren.modules.spider.entity.News;
import io.renren.modules.sys.dao.BaseDao;


/**
 * 爬取信息
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */

@Mapper
public interface NewsDao extends BaseDao<News> {
	/**
	 * 通过keyword匹配title检索*/
	List<News> queryListByKeyword(Map<String, Object> map);
	
	/**
	 * 通过keyword匹配title检索总条数*/
	int queryListCountByKeyword(Map<String, Object> map);
	
	/**
	 * 通过keyword匹配title检索总条数*/
	News queryById(Map<String, Object> map);
	
	/**
	 * 通过 crawlerdate 查询对应的总的所有符合条件的数据
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getAllBy(Map<String, Object> params);
	
	List<News> getNamespace4Replace();

	/**
	 * 批量更新新闻
	 * @param lists
	 */
	void updatebatch(List<News> lists, @Param("tbname") String tableName);

	/**
	 * 更新新闻
	 * @param news
	 */
	void updateNews(@Param("formattedContent") String formattedContent, 
			@Param("tbname") String tableName,
			@Param("id") String id);
	
	/**
	 * 更新新闻的摘要字段
	 */
	void updateNewsSummary(News news);
	
	/**
	 * 更新新闻的摘要字段
	 */
	void updateNewsSimCode(News news);
	
	/**
	 * 获取所有的新闻对象
	 * @return
	 */
	List<News> selectNews2update();

	/**
	 * 获取武器库武器的所有的名称
	 */
	List<String> getWeaponNames();
}
