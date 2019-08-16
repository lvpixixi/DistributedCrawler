package yaycrawler.spider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import bscrawler.spider.util.DateUtil;

public class DateFormatTest {
	
	
	

	public static void main(String[] args) throws IOException, ParseException {
		/*System.out.println(getDateFormat("01-12"));
        System.out.println(getDateFormat("2018-01"));
        System.out.println(getDateFormat("2018-01-12"));
        System.out.println(getDateFormat("11:22"));
        System.out.println(getDateFormat("2018-01-12 11:22:33"));
        System.out.println(getDateFormat("2018-01-12 11:22:33:333"));
        System.out.println(getDateFormat("11时22分33秒333毫秒"));
        System.out.println(getDateFormat("2018/01/12 11时22分33秒333毫秒"));*/
        
        String dataStr = "发布：2019年01-30 04:14";
        System.out.println(DateUtil.strToDate(dataStr));
        
        
	}

}
