package io.renren;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.renren.modules.spider.service.SpiderSiteService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SiteFormatTest {
	
	@Autowired
	private SpiderSiteService spiderSiteService;

	@Test
	public void execute() throws Exception {
		spiderSiteService.checkUrls(null);
		
	}

}
