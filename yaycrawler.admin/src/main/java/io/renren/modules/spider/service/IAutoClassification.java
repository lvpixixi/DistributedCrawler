package io.renren.modules.spider.service;

import java.util.Date;

public interface IAutoClassification {
	
	/**
	 * 新闻相似度计算
	 * @param type
	 * @param fromdate
	 * @param todate
	 */
	public void executeClassification(String entity,Date fromdate,Date todate);

}
