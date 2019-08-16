package bscrawler.spider.download;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yaycrawler.common.utils.FTPUtils;

/**
 * @author wangdw 带线程池的文件下载到FTP，线程大小10 文件数少的情况下，体现不大
 * @version V1.0.0
 * @Date 2018/11/28
 */
public class Down2FtpComp {

	private static final Logger logger = LoggerFactory.getLogger(Down2FtpComp.class);
	private ExecutorService executorService = Executors.newFixedThreadPool(10); // 10个线程跑

	private String ftpUrl;
	private int port;
	private String username;
	private String password;
	private String ftpPath;
	public Down2FtpComp(IFtpConfig config) {
		this.ftpUrl = config.getFtpUrl();
		this.port =config.getPort();
		this.username = config.getUsername();
		this.password = config.getPassword();
		this.ftpPath = config.getFtpPath();
	}

	private FTPUtils ftpUtil = FTPUtils.getInstance();

	// 下载文件到 ftp 服务
	public Boolean fileDown(final String netURL, final String src, final String path, final String suffix)
			throws ExecutionException, InterruptedException {
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
					// 设置请求头, 模拟浏览器进行访问
					connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
					connection.setReadTimeout(30000);
					is = connection.getInputStream();
					getData = FileUtil.readAsByteArray(is);
					ftpUtil.connect(ftpUrl, port, username, password);
					String documentName = StringUtils.substringAfterLast(src, File.separator);
					if (!StringUtils.contains(documentName, ".")) {
						documentName = documentName + suffix;
					}
					ftpUtil.upLoadByFtp(getData, path, documentName);

				} catch (IOException e) {
					logger.error("从URL获得字节流数组失败 :" + netURL);
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						logger.error("从URL获得字节流数组流释放失败 " + ExceptionUtils.getMessage(e));
					}
					try {
						ftpUtil.disconnect(); // 断开连接
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			}
		});
		return future.get();
	}

}