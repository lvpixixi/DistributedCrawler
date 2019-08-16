package bscrawler.spider.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseAndEnglish {
	
	/**

	   * 是否是英文

	   * @param c

	   * @return

	   *//*

	   public static boolean isEnglish(String charaString){

	      return charaString.trim().matches("^[a-zA-Z]*");

	    }*/
	   
	   public static boolean isChinese(String str){

		      String regEx = "[\\u4e00-\\u9fa5]+";

		      Pattern p = Pattern.compile(regEx);

		      Matcher m = p.matcher(str);

		     if(m.find())

		       return true;

		     else

		       return false;

		   }
	   
	   
	   public static void main(String[] args) {

		  System.out.println(isChinese("机器学习"));
		  System.out.println(isChinese("machine learning"));
		  System.out.println("----------------------------------------------------");
		  //System.out.println(isEnglish("机器学习"));
		  //System.out.println(isEnglish("machine learning"));
	   }

}
