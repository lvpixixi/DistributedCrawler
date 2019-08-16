package bscrawler.spider.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.UrlUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 文件操作工具类
 * @author wwq
 *
 */
public class FileUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	public static String PATH_SEPERATOR = "/";

    static {
        String property = System.getProperties().getProperty("file.separator");
        if (property != null) {
            PATH_SEPERATOR = property;
        }
    }
    
    /**
     * 写入文件
     * @param value
     * @param printFileName
     * @param isAppend
     */
    public static void print(String value, String printFileName, boolean isAppend) {
    	print(value, getFile(printFileName), isAppend);
    }
    
    /**
     * 写入文件
     * @param value
     * @param printFile
     * @param isAppend
     */
    public static void print(String value, File printFile, boolean isAppend) {
    	PrintWriter writer = null;
    	FileWriter fileWriter = null;
    	try {
    		fileWriter = new FileWriter(printFile, isAppend);
    		writer = new PrintWriter(fileWriter, true);
    		writer.println(value);
    		writer.flush();
		} catch (IOException e) {
			logger.error("print "+printFile.getName()+" error,", e);
		} finally {
			IOUtils.closeQuietly(fileWriter);
			IOUtils.closeQuietly(writer);
		}
    }
    
    /**
     * 写入文件
     * @param jsonArr
     * @param printFileName
     * @param isAppend
     */
    public static void printList(List<Object> list, String printFileName, boolean isAppend) {
    	PrintWriter writer = null;
    	FileWriter fileWriter = null;
    	try {
    		fileWriter = new FileWriter(getFile(printFileName), isAppend);
    		writer = new PrintWriter(fileWriter, true);
    		for(int i=0; i<list.size(); i++) {
    			writer.println(list.get(i));
    		}
    		writer.flush();
		} catch (IOException e) {
			logger.error("print "+printFileName+" error,", e);
		} finally {
			IOUtils.closeQuietly(fileWriter);
			IOUtils.closeQuietly(writer);
		}
    }
    
    public static String fileToString(File file, String charset) {
    	InputStream in = null;
    	BufferedReader br=null;
    	Reader reader = null;
    	String result = "";
		try {
			in = new FileInputStream(file);
			reader = new InputStreamReader(in, charset);
			br=new BufferedReader(reader);  
	    	String line = null;  
	    	
	    	while ((line = br.readLine()) != null) {  
	    		result += line;  
	    	}  
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(br);
		}
		return result;
    }
    
    public static String fileToString(String filePath) {
    	return fileToString(filePath, "GB2312");
    }
    
    public static String fileToString(String filePath, String charset) {
    	File file = getFile(filePath);
    	if(!file.exists()) {
    		return null;
    	}
    	return fileToString(file, charset);
    }
    
    public static String fileToString(File file) {
    	return fileToString(file, "GB2312");
    }
    
    public static void main(String[] args) {
    	InputStream in = null;
    	BufferedReader br=null;
    	Reader reader = null;
    	List<String> list = new ArrayList<String>();
    	JSONObject resultJson = new JSONObject();
		try {
			
			in = new FileInputStream(getFile("e:/mss/crawler/airbusdefenceandspace.com/crawlerFail.txt"));
			reader = new InputStreamReader(in, "UTF-8");
			br=new BufferedReader(new InputStreamReader(in,"UTF-8"));  
	    	String line = null;  
	    	HashSet<String> sets = new HashSet<String>();
	    	while ((line = br.readLine()) != null) {  
	    		if(line != null) {
	    			Request req = JSON.parseObject(line, Request.class);
	    			if("﻿蓝色起源火箭".equals(req.getExtra("searchText")) || "阿丽亚娜火箭".equals(req.getExtra("searchText"))) {
	    				if("200".equals(req.getExtra("statusCode")+"")) {
	    					/*JSONObject json = new JSONObject();
	    					json.put("url", req.getUrl());
	    					json.put("searchText", req.getExtra("searchText"));*/
	    					String domain = UrlUtils.getDomain(req.getUrl());
	    					String md5Url = DigestUtils.md5Hex(req.getUrl());
	    					if(!sets.contains(md5Url)) {
	    						print(req.getUrl(), "e:/mss/data/data1/" + domain+"_url.txt", true);
	    						//print(req.getUrl(), "e:/mss/data/"+req.getExtra("searchText")+"_url.txt", true);
	    						sets.add(md5Url);
	    					}
	    					/*if(resultJson.get(domain) == null) {
	    						JSONArray jsonArr = new JSONArray();
		    					jsonArr.add(json);
		    					resultJson.put(domain, jsonArr);
	    					} else {
	    						resultJson.getJSONArray(domain).add(json);
	    					}*/
	    				}
	    			}
	    		}
	    	} 
	    	print(new String(resultJson.toJSONString().getBytes(), "UTF-8"), "e:/mss/data/url1.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(br);
		}
	}
    
    /**
     * 读取本地文件
     * @param fileName
     * @return
     */
    public static Object readFile(String fileName) {
    	BufferedReader fileCursorReader = null;
        try {
        	fileCursorReader = new BufferedReader(new FileReader(fileName));
            String line;
            Object result=null;
            //read the last number
            while ((line = fileCursorReader.readLine()) != null) {
            	result = line;
            }
            return result;
        } catch (IOException e) {
            //init
            logger.error("read file error " + fileName,e);
            return null;
        }finally {
            IOUtils.closeQuietly(fileCursorReader);
        }
    }
    
    public static List<String> fileToList(File file) {
    	InputStream in = null;
    	BufferedReader br=null;
    	Reader reader = null;
    	List<String> list = new ArrayList<String>();
		try {
			in = new FileInputStream(file);
			reader = new InputStreamReader(in, "UTF-8");
			br=new BufferedReader(new InputStreamReader(in,"UTF-8"));  
	    	String line = null;  
	    	while ((line = br.readLine()) != null) {  
	    		if(line != null) {
	    			list.add(line);
	    		}
	    	}  
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(br);
		}
		return list;
    }
    
    public static byte[] File2byte(String filePath) {  
        byte[] buffer = null;  
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {  
            File file = new File(filePath);  
            fis = new FileInputStream(file);  
            bos = new ByteArrayOutputStream();  
            byte[] b = new byte[2048];  
            int n;  
            while ((n = fis.read(b)) != -1)  {  
                bos.write(b, 0, n);  
            }  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {
        	IOUtils.closeQuietly(bos);
        	IOUtils.closeQuietly(fis);
        }
        return buffer;  
    }
    
    /**
     * inputstream转String
     * @param in
     * @return
     * @throws IOException
     */
    public static String inputStreamToString(InputStream in) throws IOException{ 
    	
    	
    	StringBuffer out = new StringBuffer(); 
    	byte[] b = new byte[4096]; 
    	for(int n;(n = in.read(b)) != -1;)   { 
    		out.append(new String(b,0,n,"gbk")); 
    	} 
    	return out.toString(); 
    } 
    
    public static File getFile(String fullName) {
        checkAndMakeParentDirecotry(fullName);
        return new File(fullName);
    }

    public static void checkAndMakeParentDirecotry(String fullName) {
        int index = fullName.lastIndexOf(PATH_SEPERATOR);
        if (index > 0) {
            String path = fullName.substring(0, index);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }
    
	/**
	 * 获取文件扩展名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtend(String filename) {
		return getExtend(filename, "");
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtend(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > 0) && (i < (filename.length() - 1))) {
				return (filename.substring(i+1)).toLowerCase();
			}
		}
		return defExt.toLowerCase();
	}

	/**
	 * 获取文件名称[不含后缀名]
	 * 
	 * @param
	 * @return String
	 */
	public static String getFilePrefix(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, splitIndex).replaceAll("\\s*", "");
	}
	
	/**
	 * 获取文件名称[不含后缀名]
	 * 不去掉文件目录的空格
	 * @param
	 * @return String
	 */
	public static String getFilePrefix2(String fileName) {
		int splitIndex = fileName.lastIndexOf(".");
		return fileName.substring(0, splitIndex);
	}
	
	/**
	 * 文件复制
	 *方法摘要：这里一句话描述方法的用途
	 *@param
	 *@return void
	 */
	public static void copyFile(String inputFile,String outputFile) throws FileNotFoundException{
		File sFile = new File(inputFile);
		File tFile = new File(outputFile);
		FileInputStream fis = new FileInputStream(sFile);
		FileOutputStream fos = new FileOutputStream(tFile);
		int temp = 0;  
		byte[] buf = new byte[10240];
        try {  
        	while((temp = fis.read(buf))!=-1){   
        		fos.write(buf, 0, temp);   
            }   
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally{
            try {
            	fis.close();
            	fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } 
	}
	
	/**
	 * 清理文件中的内容
	 * @param files
	 */
	public static void clearFiles(File ... files) {
		FileWriter fwUrl = null;
		for(File file : files) {
			try {
				fwUrl = new FileWriter(file);
				fwUrl.write("");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(fwUrl);
			}
		}
	}
	
	/**
	 * 文件复制
	 *方法摘要：这里一句话描述方法的用途
	 *@param
	 *@return void
	 */
	public static void copyFile(File sFile,File tFile) throws FileNotFoundException{

		FileInputStream fis = new FileInputStream(sFile);
		FileOutputStream fos = new FileOutputStream(tFile);
		int temp = 0;  
		byte[] buf = new byte[10240];
        try {  
        	while((temp = fis.read(buf))!=-1){   
        		fos.write(buf, 0, temp);   
            }   
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally{
            try {
            	fis.close();
            	fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } 
	}
	/**
	 * 判断文件是否为图片<br>
	 * <br>
	 * 
	 * @param filename
	 *            文件名<br>
	 *            判断具体文件类型<br>
	 * @return 检查后的结果<br>
	 * @throws Exception
	 */
	public static boolean isPicture(String extendName) {
		if(StringUtils.isBlank(extendName)) {
			return false;
		}
		
		// 声明图片后缀名数组
		String imgeArray[][] = { { "bmp", "0" }, { "dib", "1" },
				{ "gif", "2" }, { "jfif", "3" }, { "jpe", "4" },
				{ "jpeg", "5" }, { "jpg", "6" }, { "png", "7" },
				{ "tif", "8" }, { "tiff", "9" }, { "ico", "10" },{ "webp", "11" } };
		// 遍历名称数组
		for (int i = 0; i < imgeArray.length; i++) {
			// 判断单个类型文件的场合
			if (imgeArray[i][0].equals(extendName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将字符串追加到已存在的文本中
	 * @param result
	 * @param filePath
	 */
	public static void writeFile(String result, String filePath){
		boolean append =false;
		FileWriter fw = null;
		BufferedWriter bf = null;
		File file = getFile(filePath);
		try{
			if(file.exists()) {
				append =true;
			}
			fw = new FileWriter(filePath,append);//同时创建新文件
			//创建字符输出流对象
			bf = new BufferedWriter(fw);
			//创建缓冲字符输出流对象
			bf.append(result);
			bf.flush();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fw);
			IOUtils.closeQuietly(bf);
		}
	}
	
	/**
	 * 判断文件是否为DWG<br>
	 * <br>
	 * 
	 * @param filename
	 *            文件名<br>
	 *            判断具体文件类型<br>
	 * @return 检查后的结果<br>
	 * @throws Exception
	 */
	/*public static boolean isDwg(String filename) {
		// 文件名称为空的场合
		if (oConvertUtils.isEmpty(filename)) {
			// 返回不和合法
			return false;
		}
		// 获得文件后缀名
		String tmpName = getExtend(filename);
		// 声明图片后缀名数组
		if (tmpName.equals("dwg")) {
			return true;
		}
		return false;
	}*/
	
	/**
	 * 删除指定的文件
	 * 
	 * @param strFileName
	 *            指定绝对路径的文件名
	 * @return 如果删除成功true否则false
	 */
	public static boolean delete(String strFileName) {
		File fileDelete = getFile(strFileName);

		if (!fileDelete.exists() || !fileDelete.isFile()) {
			return false;
		}
		return fileDelete.delete();
	}
	
	public static List<File> getFileList1(String filepath,String suffix) throws FileNotFoundException, IOException {
		 List<File> files = new ArrayList<File>();
       try {

               File file = new File(filepath);
               if (!file.isDirectory()) {
                   	
          	 	 if(file.getPath().endsWith(suffix)){
          	 		 files.add(file);
          	 	 }

               } else if (file.isDirectory()) {
                      
                       String[] filelist = file.list();
                       for (int i = 0; i < filelist.length; i++) {
                               File readfile = new File(filepath + "\\" + filelist[i]);
                               if (!readfile.isDirectory()) {
                                       if(readfile.getPath().endsWith(suffix)){
                                      	 files.add(readfile);
                              	 	 }

                               } else if (readfile.isDirectory()) {
                                       List<File> childs = getFileList1(filepath + "\\" + filelist[i],suffix);
                                       files.addAll(childs);
                               }
                       }

               }

       } catch (FileNotFoundException e) {
               System.out.println("readfile()   Exception:" + e.getMessage());
       }
       return files;
	}
}
