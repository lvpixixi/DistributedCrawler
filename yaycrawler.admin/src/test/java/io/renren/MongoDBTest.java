package io.renren;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoDBTest {
	@Autowired
    private MongoTemplate mongoTemplate;
  

    @Test
    public void testSpider() throws Exception
    {
    	Set<String> cols = mongoTemplate.getDb().getCollectionNames();
    	System.out.println("-----------------------------------------------");
    	System.out.println(mongoTemplate.getDb().getMongo().getConnectPoint());
    	
    	System.out.println(mongoTemplate.getDb().getName());

    	System.out.println("-----------------------------------------------");
    	for(String col:cols) {
    		System.out.println(col);
    	}
    	System.out.println("-----------------------------------------------");
    }

    @Test
    public void testUpdate() throws Exception{
    	Query query = new Query();
    	Update update = Update.update("doubleStatus", 0);
    	update.set("doubleKey", "0");
    	mongoTemplate.updateMulti(query, update, "news");
    }
    
  /*  public void testUpdate(Set<String> ids) throws Exception{
    	String id = ids.iterator().next();
    	Query query = new Query(Criteria.where("_id").in(ids));
    	Update update = Update.update("isDouble", true).update("doubleKey", id);
    	mongoTemplate.updateMulti(query, update, "news");
    }
    
    
    @Test
    public void testSearch() throws Exception
    {
    	Query query = new Query();
 	   // query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "update_date")));
		//query.addCriteria(Criteria.where("pubDate").gte(DateUtil.strToDate("2019-01-01",DateUtil.simpleDateFormat_yyyy_MM_dd)).lte(DateUtil.strToDate("2019-03-27",DateUtil.simpleDateFormat_yyyy_MM_dd)));
		
 		List<KlVo> list = mongoTemplate.find(query, KlVo.class,"news");
 		for(KlVo map:list) {
 			System.out.println(map.getTitle());
 		}
 		
 		Set ids;
 		Set doubleIds = new HashSet();
 		for(int i=0;i<list.size()-1;i++) {
			KlVo vo_i = list.get(i);
			ids = new HashSet<String>();
			//System.out.println(vo_i.getTitle()+"---------------------------------");
			for(int j=i+1;j<list.size();j++) {
				KlVo vo_j = list.get(j);
				//System.out.println("|--"+vo_j.getTitle()+"---------------------------------");
				if(!doubleIds.contains(vo_j.getId())) {
					int dis = MySimHash.hammingDistance(new BigInteger(vo_i.getSimHash()), new BigInteger(vo_j.getSimHash()));
					if(dis<15) {
						doubleIds.add(vo_i.getId());
						doubleIds.add(vo_j.getId());
						ids.add(vo_i.getId());
						ids.add(vo_j.getId());
					}
				}
			}
			if(ids.size()>1) {
				System.out.println("记录重复的ID为---------------------------------");
				System.out.println(ids);
				testUpdate(ids);
			}
		}
    }*/

}
