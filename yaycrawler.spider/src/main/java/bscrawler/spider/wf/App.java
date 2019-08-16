package bscrawler.spider.wf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

	public static void main(String[] args) throws Exception {
	    	
	    	
	    	String req =  "http://www.wanfangdata.com.cn/searchResult/getCoreSearch.do";  
	    	/*String keyWord = "computing platform";
	    	String classId = "1005628921800163328";*/
	    	
	    	/**
	    	 * "1005630135635935232,聚类算法,Deep learning",
						"1005631856760193024,计算机视觉,computer vision",
						"1005633048512954368,语音识别,voice recognition",
						"1005633344874086400,计算平台,computing platform"
	    	 */
	    	
	    	Map<String,String> classMap = new HashMap<String,String>();
	    	classMap.put("1005628921800163328", "machine learning");
	    	classMap.put("1005630135635935232", "deep learning");
	    	classMap.put("1005631856760193024", "computer vision");
	    	classMap.put("1005633048512954368", "voice recognition");
	    	classMap.put("1005633344874086400", "computing platform");
	    	
	    	ExecutorService pool = Executors.newCachedThreadPool();
            pool = Executors.newFixedThreadPool(6);
	    	for(Entry<String,String> entry:classMap.entrySet()){
	    		pool.execute(new WFTask(req,entry.getKey(),entry.getValue()));
	    	}
	    	
	    	
	    }


}
