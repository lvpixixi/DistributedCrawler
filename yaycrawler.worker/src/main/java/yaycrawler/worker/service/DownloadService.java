package yaycrawler.worker.service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import bscrawler.spider.listener.IPageParseListener;
import us.codecraft.webmagic.Request;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.utils.FTPUtils;
import yaycrawler.common.utils.HttpUtil;

/**
 * Created by bill on 2017/5/5.
 */
@Service
public class DownloadService {

    //private ExecutorService executorService;

    @Value("${ftp.server.url}")
    private String url;
    @Value("${ftp.server.port}")
    private int port;
    @Value("${ftp.server.username}")
    private String username;
    @Value("${ftp.server.password}")
    private String password;

    @Value("${ftp.server.path}")
    private String ftpPath;

    private FTPUtils ftpUtil;

    @Autowired(required = false)
    private IPageParseListener pageParseListener;
    
    private ExecutorService executorService = Executors.newFixedThreadPool(10); //10个线程跑

    public boolean startCrawlerDownload(List<CrawlerRequest> downList) {
        for (CrawlerRequest request : downList) {
            List<String> srcList = (List<String>) MapUtils.getObject(request.getExtendMap(),"$src");
            if (srcList == null || srcList.isEmpty())
                continue;
            executorService.submit(new Runnable() {
                public void run() {
                	System.out.println("------------------------------------------------------------------------");
                	System.out.println("url="+url+"port="+port+"username="+username+"password="+password);
                    ftpUtil = ftpUtil.getInstance();
                    try {
                        ftpUtil.connect(url, port, username, password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(1);                   
                    List<String> childRequestList = new LinkedList<>();
                    System.out.println(2);
                    String suffix = MapUtils.getString(request.getExtendMap(), "$DOWNLOAD");
                    
                    HttpUtil httpUtil= null;
                    try {
                    	httpUtil = HttpUtil.getInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(3);
                    for (String src:srcList) {
                        try {
                        	System.out.println(src);
                            HttpResponse response = httpUtil.doGet(src, null, null);
                            System.out.println(11);                   
                            if (response.getStatusLine().getStatusCode() != 200) {
                                childRequestList.add(src);
                                continue;
                            }
                            System.out.println(12);                   
                            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                            String documentName = StringUtils.substringAfterLast(src, "/");
                            System.out.println(13);        
                            if (!StringUtils.contains(documentName, ".")) {
                                documentName = documentName + suffix;
                            }
                            File document = new File(ftpPath + "/" + documentName);
                            Files.createParentDirs(document);
                            Files.write(bytes, document);
                            System.out.println(14);     
                            String path = request.getDomain() + "/" + request.getHashCode() + "/";
                            System.out.println("tempFile="+ftpPath + "/" + documentName);
                            System.out.println("path="+path);
                            System.out.println("ftpPath="+ftpPath);
                            System.out.println("documentName="+documentName);
                            //上传文件
                            ftpUtil.upLoadByFtp(ftpPath + "/" + documentName, path, documentName);
                            document.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(childRequestList.size() > 0) {
                        CrawlerRequest crawlerRequest = new CrawlerRequest();
                        crawlerRequest.setDomain(request.getDomain());
                        crawlerRequest.setHashCode(request.getHashCode());
                        crawlerRequest.setMethod("get");
                        crawlerRequest.setUrl(request.getUrl() + "?$download=pdf");
                        crawlerRequest.setExtendMap(ImmutableMap.of("$DOWNLOAD",".pdf","$src",childRequestList));
                        pageParseListener.onSuccess(new Request(request.getUrl()), Lists.newArrayList(crawlerRequest));
                    }
                    try {
                        ftpUtil.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
        return true;
    }
}
