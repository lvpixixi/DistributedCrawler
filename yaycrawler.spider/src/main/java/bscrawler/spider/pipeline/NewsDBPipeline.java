package bscrawler.spider.pipeline;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import bscrawler.spider.db.utils.DbBuilder;
import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.util.HtmlFormatter;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;
 
 
/**
 * 图片文件下载
 * @author wangdw
 *
 */

public class NewsDBPipeline extends FilePersistentBase implements Pipeline {
	
    private Logger logger = LoggerFactory.getLogger(getClass());
          
    public NewsDBPipeline() {
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
    
        List<String> objectNames = resultItems.get("objectNames");
        List<ResourceFile> rfs = (List<ResourceFile>)resultItems.get("resources");
        
        if(objectNames!=null){
	        for(String objectName:objectNames){
	        	Object object = resultItems.get(objectName);
	        	// 返回的对象是一个 List 的集合
	        	if(object instanceof ArrayList) {
	        		ArrayList<Map<String, Object>> objs = (ArrayList) object;
	        		for (Map obj : objs) {
	        			if(obj!=null){
	    	        		obj.put("url", resultItems.getRequest().getUrl());
	    	        		obj.put("crawlerdate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	    	        		obj.put("siteDomain", resultItems.get("domain"));
	    	        		// 加入額外的信息
	    	        		obj.put("news_category", resultItems.get("news_category"));
	    	        		obj.put("dbType", resultItems.get("dbType"));
	    	        		if(!StringUtils.isEmpty(resultItems.get("src"))) {
	    	        			obj.put("src", resultItems.get("src"));
	    	        		}
	    	        		// 此处的文件的保存路径直接修改为对应的 域名为 前缀
	    	        		String fileStorePath = PATH_SEPERATOR + obj.get("siteDomain") + PATH_SEPERATOR;
	    	                String attchfiles = getAttchfiles(rfs,fileStorePath);
	    	        		
	    	        		if(!StringUtils.isEmpty(attchfiles)){
	    	        			obj.put("attchfiles", attchfiles);
	    	        		}
	    	        		
	    	        		if(!StringUtils.isEmpty(obj.get("title"))) {
	    	        			writeToDB(objectName,obj);
	    	        		}
	    	        		
	    	        	}
					}
	        	} else {
	        		// 返回的是一个单一的对象
	        		Map<String,Object> obj = (Map<String,Object>)object;
		        	if(obj!=null){
		        		obj.put("url", resultItems.getRequest().getUrl());
		        		obj.put("crawlerdate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		        		obj.put("siteDomain", resultItems.get("domain"));
		        		// 加入額外的信息
    	        		obj.put("news_category", resultItems.get("news_category"));
    	        		obj.put("dbType", resultItems.get("dbType"));
    	        		/**
    	        		 * 通过的domain的判断，确定dsti网站的处理方式
    	        		 * by mcg
    	        		 */
    	        		//
    	        		if(obj.get("siteDomain").equals("www.dsti.net")){
		        			String content = dealDsti((String)obj.get("content"));
		        			obj.put("content", content);
    	        		}
    	        		if(!StringUtils.isEmpty(resultItems.get("src"))) {
    	        			obj.put("src", resultItems.get("src"));
    	        		}
    	        		if(!StringUtils.isEmpty(resultItems.get("searchText"))) {
    	        			obj.put("searchText", resultItems.get("searchText"));
    	        		}
		        		// 此处的文件的保存路径直接修改为对应的 域名为 前缀
		        		String fileStorePath = PATH_SEPERATOR + obj.get("siteDomain") + PATH_SEPERATOR;
		                String attchfiles = getAttchfiles(rfs,fileStorePath);
		        		
		        		if(!StringUtils.isEmpty(attchfiles)){
		        			obj.put("attchfiles", attchfiles);
		        		}
		        		
		        		// 在对应的 WeixinModelProcessor 中进行添加一个标志前缀!!用于数据库的  pdffiles 路径字段生成
		        		if(!StringUtils.isEmpty(obj.get("pdffiles"))) {
		        			obj.put("pdffiles", (String)(obj.get("pdffiles")) + obj.get("id") + ".pdf");
		        		}
		        		// 对于个别的 没有 title 的新闻, 不执行插入数据库的操作
		        		if(obj.containsKey("publicName")) {
		        			// 微信模块, 对于部分没有正文的对象, 不执行插入数据库操作
		        			if(!StringUtils.isEmpty(obj.get("title")) && !StringUtils.isEmpty(obj.get("publicName")) && !StringUtils.isEmpty(obj.get("content"))) {
		        				String content = (String)obj.get("content");
    	        				obj.put("formattedContent", HtmlFormatter.formatContent(content));
			        			writeToDB(objectName,obj);
			        		}
		        		} else if(!StringUtils.isEmpty(obj.get("title")) && !obj.containsKey("thesaurusEn")) {
		        				String content = (String)obj.get("content");
		        				obj.put("formattedContent", HtmlFormatter.formatContent(content));
		        				writeToDB(objectName,obj);
		        		} else if(obj.containsKey("thesaurusEn")) {
		        			writeToDB(objectName,obj);
		        		}
		        		
		        	}
	        	}
	        	
	        }
        }
    }
    
    private String getAttchfiles(List<ResourceFile> rfs,String fileStorePath){
    	StringBuilder  attchfiles = new StringBuilder();
    	if(rfs!=null){
    		for ( ResourceFile entry : rfs) {
    			 String newFileName = entry.getNewFileName();
                 StringBuffer imgFileNameNew = new StringBuffer();
                 imgFileNameNew.append(entry.getROOTPATH_LABEL());
                 imgFileNameNew.append(fileStorePath.replace("\\", "/"));
                 imgFileNameNew.append(entry.getRelativePath());
                 imgFileNameNew.append(newFileName);	            
                 attchfiles.append(imgFileNameNew).append(",");
            }
    		
    		/*
            for ( ResourceFile entry : rfs) {
                String extName=entry.getFileExtName();
                String newFileName = entry.getNewFileName();
                StringBuffer imgFileNameNew = new StringBuffer(fileStorePath);
                imgFileNameNew.append(entry.getRelativePath());
                imgFileNameNew.append(PATH_SEPERATOR);
                imgFileNameNew.append(newFileName);	            
                attchfiles.append(imgFileNameNew).append(",");
            }
            */
            if(attchfiles.length() > 1) {
            	return attchfiles.substring(0,attchfiles.length()-1);
            }
            
         }
    	return null;
    }
    
    private void insertDB(String sql,Object[] objs){
    	Connection conn = DbBuilder.getConnection();
		DbBuilder.beginTransaction(conn);
		DbBuilder.save(conn,sql, objs);
		DbBuilder.commit(conn);
		DbBuilder.close(conn);
    }
    
    public void writeToDB(String table,Map<String,Object> obj){
        
    	//构建insert语句
    	StringBuilder insert = new StringBuilder(" insert into ");
        insert.append(table).append("(");       
        StringBuilder values = new StringBuilder(" VALUES(");
        List list = new ArrayList();
        //拼写语句和循环设置参数
        for(Entry<String,Object> entry:obj.entrySet()){
        	if(entry.getValue()!=null){
	        	insert.append(entry.getKey().toLowerCase()).append(",");
	        	values.append("?").append(",");
	        	list.add(dealNews(entry.getValue()+""));
        	}
        	
        }
        
        StringBuilder sql =  new StringBuilder();
        sql.append(insert.substring(0, insert.length()-1)).append(")");        
        sql.append(values.substring(0, values.length()-1)).append(")");     
        insertDB(sql.toString(),list.toArray());
        
    }
    
    public static String dealNews(String text){
    	String newText = text;
    	// 去除前后空格
    	newText = newText.trim();
    	newText = StringUtils.replace(newText, "\n", "");
    	newText = StringUtils.replace(newText, "images/", "/images/");
    	//newText = StringUtils.replace(newText, "${RootPath}", "");
    	newText = StringUtils.replace(newText, "data-src=", "src=");
    	newText = StringUtils.replace(newText, "cover//", "cover/");
    	newText = StringUtils.replace(newText, "image//", "image/");
    	// 去除中文网站 p 标签后面的空格(中文空格), 其中的 \u3000 代表的是 中文空格
    	newText = newText.replaceAll("<p>(\u3000)+", "<p>");
    	newText = newText.replaceAll("<p>(&nbsp;)+", "<p>");
    	// 去除标题中的 【】
    	newText = newText.replaceAll("\\【(.*)\\】", "");
    	// replaceAll("(<a\\s[^>]+>)", "").replaceAll("<a>", "").replaceAll("</a>", "");
    	newText = newText.replaceAll("(<a\\s[^>]+>)", "");
    	newText = newText.replaceAll("<a>", "");
    	newText = newText.replaceAll("</a>", "");
    	newText = newText.replaceAll("<strong>", "");
    	newText = newText.replaceAll("</strong>", "");
    	return newText;
    }
    
    /**
     * 处理国防科技信息网正文
     * by mcg
     * @param 待处理的文本
     * @return 处理好的文本
     */
    //处理国防科技信息网正文
    public String dealDsti(String content){
    	String newText = content ;
    	newText = newText.replaceAll("<div.*?>", "<p>")
				.replace("</div>", "</p>")
				.replace("<br>", "</p><p>")
				.replace("<p></p>", "")
				.replace(" ", "")
				.replace("&nbsp;", "")
				.replace("<br>", "")
				.replace("<p></p>", "")
				.replace("�鳎�", ")");
    	return newText;
    }
    
    

}