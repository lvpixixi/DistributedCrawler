package io.renren.modules.spider.utils;

import com.mss.word.HtmlToWordByPOI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 import java.io.File;
 import java.io.UnsupportedEncodingException;
 import java.net.URLEncoder;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 /**
 * 打包的工具类
 * @author wgshbase
 * @version 2019/03/22
 *
 */
public class UnpackUtils {

	private static Logger LOG = LoggerFactory.getLogger(UnpackUtils.class);

	private static String HJSFileStorePath = "D:/data/HJS/";
	
	private static String HTFileStorePath = "D:/data/HT/";
	
	/**
	 * 按照项目进行数据的打包, 最后返回打包的文件的名称以及对应的打包文件的路径, 用于插入对应的数据表, 已做记录...
	 * @param project
	 * @param maps
	 * @return
	 */
	public static Map<String, String> unpack(String project, List<Map> maps) {
		Map<String, String> result = new HashMap<>();
		switch (project) {
		case "HJS":
			result = unpackHJS(maps);
			break;
		case "HT":
			result = unpackHT(maps);
			break;
		default:
			result = defaultUnpack(maps);
			break;
		}
		return result;
	}

	/**
	 * 默认的打包程序...
	 * @param maps
	 * @return
	 */
	private static Map<String, String> defaultUnpack(List<Map> maps) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 航天系统部的打包
	 * @param maps
	 * @return
	 */
	private static Map<String, String> unpackHT(List<Map> maps) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 环境所的打包
	 * @param maps
	 * @return
	 */
	private static Map<String, String> unpackHJS(List<Map> maps) {
		Map<String, String> result = new HashMap<>();
		HtmlToWordByPOI poi = new HtmlToWordByPOI();
		String fileStorepath = HJSFileStorePath + "backup" + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator;
		
		// 拷贝文件之前先将文件内容清空...
		delAllFile(fileStorepath);
		
		poi.setRootPath(fileStorepath);
		for(Map map : maps) {
			String title = (String) map.get("title");
			if(StringUtils.isEmpty(title)) {
				LOG.error("The news you wanna download has no title...");
				System.out.println("The news you wanna download has no title...");
				continue;
			}
			if(title.contains(" ")) {
				try {
					title = URLEncoder.encode(title, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			try {
				poi.toWord(getTargetHtml(map), fileStorepath + title + ".doc");
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		String zipname = HJSFileStorePath + "2019HTDT(" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ").zip";
		// 指定压缩文件名称, 压缩文件...
		compressFile(zipname, fileStorepath);
		
		result.put("filestorePath", fileStorepath);
		result.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		result.put("zipname", zipname.subSequence(zipname.indexOf(HJSFileStorePath) + HJSFileStorePath.length(), zipname.length()).toString());
		result.put("zipPath", HJSFileStorePath);
			
		return result;
	}
	
	/*** 
     * 删除指定文件夹下所有文件 
     *  
     * @param path 文件夹完整绝对路径 
     * @return 
     */  
    public static  boolean delAllFile(String path) {  
        boolean flag = false;  
        File file = new File(path);  
        if (!file.exists()) {  
            return flag;  
        }  
        if (!file.isDirectory()) {  
            return flag;  
        }  
        String[] tempList = file.list();  
        File temp = null;  
        for (int i = 0; i < tempList.length; i++) {  
            if (path.endsWith(File.separator)) {  
                temp = new File(path + tempList[i]);  
            } else {  
                temp = new File(path + File.separator + tempList[i]);  
            }  
            if (temp.isFile()) {  
                temp.delete();  
            }  
            if (temp.isDirectory()) {  
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件  
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹  
                flag = true;  
            }  
        }  
        return flag;  
    }  
      
    /*** 
     * 删除文件夹 
     *  
     * @param folderPath文件夹完整绝对路径 
     */  
    public  static void delFolder(String folderPath) {  
        try {  
            delAllFile(folderPath); // 删除完里面所有内容  
            String filePath = folderPath;  
            filePath = filePath.toString();  
            java.io.File myFilePath = new java.io.File(filePath);  
            myFilePath.delete(); // 删除空文件夹  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  

	/**
	 * 将指定的文件夹下面的文件压缩到指定的压缩文件名当中
	 * @param zipname
	 * @param fileStorepath
	 */
	private static void compressFile(String zipname, String fileStorepath) {
		ZipCompressor zc = new ZipCompressor(zipname);
		zc.compress(fileStorepath);
	}

	// 解析 map 集合的目标对象到 目标字符串
	public static String getTargetHtml(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		
		// 发布时间与原文链接, 来源网站
		String pubdate = new SimpleDateFormat("yyyy-MM-dd").format(map.get("pubDate"));
		String url = map.get("crawler_url");
		String sitedomain = map.get("crawler_site"); 
		sb.append("<h4>发布时间:  " + pubdate.trim() + "</h4>").append("<h4>来源网站: " + sitedomain.trim() + "</h4>").append("<h4>原文链接:  " + url.trim() + "</h4>");
		
		//翻译的内容
		if(map.containsKey("title_tr")) {
			// 添加翻译的部分
			String titleTr = map.get("title_tr");
			String contentTr = map.get("content_tr");
			if(StringUtils.isEmpty(titleTr)) {
				titleTr = "";
			}
			if(StringUtils.isEmpty(contentTr)) {
				contentTr = "";
			}
			sb.append("<h4>译文标题: " + titleTr.trim() + "</h4>").append(contentTr);
		}
		// 原文的标题和正文
		String title = map.get("title");
		String content = map.get("content");
		sb.append("<h4>" + title.trim() + "</h4>").append(content);
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		UnpackUtils.delAllFile("D:\\data\\HJS\\backup\\2019-03-23");
	}

	/**
	 * 抽取新闻的关键字, 实体, 摘要
	 * @param map
	 * @return 
	 */
	public static Map extract(Map map) {
		if(null == map || map.isEmpty()) {
			return map;
		}
		Map<String, Object> extras = new HashMap<>();
		HANLPExtractor extractor = new HANLPExtractor();
		String keywords;
		String summary;
		String namedEntity;
		if(map.containsKey("title_tr")) {
			keywords = CSVUtils.parseList2String(extractor.extractKeywords(CSVUtils.getContentWithoutHtmlTag((String) map.get("content_tr"))));
			summary = CSVUtils.parseList2String(extractor.extractSummary(CSVUtils.getContentWithoutHtmlTag((String) map.get("content_tr"))));
			namedEntity = CSVUtils.parseMap2String(extractor.extractNamedEntity((CSVUtils.getContentWithoutHtmlTag((String) map.get("content_tr")))));
		} else {
			keywords = CSVUtils.parseList2String(extractor.extractKeywords(CSVUtils.getContentWithoutHtmlTag((String) map.get("content"))));
			summary = CSVUtils.parseList2String(extractor.extractSummary(CSVUtils.getContentWithoutHtmlTag((String) map.get("content"))));
			namedEntity = CSVUtils.parseMap2String(extractor.extractNamedEntity((CSVUtils.getContentWithoutHtmlTag((String) map.get("content")))));
		}
		extras.put("nlpKeywords", keywords);
		extras.put("nlpSummary", summary);
		extras.put("nlpNamedEntity", namedEntity);
		map.putAll(extras);
		return map;
	}
	
}
