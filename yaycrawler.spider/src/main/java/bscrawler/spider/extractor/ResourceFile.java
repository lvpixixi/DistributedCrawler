package bscrawler.spider.extractor;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;

import bscrawler.spider.util.DateUtil;

public class ResourceFile {
	
	private String ROOTPATH_LABEL ="${RootPath}";

	private String pathSeparator = File.separator;
	//原始Url
	private String originalUrl;
	//下载Url
	private String downUrl;	
	//发布Url
	private String publicUrl;
	//相对路径
	private String relativePath;
	
	private String baseUrl;
	
	//源文件名
	private String fileName;
	//新文件名
	private String newFileName;	
	//文件扩展名
	private String fileExtName;
	//域名
	private String domain;

	//文件扩展名
	private String[] extNames = {"jpg","jpeg","png","gif","pdf","doc","docx","xls","xlsx","ppt"};
	
	
	public ResourceFile(String originalUrl,String downUrl,String domain){
		fileExtName = getFileExtension(originalUrl);		
		newFileName = DigestUtils.md5Hex(downUrl)+"."+fileExtName;
		this.baseUrl = ROOTPATH_LABEL;
		this.downUrl = downUrl;
		this.domain = domain;
		this.originalUrl = originalUrl;
		this.relativePath = domain+pathSeparator+DateUtil.getCurrentDateStr();
		this.publicUrl = baseUrl+pathSeparator+this.relativePath+pathSeparator+newFileName;
		
	}
	
	public ResourceFile(String originalUrl,String downUrl,String domain,String baseUrl){
		fileExtName = getFileExtension(originalUrl);		
		newFileName = DigestUtils.md5Hex(downUrl)+"."+fileExtName;
		this.baseUrl = baseUrl;
		this.downUrl = downUrl;
		this.domain = domain;
		this.originalUrl = originalUrl;
		this.relativePath = domain+pathSeparator+DateUtil.getCurrentDateStr();
		this.publicUrl = baseUrl+pathSeparator+this.relativePath+pathSeparator+newFileName;
		
	}
	
	
	private String getFileExtension(String fullName){
		 String fileName = new File(fullName).getName();
		 int dotIndex = fileName.lastIndexOf('.');
		 if(dotIndex==-1){
			 dotIndex = fileName.lastIndexOf('=');
		 }
		 if(dotIndex > -1){
			 String extName = fileName.substring(dotIndex + 1);	
			 for(String node:extNames){
				 if(node.equalsIgnoreCase(extName)){
					 return node;
				 }
			 }
			 return "jpg";
		 }else{
			 return "jpg";
		 }
		
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public String getFileExtName() {
		return fileExtName;
	}
	public void setFileExtName(String fileExtName) {
		this.fileExtName = fileExtName;
	}

	/**
	 * 获取文件相对路径
	 * @return
	 */
	public String getRelativeUrl(){
		return pathSeparator+relativePath+pathSeparator+newFileName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	
	
	
	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getPublicUrl() {
		return publicUrl;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getROOTPATH_LABEL() {
		return ROOTPATH_LABEL;
	}

	public static void main(String[] args){
		ResourceFile rf = new ResourceFile("https://mmbiz.qpic.cn/mmbiz_jpg/q6ZQ3YDgSRewZxAJuWnjG6A97830JLDx04wFImUCKtsy3VD40YmARa8ftF61Xpr77iafG9WwATEE8ibia19KNRPYQ/640?wx_fmt=jpeg","https://mmbiz.qpic.cn/mmbiz_jpg/q6ZQ3YDgSRewZxAJuWnjG6A97830JLDx04wFImUCKtsy3VD40YmARa8ftF61Xpr77iafG9WwATEE8ibia19KNRPYQ/640?wx_fmt=jpeg","mmbiz.qpic.cn");
		//-319159660.jpeg
		System.out.println(rf.getNewFileName());
	}
	
	
}
