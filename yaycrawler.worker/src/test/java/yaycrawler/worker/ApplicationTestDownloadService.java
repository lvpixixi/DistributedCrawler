package yaycrawler.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableMap;

import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.worker.service.DownloadService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WorkerApplication.class)
public class ApplicationTestDownloadService {

	@Autowired
	private DownloadService downloadService;

	@Test
	public void testDownLoad() {
		List<CrawlerRequest> downList = new ArrayList<>();
		//http://www.81.cn/sydbt/attachement/jpg/site351/20181123/309c236f8c3b1d6242e255.jpg
		CrawlerRequest crawlerRequest = new CrawlerRequest();
		crawlerRequest.setDomain("www.81.cn");
		crawlerRequest.setHashCode("http://www.81.cn/sydbt/attachement/jpg/site351/20181123/309c236f8c3b1d6242e255.jpg".hashCode()+"");
		crawlerRequest.setMethod("get");
		crawlerRequest.setUrl("http://www.81.cn/sydbt/attachement/jpg/site351/20181123/309c236f8c3b1d6242e255.jpg");
		
		List<String> urls = new ArrayList<String>();
		urls.add("http://www.81.cn/sydbt/attachement/jpg/site351/20181123/309c236f8c3b1d6242e255.jpg");
		Map<String,Object> data = ImmutableMap.of("$DOWNLOAD",".jpg","$src",urls);
		crawlerRequest.setExtendMap(data);
		downList.add(crawlerRequest);
		downloadService.startCrawlerDownload(downList);
	}

}
