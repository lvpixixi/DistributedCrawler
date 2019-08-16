package io.renren.modules.spider.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;

import io.renren.common.utils.PageUtils;
import io.renren.modules.oss.cloud.OSSFactory;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.spider.dao.UnpackRecordDao;
import io.renren.modules.spider.entity.UnpackRecord;
import io.renren.modules.spider.service.IUnpackRecordService;

@Service("unpackRecordService")
public class UnpackRecordServiceImpl implements IUnpackRecordService {

	@Autowired
	private UnpackRecordDao unpackRecordDao;
	
	@Autowired
	private SysOssService sysOssService;
	
	/**
	 * 查询所有的符合要求的打包记录
	 */
	@Override
	public PageUtils searchByQuery(Map<String, Object> params) {
		
		int pageNumber = Integer.parseInt(params.get("page").toString());
    	int pageSize = Integer.parseInt(params.get("limit").toString());
    	int offset = (pageNumber-1)*pageSize;
    	params.put("offset", offset);
    	params.put("limit", pageSize);
    	List searchByQuery = unpackRecordDao.getContent(params);
    	long count = unpackRecordDao.getCount(params);
    	PageUtils pageUtil = new PageUtils(searchByQuery, (int)count, pageSize, pageNumber);
    	return pageUtil;
	}

	@Override
	public void send(String jsonParams) throws Exception {
		Map<String, Object> params = (Map<String, Object>) JSON.parse(jsonParams);
		String id = (String) params.get("id");
		UnpackRecord record = unpackRecordDao.getOneById(id);
		
		// 1. 获取压缩包
		File localZip = new File(record.getZipPath() + record.getZipname());
		
		FileItem fileItem = new DiskFileItem("copyfile.txt", Files.probeContentType(localZip.toPath()),false,localZip.getName(),(int)localZip.length(),localZip.getParentFile());
        byte[] buffer = new byte[4096];
        int n;
        try (InputStream inputStream = new FileInputStream(localZip); OutputStream os = fileItem.getOutputStream()){
           while ( (n = inputStream.read(buffer,0,4096)) != -1){
               os.write(buffer,0,n);
           }
            //也可以用IOUtils.copy(inputStream,os);
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            //上传文件
            String url = OSSFactory.build().upload(multipartFile.getBytes());
            
            //保存文件信息
            SysOssEntity ossEntity = new SysOssEntity();
            ossEntity.setUrl(url);
            ossEntity.setCreateDate(new Date());
            sysOssService.save(ossEntity);
        }catch (IOException e){
            e.printStackTrace();
        }

		
	}


}
