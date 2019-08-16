package bscrawler.spider.pipeline;

import java.util.Map.Entry;
import java.util.Set;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class ConsolePageModelPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		
		Set<Entry<String,Object>> set = resultItems.getAll().entrySet();
		StringBuilder sb = new StringBuilder();
		for(Entry<String,Object> entry:set){
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("/n");
			
		}
		
	}

}
