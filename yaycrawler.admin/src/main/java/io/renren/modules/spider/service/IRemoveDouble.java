package io.renren.modules.spider.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IRemoveDouble {
	
	//重复状态
	public static final String STATUS_DOUBLE = "-2";
	//初始状态
	public static final String STATUS_INITIAL = "0";
	//审核通过
	public static final String STATUS_PASS = "1";
	//审核不通过
	public static final String STATUS_DEL = "-1";
	
	/**
	 * 新闻相似度计算
	 * @param type
	 * @param fromdate
	 * @param todate
	 * @param 距离参数
	 */
	public int executeCompute(String entity, Date fromdate, Date todate,int distance);
	
	/**
	 * 获取重复新闻列表
	 * @param entity
	 * @param keyWord
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Map> searchDoubleList(String entity,String keyWord,Map<String,Object> params,int pageIndex,int pageSize);
			

	/**
	 * 获取重复新闻数量
	 * @param entity
	 * @param keyWord
	 * @param params
	 * @return
	 */
	public long searchDoubleCount(String entity, String keyWord, Map<String, Object> params);
	
	/**
	 * 批量核实非重复的记录或保留重复记录中的一条记录
	 * @param entity
	 * @param ids
	 */
	public void multiVerifyNODouble(String entity,List<String> ids);
}
