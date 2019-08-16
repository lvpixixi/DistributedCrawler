package yaycrawler.worker.download;

import java.io.File;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nick 带线程池的文件下载类，线程大小10
 *      文件数少的情况下，体现不大
 * @version V1.0.0
 * @Date 2017/8/2 20:43
 */
public class FileDownConnManager {

    private static final Logger logger = LoggerFactory.getLogger(FileDownConnManager.class);

    private static final FileDownConnManager connManager = new FileDownConnManager();

    private static ExecutorService executorService = Executors.newFixedThreadPool(10); //10个线程跑

    public static FileDownConnManager getDefaultManager() {
        return connManager;
    }

    public static byte[] fileDown(final String netURL) throws ExecutionException, InterruptedException {
        Future<byte[]> future = executorService.submit(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                Date date = new Date();
                URL url;
                byte[] getData = new byte[0];
                InputStream is = null;
                try {
                    url = new URL(netURL);
                    URLConnection connection = url.openConnection();
                    is = connection.getInputStream();
                    //getData = readInputStream(is);      
                    getData = FileUtil.readAsByteArray(is);
                } catch (IOException e) {
                    logger.error("从URL获得字节流数组失败 " + ExceptionUtils.getMessage(e));
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.error("从URL获得字节流数组流释放失败 " + ExceptionUtils.getMessage(e));
                    }
                }
                return getData;
            }
        });
        return future.get();
    }
    
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < 15; i++) {
            byte[] data = FileDownConnManager.fileDown("http://mpge.5nd.com/2016/2016-11-15/74847/1.mp3");
            FileUtils.writeByteArrayToFile(new File("D:\\test2_"+i+".mp3"), data);
        }
        System.out.println(System.currentTimeMillis() - startTime);

    }


}