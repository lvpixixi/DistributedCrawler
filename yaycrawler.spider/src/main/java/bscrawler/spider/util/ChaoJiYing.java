package bscrawler.spider.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaoJiYing {
	
	private static Logger logger = LoggerFactory.getLogger(ChaoJiYing.class);
	
	/**
	 * 字符串MD5加密
	 * @param s 原始字符串
	 * @return  加密后字符串
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 通用POST方法
	 * @param url 		请求URL
	 * @param param 	请求参数，如：username=test&password=1
	 * @return			response
	 * @throws IOException
	 */
	public static String httpRequestData(String url, String param)
			throws IOException {
		URL u;
		HttpURLConnection con = null;
		OutputStreamWriter osw;
		StringBuffer buffer = new StringBuffer();

		u = new URL(url);
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
		osw.write(param);
		osw.flush();
		osw.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}

		return buffer.toString();
	}

	/**
	 * 查询题分
	 * @param username	用户名
	 * @param password	密码
	 * @return			response
	 * @throws IOException
	 */
	public static String GetScore(String username, String password) {
		String param = String.format("user=%s&pass=%s", username, password);
		String result;
		try {
			result = ChaoJiYing.httpRequestData(
					"http://code.chaojiying.net/Upload/GetScore.php", param);
		} catch (IOException e) {
			logger.error("GetScore error,",e);
			result = "未知问题";
		}
		return result;
	}
	
	/**
	 * 注册账号
	 * @param username	用户名
	 * @param password	密码
	 * @return			response
	 * @throws IOException
	 */
	public static String UserReg(String username, String password) {
		String param = String.format("user=%s&pass=%s", username, password);
		String result;
		try {
			result = ChaoJiYing.httpRequestData(
					"http://code.chaojiying.net/Upload/UserReg.php", param);
		} catch (IOException e) {
			logger.error("UserReg error,",e);
			result = "未知问题";
		}
		return result;
	}

	/**
	 * 账号充值
	 * @param username	用户名
	 * @param card		卡号
	 * @return			response
	 * @throws IOException
	 */
	public static String UserPay(String username, String card) {

		String param = String.format("user=%s&card=%s", username, card);
		String result;
		try {
			result = ChaoJiYing.httpRequestData(
					"http://code.chaojiying.net/Upload/UserPay.php", param);
		} catch (IOException e) {
			logger.error("UserPay error,",e);
			result = "未知问题";
		}
		return result;
	}
	
	/**
	 * 报错返分
	 * @param username	用户名
	 * @param password	用户密码
	 * @param softId	软件ID
	 * @param id		图片ID
	 * @return			response
	 * @throws IOException
	 */
	public static String ReportError(String username, String password, String softid, String id) {
		
		String param = String
		.format(
				"user=%s&pass=%s&softid=%s&id=%s",
				username, password, softid, id);
		String result;
		try {
			result = ChaoJiYing.httpRequestData(
					"http://code.chaojiying.net/Upload/ReportError.php", param);
		} catch (IOException e) {
			logger.error("ReportError error,",e);
			result = "未知问题";
		}
		
		return result;
	}


	/**
	 * 核心上传函数
	 * @param url 			请求URL
	 * @param param			请求参数，如：username=test&password=1
	 * @param data			图片二进制流
	 * @return				response
	 * @throws IOException
	 */
	public static String httpPostImage(String url, String param,
			byte[] data) throws IOException {
		long time = (new Date()).getTime();
		URL u = null;
		HttpURLConnection con = null;
		String boundary = "----------" + MD5(String.valueOf(time));
		String boundarybytesString = "\r\n--" + boundary + "\r\n";
		StringBuffer buffer = new StringBuffer();
		OutputStream out = null;
		InputStream in = null;
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			//con.setReadTimeout(60000);   
			con.setConnectTimeout(60000);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(true);
			con.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary);
			
			out = con.getOutputStream();
				
			for (String paramValue : param.split("[&]")) {
				out.write(boundarybytesString.getBytes("UTF-8"));
				String paramString = "Content-Disposition: form-data; name=\""
						+ paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
				out.write(paramString.getBytes("UTF-8"));
			}
			out.write(boundarybytesString.getBytes("UTF-8"));

			String paramString = "Content-Disposition: form-data; name=\"userfile\"; filename=\""
					+ "chaojiying_java.gif" + "\"\r\nContent-Type: application/octet-stream\r\n\r\n";
			out.write(paramString.getBytes("UTF-8"));
			
			out.write(data);
			
			String tailer = "\r\n--" + boundary + "--\r\n";
			out.write(tailer.getBytes("UTF-8"));
			out.flush();

			in = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			con.disconnect();
		}
		return buffer.toString();
	}	
	
	/**
	 * 核心上传函数
	 * @param url 			请求URL
	 * @param param			请求参数，如：username=test&password=1
	 * @param data			图片二进制流
	 * @return				response
	 * @throws IOException
	 */
	public static String httpPostImage1(String url, String param,
			byte[] data, String fileName) throws IOException {
		long time = (new Date()).getTime();
		URL u = null;
		HttpURLConnection con = null;
		String boundary = "----------" + MD5(String.valueOf(time));
		String boundarybytesString = "\r\n--" + boundary + "\r\n";
		OutputStream out = null;
		
		u = new URL(url);
		
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		//con.setReadTimeout(60000);   
		con.setConnectTimeout(60000);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(true);
		con.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		
		out = con.getOutputStream();
			
		for (String paramValue : param.split("[&]")) {
			out.write(boundarybytesString.getBytes("UTF-8"));
			//System.out.println(boundarybytesString);
			String paramString = "Content-Disposition: form-data; name=\""
					+ paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
			//System.out.println(paramString);
			out.write(paramString.getBytes("UTF-8"));
		}
		//System.out.println(boundarybytesString);
		out.write(boundarybytesString.getBytes("UTF-8"));

		String paramString = "Content-Disposition: form-data; name=\"file\"; filename=\""
				+ fileName + "\"\r\nContent-Type: image/jpeg\r\n\r\n";
		out.write(paramString.getBytes("UTF-8"));
		//System.out.println(paramString);
		out.write(data);
		
		String tailer = "\r\n--" + boundary + "--\r\n";
		//System.out.println(tailer);
		out.write(tailer.getBytes("UTF-8"));

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}

		return buffer.toString();
	}
	
	/**
	 * 识别图片_按图片文件路径
	 * @param username		用户名
	 * @param password		密码
	 * @param softid		软件ID
	 * @param codetype		图片类型

	 * @param len_min		最小位数
	 * @param time_add		附加时间
	 * @param str_debug		开发者自定义信息
	 * @param filePath		图片文件路径
	 * @return
	 * @throws IOException
	 */
	public static String PostPic(String username, String password,
			String softid, String codetype, String len_min, String time_add, String str_debug,
			String filePath) {
		String result = "";
		String param = String
		.format(
				"user=%s&pass=%s&softid=%s&codetype=%s&len_min=%s&time_add=%s&str_debug=%s",
				username, password, softid, codetype, len_min, time_add, str_debug);
		try {
			File f = new File(filePath);
			if (null != f) {
				int size = (int) f.length();
				byte[] data = new byte[size];
				FileInputStream fis = new FileInputStream(f);
				fis.read(data, 0, size);
				if(null != fis) fis.close();
				
				if (data.length > 0)	result = ChaoJiYing.httpPostImage("http://upload.chaojiying.net/Upload/Processing.php", param, data);
			}
		} catch(Exception e) {
			logger.error("PostPic error,",e);
			result = "未知问题";
		}
		return result;
	}


	
	/**
	 * 识别图片_按图片二进制流
	 * @param username		用户名
	 * @param password		密码
	 * @param softid		软件ID
	 * @param codetype		图片类型

	 * @param len_min		最小位数
	 * @param time_add		附加时间
	 * @param str_debug		开发者自定义信息
	 * @param byteArr		图片二进制数据流
	 * @return
	 * @throws IOException
	 */
	public static String PostPic(String username, String password,
			String softid, String codetype, String len_min, String time_add, String str_debug,
			byte[] byteArr) {
		String result = "";
		String param = String
		.format(
				"user=%s&pass=%s&softid=%s&codetype=%s&len_min=%s&time_add=%s&str_debug=%s",
				username, password, softid, codetype, len_min, time_add, str_debug);
		try {
			result = ChaoJiYing.httpPostImage("http://upload.chaojiying.net/Upload/Processing.php", param, byteArr);
		} catch(Exception e) {
			logger.error("PostPic error,",e);
			result = "未知问题";
		}
		
		
		return result;
	}

	/**
	 * 识别图片_按图片文件路径
	 * @param username		用户名
	 * @param password		密码
	 * @param softid		软件ID
	 * @param codetype		图片类型

	 * @param len_min		最小位数
	 * @param time_add		附加时间
	 * @param str_debug		开发者自定义信息
	 * @param filePath		图片文件路径
	 * @return
	 * @throws IOException
	 */
	public static String PostPic1(String username, String password,
			String softid, String codetype,String time_add, String appKey,String method,
			String filePath, String fileName) {
		String result = "";
		String param = String
		.format(
				"username=%s&password=%s&appid=%s&codetype=%s&timeout=%s&appkey=%s&method=%s",
				username, password, softid, codetype,  time_add, appKey,method);
		try {
			File f = new File(filePath+fileName);
			if (null != f) {
				int size = (int) f.length();
				byte[] data = new byte[size];
				FileInputStream fis = new FileInputStream(f);
				fis.read(data, 0, size);
				if(null != fis) fis.close();
				
				if (data.length > 0)	result = ChaoJiYing.httpPostImage1("http://api.yundama.com/api.php", param, data, fileName);
			}
		} catch(Exception e) {
			logger.error("PostPic1 error,",e);
			result = "未知问题";
		}
		return result;
	}
	
	public static String convert(String utfString){  
	    StringBuilder sb = new StringBuilder();  
	    int i = -1;  
	    int pos = 0;  
	      
	    while((i=utfString.indexOf("\\u", pos)) != -1){  
	        sb.append(utfString.substring(pos, i));  
	        if(i+5 < utfString.length()){  
	            pos = i+6;  
	            sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));  
	        }  
	    }  
	      
	    return sb.toString();  
	}  
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		//System.out.println("\u534f\u5bbd\u5b99\u9053");
		
	/*	File[] files = FileUtils.getFile("/mss/authcode").listFiles();
		for(File file : files) {
			long b = System.currentTimeMillis();
			String yzm = PostPic("wwqlky", "130307", "891249", "1104", "0", "10", "test", file.getAbsolutePath());
			int i=0;
			while(true) {
				i++;
				
				String yzm = PostPic1("moshushi123", "moshushi123", "1", "6300", "10", "22cc5376925e9387a23cf797cb9ba745","upload",
						"E:\\mss\\authcode\\","1474305160769.jpg");
				JSONObject json = JSONObject.fromObject(yzm);
				
				System.out.println(yzm);
				if(!"".equals(json.get("text"))) {
					System.out.println("识别次数:"+i);
					break;
				}
				
			}
			
			while("".equals(yzm)) {
				yzm = PostPic("wwqlky", "130307", "891249", "1104", "0", "10", "test", file.getAbsolutePath());
			}
			System.out.println(yzm);
			long e = System.currentTimeMillis();
			System.out.println("用时:"+(e-b));
		}*/
		
	}
}
