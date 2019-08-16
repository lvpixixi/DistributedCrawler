package com.mss.word;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.mss.utils.ImageUtil;

public class HtmlToWordByPOI {
	
	private static String nginxRootPath = "http://localhost";
	private static String nginxFilsStorePath = "D:/DevInstall/nginx-1.14.0/root";
	
	CustomXWPFDocument document;
//	XWPFDocument document;
	String rootPath ;
	String pline="" ;//当前读取内容
	public void setRootPath(String rootPath){
		this.rootPath=rootPath;
	}
	public void toWord(String html,String outPath){
		//创建一个word文档
//		document = new XWPFDocument();
		document =new CustomXWPFDocument();
		FileOutputStream outputStream;
		File file = new File(outPath);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			outputStream = new FileOutputStream(outPath);
			org.jsoup.nodes.Document  htmlRoot = Jsoup.parse(html);
			Elements  es = htmlRoot.children();
			parseNodes(es);
			if(StringUtils.isNotBlank(pline)){
				addContext(pline);
			}
			document.write(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	public void parseNodes(Elements  es){
		if(null==es||es.size()==0){
			return;
		}
		for(  org.jsoup.nodes.Element e  :es){
			String tag= e.tagName();
			if(tag.equalsIgnoreCase("img")&&e.hasAttr("src")){
				String imagPath = e.attr("src");
				if(imagPath.startsWith("${RootPath}")) {
					imagPath = imagPath.replace("${RootPath}", rootPath);
				}
				try {
					addImg(imagPath);
					addContext(pline);
					pline="";
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else{
				if(tag.equalsIgnoreCase("p")||tag.equalsIgnoreCase("div")){
					addContext(pline);
					pline="";
					addContext(e.ownText(), false);
				}else if(tag.startsWith("h")){
					addContext(pline);
					pline="";
					addContext(e.ownText(), true);
				}else{
					pline+=e.ownText();
				}
				if(e.childNodeSize()>0){
					parseNodes(e.children());
				}
			}
			
		}
	}
	public void addImg(String imgPath) throws Exception{
		float fixedSize=500;
		String  filetype= imgPath.substring(imgPath.lastIndexOf(".")+1);
		int pictype = getPictureType(filetype);
		
		// 添加判断, 映射 nginx 地址到本地的图片
		if(!org.springframework.util.StringUtils.isEmpty(imgPath) && imgPath.startsWith(nginxRootPath)) {
			imgPath = imgPath.replace(nginxRootPath, nginxFilsStorePath);
		}
		
		File  ff= new File(imgPath);
		BufferedImage bufImg= null;
		try {
			bufImg = ImageIO.read(ff);
		} catch (Exception e) {
			System.out.println(imgPath + " <------------------------- cannot download...");
		}
		if(bufImg != null) {
			double rate  = ImageUtil.getPercentDouble(fixedSize,bufImg.getWidth());
			String picId=document.addPictureData(ImageUtil.zoomToBytes(bufImg, rate,filetype),pictype);
			int pid = Integer.valueOf(picId.replaceAll("[^0-9]+", ""));
			
			document.createPicture( document.createParagraph(),picId, pid,(int)(bufImg.getWidth()*rate),(int)(bufImg.getHeight()*rate),"");  
		}
	}
	//
	public void addContext(String boldLine){
		addContext(boldLine,false);
	} 
	public void addContext(String text,boolean isBold) {
		if(StringUtils.isBlank(text)){
			return ;
		}
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.setText(text);
		run.setBold(isBold);
//		run.setColor("fff000");
	} 
	  /**
     * 根据图片类型，取得对应的图片类型代码
     */
    private static int getPictureType(String picType) {
        int res = XWPFDocument.PICTURE_TYPE_PICT;
        if (picType != null) {
            if (picType.equalsIgnoreCase("png")) {
                res = XWPFDocument.PICTURE_TYPE_PNG;
            } else if (picType.equalsIgnoreCase("dib")) {
                res = XWPFDocument.PICTURE_TYPE_DIB;
            } else if (picType.equalsIgnoreCase("emf")) {
                res = XWPFDocument.PICTURE_TYPE_EMF;
            } else if (picType.equalsIgnoreCase("jpg") || picType.equalsIgnoreCase("jpeg")) {
                res = XWPFDocument.PICTURE_TYPE_JPEG;
            } else if (picType.equalsIgnoreCase("wmf")) {
                res = XWPFDocument.PICTURE_TYPE_WMF;
            }
        }
        return res;
    }
}
