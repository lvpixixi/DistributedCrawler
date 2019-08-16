package yaycrawler.spider;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Maps;

import bscrawler.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class MongoDBTest {
	@Autowired
    private MongoTemplate mongoTemplate;
  


    
    @Test
    public void testSave() throws Exception
    {
    	try {
    		Map<String,String> data = Maps.newHashMap();
    		data.put("_id", "1");
    		data.put("name", "wdw");
            mongoTemplate.save(data, "crawler");
            
            data = Maps.newHashMap();
    		data.put("_id", "1");
    		data.put("name", "dwd");
            mongoTemplate.save(data, "crawler");
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
       
    }


}
