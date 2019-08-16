package com.mss.utils;

import java.io.BufferedOutputStream;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * 保存文件到本地
     * @param in
     * @param output
     */
    public static void saveFile(InputStream in, String output) {
    	BufferedOutputStream writeBos = null;
    	int temp = 0;
    	try {
    		writeBos = new BufferedOutputStream(new FileOutputStream(output), 16384);
    		byte[] buf = new byte[16384];
    		while((temp = in.read(buf))!=-1){   
    			writeBos.write(buf, 0, temp);   
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(writeBos);
		}
    }
    
    /** 
     * 获得类的基路径，打成jar包也可以正确获得路径 
     * @return  
     */  
    public static String getBasePath(){  
  
        String filePath = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();  
        if (filePath.endsWith(".jar")){  
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));  
            try {  
                filePath = URLDecoder.decode(filePath, "UTF-8"); //解决路径中有空格%20的问题  
            } catch (UnsupportedEncodingException ex) {  
  
            }  
  
        }  
        File file = new File(filePath);  
        filePath = file.getAbsolutePath();  
        return filePath;  
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
    	StringBuilder result = new StringBuilder();
		try {
			in = new FileInputStream(file);
			reader = new InputStreamReader(in, charset);
			br=new BufferedReader(reader);  
	    	String line = null;  
	    	
	    	while ((line = br.readLine()) != null) {  
	    		result.append(line);
	    	}  
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(br);
		}
		return result.toString();
    	/*InputStream in = null;
    	String result = null;
    	try {
			in = new FileInputStream(file);
			result = inputStreamToString(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(in);
		}
    	return result;*/
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
    	List<String> list = new ArrayList<String>();
		try {
			in = new FileInputStream(file);
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
    public static String inputStreamToString(InputStream in, String charset) throws IOException{ 
    	
    	
    	StringBuffer out = new StringBuffer(); 
    	byte[] b = new byte[4096]; 
    	for(int n;(n = in.read(b)) != -1;)   { 
    		out.append(new String(b,0,n, charset)); 
    	} 
    	return out.toString(); 
    } 
    
    public static String  inputStreamToString(String filePath) {
    	return inputStreamToString(filePath, "GB2312");
    }
    
    public static String  inputStreamToString(String filePath, String charset){   	
    	InputStream in = null;
		String result = null;
		try {
			in = new FileInputStream(filePath);
			result = inputStreamToString(in, charset);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			IOUtils.closeQuietly(in);
		}
		return result;
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
			fw = new FileWriter(filePath,false);//同时创建新文件
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
}
