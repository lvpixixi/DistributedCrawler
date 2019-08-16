package yaycrawler.spider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import bscrawler.Application;
import bscrawler.spider.download.Down2FtpComp;
import bscrawler.spider.download.FtpBean;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class File2FTPTest {
	
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

    @Test
    public void test() throws Exception
    {
    	FtpBean ftpBean = new FtpBean();
		ftpBean.setFtpUrl(ftpUrl);
		ftpBean.setUsername("admin");
		ftpBean.setPassword("admin");
		ftpBean.setPort(port);
		ftpBean.setFtpPath(ftpPath);
    	
    	Down2FtpComp down2FtpManager = new Down2FtpComp(ftpBean);
    	String downUrl= "http://www.81.cn/jmywyl/attachement/png/site351/20190130/309c236f8c3b1dbb1d2c05.png";
    	String RelativeUrl = "../attachement/png/site351/309c236f8c3b1dbb1d2c05";
    	String fileStorePath = "/20190130/www.81.cn";
    	String extName=".png";
    	down2FtpManager.fileDown(downUrl, RelativeUrl, fileStorePath, extName);
       
    }


}
