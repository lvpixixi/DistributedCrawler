package io.renren.modules.spider.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(CSVUtils.class);
	
	/**
	 * 导出数据 dataList 到 指定的文件 file 中
	 * 
	 * @param file
	 *            csv文件(路径+文件名)，csv文件不存在会自动创建, 保存的对应的数据库中的记录
	 * @param dataList
	 *            数据
	 * @return
	 */
	public static Map<String, Set<String>> createCSVFile(List<String> heads, List<Map<String, Object>> dataList, String outPutPath,
			String filename) {
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		Map<String, Set<String>> extra = new HashMap<>();
		Set<String> attachfiles = new HashSet<>();
		Set<String> pdffiles = new HashSet<>();
		try {
			if(!new File(outPutPath).exists()) {
				new File(outPutPath).mkdirs();
			}
			out = new FileOutputStream(new File(outPutPath + File.separator + filename));
			// 输出字符流按照 指定的 编码格式进行编码
			osw = new OutputStreamWriter(out, "UTF-8");
			bw = new BufferedWriter(osw);

			// 写入首行
			for (String key : heads) {
				bw.append(key).append(",");
			}
			bw.append("\r\n");

			
			// 写入 content
			if (dataList != null && !dataList.isEmpty()) {
				for (Map<String, Object> entities : dataList) {
					// 对于部分正文部分内容极少或没有的数据, 不予处理
					String entityContent = (String) entities.get("content");
					//entityContent = entityContent.replaceAll("<(.*?)>", "").trim();
					if(entityContent == null || entityContent.replaceAll("<(.*?)>", "").trim().length() < 50) {
						continue;
					}
							
					// 1. 获取所有的数据
					for (Map.Entry<String, Object> entity : entities.entrySet()) {
						
						
						// 处理附件名, 即处理图片
						if ("attchfiles".equals(entity.getKey()) && !StringUtils.isEmpty(entity.getValue())) {
							addAttachFiles(entity.getKey(), attachfiles, extra, (String)entity.getValue());
						}
						// 处理 pdf 附件
						if("pdffiles".equals(entity.getKey()) && entity.getValue() != null) {
							addAttachFiles(entity.getKey(), pdffiles, extra, (String)entity.getValue());
						}
						
						// 处理日期
						if (entity.getValue() instanceof Date) {
							String entityDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getValue());
							if (entityDate == null) {
								entityDate = "\"\"";
							} else {
								entityDate = dealEntityVal(entityDate);
							}
							bw.append(entityDate).append(",");
						} else {
							// 常规处理
							String entityVal = (String) entity.getValue();
							if (entityVal == null || entityVal == "") {
								entityVal = "\"\"";
							} else {
								entityVal = dealEntityVal(entityVal);
							}
							bw.append(entityVal).append(",");
						}
					}
					bw.append("\r\n");
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
					bw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
					osw = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
					out = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return extra;
		
	}

	private static Map<String, Set<String>> addAttachFiles(String key, Set<String> list, Map<String, Set<String>> extra, String attachfiles) {
		// 使用 , 进行分割附件
		String[] split = attachfiles.split(",");
		for(String str : split) {
			list.add(str.substring(str.indexOf("/"), str.length()));
		}
		extra.put(key, list);
		
		return extra;
	}
	
	/**
	 * 按照 csv 文件的规则进行内部数据的调整
	 * 
	 * @param entityVal
	 * @return
	 */
	private static String dealEntityVal(String entityVal) {
		entityVal = StringUtils.replace(entityVal, "\"", "\"\"");
		entityVal = StringUtils.replace(entityVal, entityVal, "\"" + entityVal + "\"");
		entityVal = entityVal.replaceAll("\\n", "");
		entityVal = entityVal.replaceAll("\\r", "");
		entityVal = entityVal.replaceAll("\\t", "");
		return entityVal;
	}

	public static String getTargetDay(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
	// 获取前一天的日期, 由于外文网与中国有时差
	public static String getPrevDay(Date crawlerdate) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(crawlerdate); 
		
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day-1); 

		String prevDay=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
		return prevDay; 
	}

	/**
	 * 复制目标路径下的所有文件到目标路径下
	 * 
	 * @param path
	 *            数据的来源路径, 即你想拷贝哪个路径下面的文件
	 * @param copyPath
	 *            目标路径, 即你想把数据拷贝到什么地方
	 * @throws IOException
	 */
	public static void copy(String path, String copyPath) throws IOException {
		File file = new File(copyPath);
		File rootFile = file.getParentFile();
		if(!rootFile.exists()) {
			rootFile.mkdirs();
			file.createNewFile();
		}
		File filePath = new File(path);
		DataInputStream read;
		DataOutputStream write;
		if (filePath.isDirectory()) {
			File[] list = filePath.listFiles();
			for (int i = 0; i < list.length; i++) {
				String newPath = path + File.separator + list[i].getName();
				String newCopyPath = copyPath + File.separator + list[i].getName();
				File newFile = new File(copyPath);
				if (!newFile.exists()) {
					newFile.mkdir();
				}
				copy(newPath, newCopyPath);
			}
		} else if (filePath.isFile()) {
			read = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
			write = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			byte[] buf = new byte[1024 * 512];
			while (read.read(buf) != -1) {
				write.write(buf);
			}
			read.close();
			write.close();
		} else {
			LOG.error("请输入正确的文件名或路径名 ----------------> " + copyPath);
		}
	}
	
	/**
	 * 获取新闻的正文文字部分
	 */
	public static String getContentWithoutHtmlTag(String html) {
		if(StringUtils.isEmpty(html)) {
			return "";
		}
		return html.replaceAll("<.*?>", "");
	}
	
	/**
	 * List 集合转化为 字符串
	 */
	public static String parseList2String(List<String> list) {
		if(null == list || list.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(String str : list) {
			sb.append(str).append(",");
		}
		return sb.toString().subSequence(0, sb.toString().length() - 1).toString();
	}

	/**
	 * map 集合转化为 字符串
	 * @param extractNamedEntity
	 * @return
	 */
	public static String parseMap2String(Map<String, Set<String>> map) {
		if(null == map || map.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if(map.containsKey("nr")) {
			sb.append("人物:").append(parseList2String(new ArrayList<>(map.get("nr")))).append("<br>");
		}
		if(map.containsKey("ns")) {
			sb.append("地点:").append(parseList2String(new ArrayList<>(map.get("ns")))).append("<br>");
		}
		if(map.containsKey("nt")) {
			sb.append("机构名:").append(parseList2String(new ArrayList<>(map.get("nt")))).append("<br>");
		}
		return sb.toString().subSequence(0, sb.lastIndexOf("<br>")).toString();
	}

}
