package bscrawler.spider.util.excel;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bscrawler.spider.util.DateUtil;
 
/**
 * 追加excel 父类
 * @author liyangyang
 *
 * @param <T>
 */
public  class ChatExcel {
	
	public XSSFWorkbook wb;
	
	public Sheet sheet;
	
	public Map<String,Integer> keyColumnMapping;
	
	private FileInputStream fileInput=null;
	
	//private FileChannel fileChannel=null;
	
	//private FileLock fileLock=null;
	
	/**
	 * 路径是否是文件
	 */
	public boolean isFile=false;
	
	/**
	 * 当前行号
	 */
	public int rownum=0;
	
	//private int headnum=0;
	
	/**
	 * 样式列表
	 */
	private Map<String, CellStyle> styles;
	
	private String filePath;
	
	//检测文件大小。
	private File checkFilePath(String filePath) {
		try {
			File file=new File(filePath);
			if(!file.exists()){
				return file;
			}else{
				long fileSize=file.length();
				if(fileSize > 1328427){//1.26MB左右，大概4-5万行左右
				   String fileName=DateUtils.formatDate(new Date(), "yyyyMMdd")+"-"+file.getName();
				   File path=new File(file.getParent());
				   FileUtils.copyFile(file, new File(path,fileName));
				   file.delete();
				}
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	public ChatExcel(String filePath,List<String> columnNames) {
		File file=checkFilePath(filePath);
		try {
			
			if(file.exists() && file.isFile()){//文件存在//追加
				isFile=true;
				fileInput= new FileInputStream(file);
//				fileChannel=new RandomAccessFile(file, "rw").getChannel();
				wb=new XSSFWorkbook(fileInput);
				sheet=wb.getSheet("message");
//				fileLock=fileChannel.lock();
			}else{//文件不存在 // 新建
				isFile=false;
				wb=new XSSFWorkbook();
				sheet=wb.createSheet("message");
			}
			this.filePath=filePath;
			//this.headnum=columnNames.size();
			this.keyColumnMapping = new HashMap<String,Integer>();
			for (int i = 0; i < columnNames.size(); i++) {
				keyColumnMapping.put(columnNames.get(i), i);
			}
			addHeader(columnNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取最后一个数据行号
	 * @return
	 */
	public int getLastDataRowNum(){
		return this.sheet.getLastRowNum();
	}
	
	public void setData(Row row,Map<String,Object> data){
		int colunm = 0;
		for (Map.Entry<String,Object> entry:data.entrySet()){
			colunm = keyColumnMapping.get(entry.getKey());
			this.addCell(row, colunm, entry.getValue(), 2, String.class);
		}
	}
	
	/*public void setListData(int rownum,List<Map<String,Object>> data){
		for(Map<String,Object> m:data){
		   Row row= sheet.createRow(rownum);
		   setData(row, m);
		}
	}
	*/
	
	
	/**
	 * 添加一个单元格
	 * @param row 添加的行
	 * @param column 添加列号
	 * @param val 添加值
	 * @param align 对齐方式（1：靠左；2：居中；3：靠右）
	 * @return 单元格对象
	 */
	private Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType){
		Cell cell = row.createCell(column);
		String cellFormatString = "@";
		try {
			if(val == null){
				cell.setCellValue("");
			}else if(fieldType != Class.class){
				cell.setCellValue((String)fieldType.getMethod("setValue", Object.class).invoke(null, val));
			}else{
				if(val instanceof String) {
					cell.setCellValue((String) val);
				}else if(val instanceof Integer) {
					cell.setCellValue((Integer) val);
					cellFormatString = "0";
				}else if(val instanceof Long) {
					cell.setCellValue((Long) val);
					cellFormatString = "0";
				}else if(val instanceof Double) {
					cell.setCellValue((Double) val);
					cellFormatString = "0.00";
				}else if(val instanceof Float) {
					cell.setCellValue((Float) val);
					cellFormatString = "0.00";
				}else if(val instanceof Date) {
					cell.setCellValue((Date) val);
					cellFormatString = "yyyy-MM-dd HH:mm";
				}else {
					cell.setCellValue((String)Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(), 
						"fieldtype."+val.getClass().getSimpleName()+"Type")).getMethod("setValue", Object.class).invoke(null, val));
				}
			}
			if (val != null){
				CellStyle style = styles.get("data_column_"+column);
				if (style == null){
					style = wb.createCellStyle();
					style.cloneStyleFrom(styles.get("data"+(align>=1&&align<=3?align:"")));
			        style.setDataFormat(wb.createDataFormat().getFormat(cellFormatString));
					styles.put("data_column_" + column, style);
				}
				cell.setCellStyle(style);
			}
		} catch (Exception ex) {
			cell.setCellValue(val.toString());
		}
		return cell;
	}
	
	private void addHeader(List<String> headerList){
		this.styles = createStyles(wb);
		if(!isFile){//新建 header
			Row headerRow = sheet.createRow(rownum);
			headerRow.setHeightInPoints(16);
			for (int i = 0; i < headerList.size(); i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellStyle(styles.get("header"));
				String[] ss = StringUtils.split(headerList.get(i), "**", 2);
				if (ss.length==2){
					cell.setCellValue(ss[0]);
					Comment comment = this.sheet.createDrawingPatriarch().createCellComment(
							new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
					comment.setString(new XSSFRichTextString(ss[1]));
					cell.setCellComment(comment);
				}else{
					cell.setCellValue(headerList.get(i));
				}
				sheet.autoSizeColumn(i);
			}
			for (int i = 0; i < headerList.size(); i++) {  
				int colWidth = sheet.getColumnWidth(i)*2;
		        sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);  
			}
		}
	}
	
	/**
	 * 创建表格样式
	 * @param wb 工作薄对象
	 * @return 样式列表
	 */
	private Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		
		CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		Font titleFont = wb.createFont();
		titleFont.setFontName("Arial");
		titleFont.setFontHeightInPoints((short) 16);
		titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setFont(titleFont);
		styles.put("title", style);
 
		style = wb.createCellStyle();
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		Font dataFont = wb.createFont();
		dataFont.setFontName("Arial");
		dataFont.setFontHeightInPoints((short) 10);
		style.setFont(dataFont);
		styles.put("data", style);
		
		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_LEFT);
		styles.put("data1", style);
 
		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_CENTER);
		styles.put("data2", style);
 
		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		styles.put("data3", style);
		
		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
//		style.setWrapText(true);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font headerFont = wb.createFont();
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(headerFont);
		styles.put("header", style);
		
		return styles;
	}
	
	
	public void  flush(){
		try {
			if(fileInput!=null){
				fileInput.close();
			}
			/*if(fileLock!=null){
				fileLock.release();
			}*/
			/*if(fileChannel!=null){
				fileChannel.close();
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		write();
	}
	
	public void write(){
		try {
			FileOutputStream outputStream=new FileOutputStream(new File(filePath));
			wb.write(outputStream);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*public void appenList(List<Map<String,Object>> listdata) {
		if(isFile){
		   int listData=getLastDataRowNum();
		   setListData(listData, listdata);
		}else{
		   setListData(rownum, listdata);
		}
		export();
	}*/
	
	public void append(Map<String,Object> data) {
		if(isFile){
		   int listData=getLastDataRowNum();
		   Row row= sheet.createRow(++listData);
		   setData(row, data);
		}else{
		   rownum++;
		   Row row=sheet.createRow(rownum);
		   setData(row, data);
		}
		//export();
	}
	
	public static void main(String[] args) {
		List<Map<String,Object>> list=new ArrayList<>();
		Map<String,Object> m=new HashMap<>();
		m.put("pwd", DateUtil.getCurrentDateStr1());
		m.put("name", "wangdw");
		list.add(m);
		list.add(m);
		List<String> header = new ArrayList<>();
		header.add("name");
		header.add("pwd");
		ChatExcel e=new ChatExcel("D:\\msg.xlsx",header);
		//e.appenList(list);
		//e.appenList(list);
		e.append(m);
		e.append(m);
		e.append(m);
		e.flush();
	}
	
}

