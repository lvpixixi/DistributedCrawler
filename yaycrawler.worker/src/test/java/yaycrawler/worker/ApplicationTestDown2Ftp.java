package yaycrawler.worker;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yaycrawler.worker.service.Down2FtpManager;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WorkerApplication.class)
public class ApplicationTestDown2Ftp {

	@Autowired
	private Down2FtpManager fileDownConnManager;

	@Test
	public void testDownLoad() {
		String url = "http://www.combatindex.com/store/MCWP/Sample/TYPE_OPERATIONS/MCWP_3-35_7.pdf";
		for(int i=0;i<1000;i++) {
			try {
				fileDownConnManager.fileDown(url, "TYPE_OPERATIONS/MCWP_3-35_7"+i, "pdf", ".pdf");
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
