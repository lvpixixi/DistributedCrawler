package com.mss.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {
	public static int getPercent(float fixedSize ,float width)
	{
		return Math.round(fixedSize/width*100);
	}
	public static double getPercentDouble(float fixedSize ,float width)
	{
		return Math.round(fixedSize/width*100)* 0.01d;
	}
	/**
     * 对图片进行放大
     * @param originalImage 原始图片
     * @param times 放大倍数
     * @return
     */
    public static BufferedImage  zoomToBufferedImage(BufferedImage  originalImage, double rate){
        int width = (int) (originalImage.getWidth()*rate);
        int height = (int) (originalImage.getHeight()*rate);
        BufferedImage newImage = new BufferedImage(width,height,originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0,0,width,height,null);
        g.dispose();
        return newImage;
    }
    public static byte[]  zoomToBytes(BufferedImage  originalImage, double rate,String formatName){
    	BufferedImage outimg = zoomToBufferedImage(originalImage, rate);
    	ByteArrayOutputStream outByteArrayOutputStream= new ByteArrayOutputStream();
	    try {
			ImageIO.write(outimg,formatName,outByteArrayOutputStream);
			ImageIO.write(outimg,formatName,new File("d:/tt.jpg"));
			return outByteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return new byte[0];
    }
	public static void main(String[] args) {
		
	}
}
