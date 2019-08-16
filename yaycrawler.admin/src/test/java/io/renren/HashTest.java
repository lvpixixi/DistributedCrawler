package io.renren;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Maps;

import io.renren.modules.spider.dao.NewsDao;
import io.renren.modules.spider.entity.News;
import yaycrawler.common.utils.MySimHash;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HashTest {

	@Autowired
	NewsDao NewsDao;

	@Test
	public void testMagic() {
		
		try {
			List<News> list = NewsDao.queryList(Maps.newHashMap());
			News news1 = NewsDao.queryObject("1774dae7b4f54347b2b8570c7468baa1");
			//MySimHash hash1 = new MySimHash(news1.getContent());
			long startTime = System.currentTimeMillis();
			for(News news:list) {
				/*MySimHash hash2 = new MySimHash(news.getContent());
				System.out.println(news.getTitle()+",simhash="+hash2.getSimHash().toString());
				news.setH1(hash2.getSimHash().toString());
				news.setH2("");
				news.setH3("");
				news.setH4("");
				this.NewsDao.updateNewsSimCode(news);*/
				int dis = MySimHash.hammingDistance(new BigInteger(news1.getH1()), new BigInteger(news.getH1()));
				if(dis<12) {
					System.out.println(news.getTitle()+",hammingDis="+dis);
				}
			}
			long l4 = System.currentTimeMillis();
		    System.out.println("总共耗时:"+(l4-startTime)+"毫秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
