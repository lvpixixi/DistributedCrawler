package yaycrawler.worker.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import yaycrawler.common.utils.FTPUtils;

/**
 * @author wangdw 带线程池的文件下载到FTP，线程大小10
 *      文件数少的情况下，体现不大
 * @version V1.0.0
 * @Date 2018/11/28
 */
@Component
public class Down2FtpManager {

    private static final Logger logger = LoggerFactory.getLogger(Down2FtpManager.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(10); //10个线程跑
    
    @Value("${ftp.server.url}")
    private String ftpUrl;
    @Value("${ftp.server.port}")
    private int port;
    @Value("${ftp.server.username}")
    private String username;
    @Value("${ftp.server.password}")
    private String password;
    @Value("${ftp.server.path}")
    private String ftpPath;
    
    private FTPUtils ftpUtil = FTPUtils.getInstance();

    public Boolean fileDown(final String netURL,final String src,final String path,final String suffix) throws ExecutionException, InterruptedException {
        Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Date date = new Date();
                URL url;
                byte[] getData = new byte[0];
                InputStream is = null;
                try {
                    url = new URL(netURL);
                    URLConnection connection = url.openConnection();
                    is = connection.getInputStream();
                    getData = FileUtil.readAsByteArray(is);                    
                    ftpUtil.connect(ftpUrl, port, username, password);
                    String documentName = StringUtils.substringAfterLast(src, "/");
                    if (!StringUtils.contains(documentName, ".")) {
                        documentName = documentName + suffix;
                    }
                    ftpUtil.upLoadByFtp(getData, path, documentName);
                    
                } catch (IOException e) {
                    logger.error("从URL获得字节流数组失败 " + ExceptionUtils.getMessage(e));
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.error("从URL获得字节流数组流释放失败 " + ExceptionUtils.getMessage(e));
                    }
                }
                return true;
            }
        });
        return future.get();
    }
}