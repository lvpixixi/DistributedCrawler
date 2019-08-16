package bscrawler.spider.pipeline;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bscrawler.spider.extractor.ResourceFile;
import bscrawler.spider.util.HttpFileUtil;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;
 
 
/**
 * 文件存储处理管道
 * @author wangdw
 *
 */

public class File2LocalPipeline extends FilePersistentBase implements Pipeline {
 
    private Logger logger = LoggerFactory.getLogger(getClass());
    
  
    public File2LocalPipeline() {
        setPath("D:/data/webmagic/");
    }
 
    public File2LocalPipeline(String path) {
        setPath(path);
    }
 
    @Override
    public void process(ResultItems resultItems, Task task) {
    	String fileStorePath = this.path + PATH_SEPERATOR + task.getSite().getDomain() + PATH_SEPERATOR;
        List<ResourceFile> rfs = (List<ResourceFile>)resultItems.get("resources");
        if(rfs!=null){
            for (ResourceFile entry : rfs) {
                String extName=entry.getFileExtName();
                String newFileName = entry.getNewFileName();
                StringBuffer fileNameNew = new StringBuffer(fileStorePath);
                fileNameNew.append(entry.getRelativePath());
                fileNameNew.append(PATH_SEPERATOR);
                fileNameNew.append(newFileName);	            
                //这里通过httpclient下载之前抓取到的图片网址，并放在对应的文件中
                try {
					HttpFileUtil.getInstance().getFileTo(entry.getDownUrl(), fileNameNew.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
                
               
            }
        }
      
    }
}