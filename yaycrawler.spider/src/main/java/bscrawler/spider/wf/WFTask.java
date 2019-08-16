package bscrawler.spider.wf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bscrawler.spider.db.utils.DbBuilder;
import bscrawler.spider.util.HtmlFormatter;
import bscrawler.spider.util.SSLClient;  
/* 
 * 利用HttpClient进行post请求的工具类 
 */  
public class WFTask implements Runnable{  
	private SSLClient httpClient;
	private String reqUrl;
	private String keyWord;
	private String classId;
	private int pages = 100;
	public WFTask(String reqUrl,String classId,String keyWord) throws Exception{
		this.reqUrl = reqUrl;
		this.classId = classId;
		this.keyWord = keyWord;
		this.httpClient = new SSLClient();
	}
	
    public String doPost(String url,Map<String,String> map,String charset){  
        //HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
           // httpClient = new SSLClient();  
            httpPost = new HttpPost(url);  
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  
            HttpResponse response = httpClient.execute(httpPost);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return result;  
    }  
    
    
    private List<Map<String,Object>> parseResult(String classId,String keyword,String language,int pageNum,String text){
    	 List<Map<String,Object>> list = new ArrayList<>();
    	 JSONObject jobj = JSONObject.parseObject(text);
    	 JSONArray jarray = jobj.getJSONArray("pageRow");
    	 for(int i=0;i<jarray.size();i++){
    		 JSONObject node = (JSONObject)jarray.get(i);
    		 Map<String,Object> newNode = new HashMap<>();
    		 	newNode.put("abstract", node.get("trans_abstract"));
    		 	
    		 	String title = HtmlFormatter.html2text(node.get("title")+"", HtmlFormatter.regEx_html);
    		 	String trans_title = HtmlFormatter.html2text(node.get("trans_title")+"", HtmlFormatter.regEx_html);
    		 	String summary = HtmlFormatter.html2text(node.get("summary")+"", HtmlFormatter.regEx_html);
    		 	
    		 	newNode.put("summary", summary);
    		 	newNode.put("title", title);
    		 	newNode.put("trans_title", trans_title);
    		 	newNode.put("classId", classId);
    		 	newNode.put("pagenum", pageNum);
    		 	newNode.put("language", language);
    		 	newNode.put("keyword", keyword);
    		 list.add(newNode);
    	 }
    	 return list;
    }
    
    private void insertDB(String sql,List<Object[]> list){
    	Connection conn = DbBuilder.getConnection();
		DbBuilder.beginTransaction(conn);
		for(Object[] objs:list){
			DbBuilder.save(conn,sql, objs);
		}
		DbBuilder.commit(conn);
		DbBuilder.close(conn);
    }
    
    public void writeToDB(String table,Map<String,Object> obj){
        
    	//构建insert语句
    	StringBuilder insert = new StringBuilder(" insert into ");
        insert.append(table).append("(");       
        StringBuilder values = new StringBuilder(" VALUES(");
        //拼写SQL
        for(Entry<String,Object> entry:obj.entrySet()){
        	if(entry.getValue()!=null){
	        	insert.append(entry.getKey().toLowerCase()).append(",");
	        	values.append("?").append(",");
        	}
        	
        }
        //构造值矩阵
        List<Object[]> valueList = new ArrayList<>();
        
        
        StringBuilder sql =  new StringBuilder();
        sql.append(insert.substring(0, insert.length()-1)).append(")");        
        sql.append(values.substring(0, values.length()-1)).append(")");     
        insertDB(sql.toString(),valueList);
        
    }
    
    public void writeToDB(String table,List<Map<String,Object>> list){
        
    	//构建insert语句
    	StringBuilder insert = new StringBuilder(" insert into ");
        insert.append(table).append("(");       
        StringBuilder values = new StringBuilder(" VALUES(");
        
        Map<String,Object> one = list.get(0);
        Set<String> keys = one.keySet();
        //拼写语句和循环设置参数
        for(String key:keys){
	        	insert.append(key.toLowerCase()).append(",");
	        	values.append("?").append(",");
        }
        StringBuilder sql =  new StringBuilder();
        sql.append(insert.substring(0, insert.length()-1)).append(")");        
        sql.append(values.substring(0, values.length()-1)).append(")");   
        
        //构造值列表
        List<Object[]> valueList = new ArrayList<>();
        for(Map<String,Object> map:list){
        	Object[] params = new Object[keys.size()];
        	int index=0;
        	for(String key:keys){
        		params[index++] = map.get(key);
        	}
        	valueList.add(params);
        }
        
        insertDB(sql.toString(),valueList);
        
    }
    
    
    public void getWFByPages(String reqUrl,String classId,String keyWord,String language,int pages){
        Map<String,String> createMap = new HashMap<String,String>();  
        createMap.put("paramStrs","主题:(\""+keyWord+"\")*$language:"+language);  
        createMap.put("startDate","1900");  
        createMap.put("endDate","2018");  
        createMap.put("classType", "perio-perio_artical,degree-degree_artical,conference-conf_artical,patent-patent_element,techResult-tech_result,tech-tech_report"); 
        //createMap.put("pageNum","0");  
        createMap.put("isSearchSecond","true");  
        
        
        for(int i=1;i<=pages;i++){
        	createMap.put("pageNum",i+"");
        	String httpOrgCreateTestRtn = this.doPost(reqUrl,createMap,"UTF-8"); 
        	List<Map<String,Object>> list = this.parseResult(classId,keyWord,language,i,httpOrgCreateTestRtn);
        	if(list.size()==0){
        		break;
        	}else{
        		this.writeToDB("gx_train_new", list);
        	}
        }
        
        
        
       
    	
    	
    }
    


	@Override
	public void run() {
		
		getWFByPages(this.reqUrl,this.classId,this.keyWord, "chi", pages);
    	getWFByPages(this.reqUrl,this.classId,this.keyWord, "eng", pages);
	}  

} 