package bscrawler.spider.pipeline;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bscrawler.spider.SpiderConstants;
import bscrawler.spider.download.Down2FtpComp;
import bscrawler.spider.extractor.ResourceFile;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;
 
/**
 * @author wangdw 下载资源文件到ftp服务器管道
 * @version V1.0.0
 * @Date 2018/11/28
 */

public class File2FtpPipeline extends FilePersistentBase implements Pipeline {
 
    private Logger logger = LoggerFactory.getLogger(getClass());
  
    public File2FtpPipeline(Down2FtpComp down2FtpManager) {
    	this.down2FtpManager = down2FtpManager;
    }
    public File2FtpPipeline(Down2FtpComp down2FtpManager,String path) {
        setPath(path);
        this.down2FtpManager = down2FtpManager;
    }
    
    
    private Down2FtpComp down2FtpManager;
 
    @Override
    public void process(ResultItems resultItems, Task task) {
        List<ResourceFile> rfs = (List<ResourceFile>)resultItems.get(SpiderConstants.PAGE_RESOURCE_LABEL);
        if(rfs!=null&&rfs.size()>0){
            for (ResourceFile entry : rfs) {
                String extName=entry.getFileExtName();
                String newFileName = entry.getNewFileName();
                StringBuffer fileNameNew = new StringBuffer();
                fileNameNew.append(entry.getRelativePath());
                fileNameNew.append(PATH_SEPERATOR);
                fileNameNew.append(newFileName);
                //这里通过httpclient下载之前抓取到的图片网址，并放在ftp服务器中
                try {
					//HttpFileUtil.getInstance().getFileTo(entry.getDownUrl(), fileNameNew.toString());
                	logger.info("downfile "+entry.getDownUrl()+"  to  "+entry.getRelativePath());
                	
                	
					down2FtpManager.fileDown(entry.getDownUrl(), entry.getRelativeUrl(), entry.getRelativePath(), extName);
				} catch (Exception e) {
					e.printStackTrace();
				}
                
               
            }
        }
      
    }
}