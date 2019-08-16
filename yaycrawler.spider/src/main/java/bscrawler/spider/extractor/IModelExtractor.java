package bscrawler.spider.extractor;

import java.util.List;
import java.util.Set;

import us.codecraft.webmagic.Page;

/**
 * 模型抽取接口
 * @author wdw
 *
 */
public interface IModelExtractor {
	/**
	 * 抽取页面对象
	 * @param page
	 * @return
	 */
	public Object getObject(Page page,List<String> pageLinks,List<ResourceFile> resources);

}
