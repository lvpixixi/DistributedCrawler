package io.renren.modules.spider.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mss.crawler.custom.weapon.KeywordsThesaurus;

import io.renren.modules.sys.dao.BaseDao;

@Mapper
public interface KeywordsThesaurusMapper extends BaseDao<KeywordsThesaurus> {

	void insertThesaurus(KeywordsThesaurus thesaurus);

	void trimThesaurusNum(KeywordsThesaurus thesaurus);
	
	List<KeywordsThesaurus> getAllThesaurusNum4Trim();

	/**
	 * 获取所有的词表项
	 * @return
	 */
	List<KeywordsThesaurus> getAllTargetFields();
	
	/**
	 * 根据请求参数进行更新操作
	 * @param params
	 */
	void separateString2TargetFields(Map<String, String> params);

	/**
	 * 批量更新目标对象
	 * @param subList
	 */
	void batchUpdateKeywordsThesaurus(List<KeywordsThesaurus> subList);
	
}
