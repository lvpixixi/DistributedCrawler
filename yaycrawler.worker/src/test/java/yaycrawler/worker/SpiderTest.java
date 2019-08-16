package yaycrawler.worker;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import bscrawler.Application;
import bscrawler.spider.SpiderBuilder;
import bscrawler.spider.YaySpider;
import us.codecraft.webmagic.SpiderListener;
import yaycrawler.worker.model.FtpBean;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SpiderTest {
    private SpiderBuilder spiderBuilder;
    
    @Autowired
	private MongoTemplate mongoTemplate;
    
    @Value("${ftp.server.url}")
	private String ftpUrl;
	@Value("${ftp.server.port}")
	private int port;
	@Value("${ftp.server.username}")
	private String username;
	@Value("${ftp.server.password}")
	private String password;
	@Value("${ftp.server.path}")
	private String ftpPath;
	
	private SpiderListener downloadFailureListener = new DefaultSpiderListener();
	
	public SpiderBuilder getSpiderBuilder() {
		
		FtpBean ftpBean = new FtpBean();
		ftpBean.setFtpUrl(ftpUrl);
		ftpBean.setUsername(username);
		ftpBean.setPassword(password);
		ftpBean.setPort(port);
		ftpBean.setFtpPath(ftpPath);
		if(spiderBuilder==null) {
			spiderBuilder = new SpiderBuilder(mongoTemplate,ftpBean);
		}
		return spiderBuilder;
	}
    

    @Test
    public void testSpider() throws Exception
    {
       /* String id = "e25224a2522449fb82f3723e46ca3306";	
    	CrawlerConfig crawlerConfig = crawlerConfigService.getCrawlerConfig(id);
        YaySpider spider = this.spiderBuilder.builderByBean(crawlerConfig);*/
    	//String filePath = "E:\\GitLib\\BSCrawler\\YayCrawler\\yaycrawler.spider\\doc\\listDetail-gz.txt";
    	String filePath = "C:\\Users\\wgshb\\git\\BSCrawler\\YayCrawler\\yaycrawler.spider\\doc\\zhs\\listDetail_space.txt";
    	//String filePath = "E:\\GitLib\\BSCrawler\\YayCrawler\\yaycrawler.spider\\doc\\urls_model.txt";
    	YaySpider spider = this.getSpiderBuilder().builderByFile(filePath);
        spider.getSpiderListeners().add(downloadFailureListener);
        spider.run();
       
    }


}
