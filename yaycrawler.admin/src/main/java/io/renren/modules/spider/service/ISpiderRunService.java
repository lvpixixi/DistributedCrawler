package io.renren.modules.spider.service;

import java.util.List;
import java.util.Map;


/**
 * 采集模板服务接口
 * 
 * @author wangdw
 * @email dawei.happy@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */
public interface ISpiderRunService {
	
	/**
	 * 测试运行爬虫配置模板
	 * @param spiderId
	 * @return
	 */
	public List<Map<String, Object>> runSpiderTest(String spiderId)throws Exception;
	
	/**
	 * 批量运行所有的爬虫模板
	 */
	public void runAllSpiderTest();
   
}
