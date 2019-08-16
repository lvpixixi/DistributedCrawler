package io.renren.modules.spider.service;


import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 采集网页查询服务接口
 * 
 * @author mfq
 * @email 774638327@qq.com
 * @date 2018年4月2日 
 */
public interface INewsQueryService {
	
	/**
	 * 根据关键字匹配标题检索*/
    public List<Map<String,Object>> searchByQuery(String entityName,Map<String, Object> map,int pageIndex,int pageSize);
    
    /**
	 * 根据关键字匹配标题检索总条数*/
    public long searchCountByQuery(String entityName,Map<String, Object> map);
    
    /**
	 * 根据id 获取news*/
    public Map<String,Object> searchByid(String entityName,String id);
    
    /**
             * 获取数据对象
     * @return
     */
    public Set<String> getEntitys();

    /**
     * 将指定的 id 记录下载到本地
     * @param ids 
     * @param entity
     * @return 
     */
	public String down2Local(String[] ids, String entity);

	/**
	 * 修改新闻的信息
	 * @param jsonParams
	 */
	public void update(String jsonParams);

	/**
	 * 审核单条新闻
	 * @param jsonParams
	 */
	public void check(String jsonParams);

	/**
	 * 批量审核新闻
	 * @param jsonParams
	 */
	public void multiCheck(String jsonParams);

	/**
	 * 根据 entity 查询对应的项目名称
	 * @param params
	 * @return
	 */
	public String getProject(String params);

	public String down2local(String params);

	/**
	 * 抽取实体对象
	 * @param params
	 * @return
	 */
	public Map extract(String params);

	/**
	 * 生成报告
	 * @param params
	 * @return
	 */
	public String report(String params);

}
