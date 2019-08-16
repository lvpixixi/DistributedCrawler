package io.renren.modules.spider.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.renren.modules.oss.cloud.CloudStorageConfig;
import io.renren.modules.oss.cloud.QiniuCloudStorageService;
import io.renren.modules.spider.dao.NewsDao;
import io.renren.modules.spider.service.IImport2CloudService;
import io.renren.modules.spider.utils.CSVUtils;
import io.renren.modules.spider.utils.ZipCompressor;

@Service(value="import2CloudService")
public class IImport2CloudServiceImpl implements IImport2CloudService {
	
	@Autowired
	private NewsDao newsDao;
	
	/**
	 *  导入数据到云存储
	 *  1)导出数据库数据——例子：
	 *  2)导出图片——文件包
	 *  3)按照日期，生成文件包      20180404.zip
	 *  目录结构
	 *  20180404.zip
	 *     news_cn_20180404.csv
	 *     news_en_20180404.csv
	 *     news_wx_20180404.csv
	 *     domain例：www.baidu.com
	 *        images/**		
	 *        pdf/**			
	 *  4)上传到云上
	 * @param date  如果日期为null，导出全部数据， 如果不为空，导出和参数日期相同的采集日期的数据
	 * @param tables 包括：news_cn,news_en,news_wx
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	@Transactional(rollbackFor=Exception.class, isolation=Isolation.READ_COMMITTED, propagation=Propagation.REQUIRED)
	public void importByDate(Date date, String[] tables) throws FileNotFoundException {
		String timePath = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String targetPath = "D:/temp/" + timePath + "/";
		String zipPath = "D:/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".zip";
		Map<String, Set<String>> extras = new HashMap<>();
		// 不同的表进行分别的处理
		for(int i = 0; i < tables.length; i++) {
			Map<String, Object> params = new HashMap<>();
			// 拼接 SQL
			params.put("crawlerdate", CSVUtils.getTargetDay(date));
			params.put("tbname", tables[i]);
			// 数据库中最新的采集的消息全部都要
			params.put("pubdate", "1970-01-01");
			
			List<Map<String, Object>> dataList = newsDao.getAllBy(params);
			
			// 遍历集合, 获取所有的字段
			List<String> heads = new ArrayList<>();
			for(Map<String, Object> map : dataList) {
				for(Map.Entry<String, Object> entry : map.entrySet()) {
					if(!heads.contains(entry.getKey())) {
						heads.add("\"" + entry.getKey() + "\"");
					}
				}
				break;
			}
			
			// 1. 将数据库的记录转化为 tbname_日期.cvs 文件
			extras = CSVUtils.createCSVFile(heads, dataList, targetPath, tables[i] + "_" + new SimpleDateFormat("yyyy-MM-dd").format(date) + ".csv");
			//Set<String> set = extras.get("domains");
			/*if(extras != null && !extras.isEmpty()) {
				// 2. 导出图片到 /Domain/image/,
				if(extras.containsKey("attchfiles")) {
					Set<String> attachfiles = extras.get("attchfiles");
					for(String attachfile : attachfiles) {
						String fromDirs = "D:/data/webmagic" + attachfile;
						String toDirs = targetPath.subSequence(0, targetPath.length() - 1) + attachfile;
						try {
							CSVUtils.copy(fromDirs, toDirs);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				// 3. 导出 pdf 到   /Domain/XXX.pdf
				if(extras.containsKey("pdffiles")) {
					Set<String> pdffiles = extras.get("pdffiles");
					for(String pdffile : pdffiles) {
						String fromDirs = "D:/data/webmagic" + pdffile;
						String toDirs = targetPath.subSequence(0, targetPath.length() - 1) + pdffile;
						try {
							CSVUtils.copy(fromDirs, toDirs);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}*/
			// 4. 生成文件包  yyyy-MM-dd.zip
			ZipCompressor compressor = new ZipCompressor(zipPath);
			compressor.compress(targetPath);
		}
		
		// 5. 将最后的总内容 打包为 .zip 并上传到 云
		/*QiniuCloudStorageService storageService = new QiniuCloudStorageService(getCloudStorageConfig());
		InputStream in = new FileInputStream(new File(zipPath));
		storageService.upload(in);*/
		
	}
	
	// 返回云存储的配置文件
	private CloudStorageConfig getCloudStorageConfig() {
		CloudStorageConfig config = new CloudStorageConfig();
		config.setType(1);
		config.setQiniuDomain("https://www.qiniu.com/");
		config.setQiniuAccessKey("_LbIjh7by1ob-4GyjUO9bHfhNr0HaFyfqVOc5c3o");
		config.setQiniuSecretKey("7Be6Jz-aX-CTHwXgL8KklQrfasUNbLi9p-hi_3dc");
		config.setQiniuBucketName("best-jh");
		return config;
	}

}
