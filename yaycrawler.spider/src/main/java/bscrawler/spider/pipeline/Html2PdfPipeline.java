package bscrawler.spider.pipeline;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.pdf.HtmlToPdf;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

public class Html2PdfPipeline extends FilePersistentBase implements Pipeline{
	
	  private Logger logger = LoggerFactory.getLogger(getClass());
	    public static final String JSON_LABEL = "json";
	    public Html2PdfPipeline(){
	        setPath("D:/data/webmagic/");
	    }
	 
	    public Html2PdfPipeline(String path) {
	        setPath(path);
	    }
	    
	    @Override
	    public void process(ResultItems resultItems, Task task) {
	    	String fileStorePath = "";
	        String rawText = resultItems.get("rawText");
	        rawText = StringUtils.replace(rawText, "data-src", "src");
	        List<ResourceFile> rfs = (List<ResourceFile>)resultItems.get("resources");
	        if(rfs!=null){
		    /*    for(ResourceFile rf:rfs){
		        	rawText = StringUtils.replace(rawText, rf.getDownUrl(), rf.getRealPath());
		        }*/
		        List<String> objectNames = resultItems.get("objectNames");
		        String url =  resultItems.get("url");
		        if(rawText!=null&&objectNames!=null&&objectNames.size()>0){
		        	Map<String,Object> obj = (Map<String,Object>)resultItems.get(objectNames.get(0));
		        	fileStorePath = this.path.replace("\\", "") + task.getSite().getDomain() + "/";
		        	String filename = fileStorePath + obj.get("id") + ".html";
		        	System.out.println(filename);
		        	writeToFile(rawText,obj,filename);
		        	if(!StringUtils.isEmpty(filename)){
		        		HtmlToPdf.convert(filename, fileStorePath+obj.get("id")+".pdf");
		        	}
		        }
	        }
	    }
	    
	    private void writeToFile(String rawText,Map<String,Object> obj,String filename){
	    	try {
	            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(filename)));
	            printWriter.write(rawText);
	            printWriter.close();
	        } catch (IOException e) {
	            logger.warn("write file error", e);
	        }
	    }

}
