package io.renren.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期处理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月21日 下午12:53:33
 */
public class DateUtils {
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	public static final String DATE_PATTERN_yyyyMMdd = "yyyyMMdd";
	public static final String DATE_PATTERN_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String simpleDateFormat_yyyyMMdd = "yyyyMMdd";
    public static final String simpleDateFormat_yyyy_MM_dd = "yyyy-MM-dd";
    
	   
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }
    /**
     * 将yyyyMMdd格式的字符串日期转化为日期格式
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate,String dateFormate) {
    	Date date = null;
    	try {
    		date = new SimpleDateFormat(dateFormate).parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return date;
    }
}
