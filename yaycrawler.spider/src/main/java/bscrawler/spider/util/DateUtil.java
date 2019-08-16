package bscrawler.spider.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @package :com.base.util
 * @memo :
 * @fileName:DateUtil.java @class :DateUtil
 * @write :huangjb
 * @date :2005-10-19
 */
public class DateUtil{
    public static final String simpleDateFormat_yyyyMMdd = "yyyyMMdd";
    public static final String simpleDateFormat_yyyy_MM_dd = "yyyy-MM-dd";
    

    /**
     * 定义日期时间格式的中间符号，可以是"-"或"/"或":"。日期默认为"-"时间默认为":"
     */
    private static final String formatDateSign    = "-";

    private static final String formatDandTSign   = "/";

    private static final String formatTimeSign    = ":";

    private static final String simpleDateFormat  = "yyyy" + formatDateSign
                                                          + "MM"
                                                          + formatDateSign
                                                          + "dd";
    private static final String simpleDateFormat1 = "yyyyMMdd";
    
    private static final String simpleTimeFormat  = simpleDateFormat + " HH"
                                                          + formatTimeSign
                                                          + "mm"
                                                          + formatTimeSign
                                                          + "ss";

    static SimpleDateFormat     chineseDateFormat = new SimpleDateFormat(
                                                          "yyyy-MM-dd");
    
public static String regEx_date_style0 = "(\\d{4})-(\\d{1,2})-(\\d{1,2}) (\\d{2}):(\\d{2})";
	
	public static String regEx_date_style1 = "(\\d{4})-(\\d{1,2})-(\\d{1,2})";
	
	public static String regEx_date_style2 = "(\\d{4})年(\\d{1,2})月(\\d{1,2}日)";
	
	public static String regEx_date_style3 = "[A-Za-z]+ (\\d{1,2}), (\\d{4})";
	
	public static String regEx_date_style4 = "(\\d{1,2}) [A-Za-z]+ (\\d{4})";
	
	public static String regEx_date_style5 = "(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})";
	
	public static String regEx_date_style6 = "(\\d{4})/(\\d{1,2})/(\\d{1,2})";
	
	public static String regEx_date_style7 = "(\\d{1,2})/(\\d{1,2})/(\\d{4})";
	
	public static String regEx_date_style8 = "(\\d{4})年(\\d{1,2})月(\\d{1,2})日 (\\d{2}):(\\d{2})";

	public static String regEx_date_style9 = "[A-Za-z]+\\. (\\d{1,2}), (\\d{4})";
	
	public static String regEx_date_style10 = "([A-Za-z]+) (\\d{1,2})[A-Za-z]+ (\\d{4})";

	/**
	 * 此工具方法是为了格式化 pubDate 字段
	 * 依据不同的情况将爬取到的  pubDate 字段进行格式化, 便于数据库的操作
	 * 
	 * @param pubDate
	 * @return
	 * @throws ParseException 
	 * 
	 * @author wgshb
	 */
	public static Date convertPubDate(String date)  {
		if(date == "" || date == null) {
			return null;
		}

		if(date.contains("ago")) {
			try {
				return chineseDateFormat.parse(getDateByAgo(date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		// 去除日期时间中间的所有的空格????
		/*if(date.contains(" ")) {
			date = date.replace(" ", "");
		}*/

		Pattern p0 = Pattern.compile(regEx_date_style0);
		Matcher m0 = p0.matcher(date);
		Pattern p1 = Pattern.compile(regEx_date_style1);
		Matcher m1 = p1.matcher(date);
		Pattern p2 = Pattern.compile(regEx_date_style2);
		Matcher m2 = p2.matcher(date);
		Pattern p3 = Pattern.compile(regEx_date_style3);
		Matcher m3 = p3.matcher(date);
		Pattern p4 = Pattern.compile(regEx_date_style4);
		Matcher m4 = p4.matcher(date);
		Pattern p5 = Pattern.compile(regEx_date_style5);
		Matcher m5 = p5.matcher(date);
		Pattern p6 = Pattern.compile(regEx_date_style6);
		Matcher m6 = p6.matcher(date);
		Pattern p7 = Pattern.compile(regEx_date_style7);
		Matcher m7 = p7.matcher(date);
		Pattern p8 = Pattern.compile(regEx_date_style8);
		Matcher m8 = p8.matcher(date);
		Pattern p9 = Pattern.compile(regEx_date_style9);
		Matcher m9 = p9.matcher(date);
		Pattern p10 = Pattern.compile(regEx_date_style10);
		Matcher m10 = p10.matcher(date);
		String str = "";
		
		// 外文网站的日期
	    // 按照年份进行截取字符串
		if(m3.find()) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy", Locale.US);
			String t = m3.group().trim();
			String m = t.substring(0, t.indexOf(" "));
			if(m.length() > 3) {
				t = t.replace(m, m.substring(0, 3));
			}
			Date date2Format = null;
			try {
				date2Format = sdf.parse(t);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 重新格式化日期并进行设置
			str = new SimpleDateFormat("yyyy-MM-dd").format(date2Format);
		} else if(m9.find()) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMMMM. dd, yyyy", Locale.US);
			String t = m9.group().trim();
			String m = t.substring(0, t.indexOf(" "));
			if(m.length() > 4) {
				t = t.replace(m, m.substring(0, 4));
			}
			Date date2Format = null;
			try {
				date2Format = sdf.parse(t);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 重新格式化日期并进行设置
			str = new SimpleDateFormat("yyyy-MM-dd").format(date2Format);
		} else if(m10.find()) {
			String month = m10.group(1);
			String day = m10.group(2);
			String year = m10.group(3);
			SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd yyyy", Locale.US);
			String t = month + " " + day + " " + year;
			String m = t.substring(0, t.indexOf(" "));
			if(m.length() > 3) {
				t = t.replace(m, m.substring(0, 3));
			}
			Date date2Format;
			try {
				date2Format = sdf.parse(t);
				// 重新格式化日期并进行设置
				str = new SimpleDateFormat("yyyy-MM-dd").format(date2Format);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if(m4.find()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy", Locale.US);
			Date date2Format = null;
			try {
				date2Format = sdf.parse(m4.group().trim());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 重新格式化日期并进行设置
			str = new SimpleDateFormat("yyyy-MM-dd").format(date2Format);
		} else if(m0.find()) {
			String year = m0.group(1);
			String month = m0.group(2);
			String day = m0.group(3);
			month = month.length()==2?month:("0" + month);
			day = day.length()==2?day:("0" + day);
			str = year + "-" + month + "-" + day;
		} else if(m1.find()) {
			String year = m1.group(1);
			String month = m1.group(2);
			String day = m1.group(3);
			month = month.length()==2?month:("0" + month);
			day = day.length()==2?day:("0" + day);
			str = year + "-" + month + "-" + day;
		} else if(m8.find()) {
			String year = m8.group(1);
			String month = m8.group(2);
			String day = m8.group(3);
			str = year + "-" + month + "-" + day;
		} else if(m2.find()) {
			// 包含 yyyy年MM月dd日 的日期格式
			try {
				str = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy年MM月dd日").parse(m2.group()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(m5.find()) {
			String dd = m5.group(1);
			String mm = m5.group(2);
			String yyyy = m5.group(3);
			str = yyyy + "-" + mm + "-" + dd;
		} else if(m6.find()) {
			String yyyy = m6.group(1);
			String mm = m6.group(2);
			String dd = m6.group(3);
			str = yyyy + "-" + mm + "-" + dd;
		} else if(m7.find()) {
			String month = m7.group(1);
			String day = m7.group(2);
			String year = m7.group(3);
			month = month.length()==2?month:("0" + month);
			day = day.length()==2?day:("0" + day);
			str = year + "-" + month + "-" + day;
		} 
		if(str.length() > 1) {
			// 若 str 不为空, 直接返回 str
			try {
				return chineseDateFormat.parse(str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    // throw new ParseException("Bad format date, Please check!!!", 6);
		
		// 默认返回前一天的格式化之后的日期
		return new Date(System.currentTimeMillis() - 60 * 60 * 24 * 1000);
		
	}
	
	private static String getDateByAgo(String date) {
		// String resultDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		int dateCount = 0;
		date = date.trim();
		String[] dates = date.split(" ");
		if(dates[1].equals("days") || dates[1].equals("day")) {
			dateCount = Integer.parseInt(dates[0]) + 1;
		} else if(dates[1].equals("hours")) {
			dateCount = 1;
		}
		String result = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - 60 * 60 * 24 * 1000 * dateCount));
		return result;
	}

    /**
     * @函数名称：dateTo8 @功能描述：10位yyyy/MM/dd,yyyy-MM-dd,yyyy:MM:dd 转换为8位yyyyMMdd
     * @param date 要格式化的日期字符串: 10位 yyyy/MM/dd或yyyy-MM-dd或yyyy:MM:dd
     * @return 返回格式化后的日期
     */    
    public static String timeNumberTodate(String time)
    {
        int len = time.length();
        return time.substring(0, len - 4) + formatDateSign
                + time.substring(len - 4, len - 2) + formatDateSign
                + time.substring(len - 2, len);
    }

    public static String dateTo8(String date)
    {
        if (date == null)
            return "";
        if (date.trim().length() != 10)
        {
            return date;
        }
        return date.substring(0, 4) + date.substring(5, 7)
                + date.substring(8, 10);
    }

    public static String dateTo8Ext(String date)
    {
        if (null == date || date.equals(""))
            return "";
        String datestr[] = DataTypeUtil.spiltStr(date, "-");
        String mon = "";
        String day = "";
        if (datestr[1].trim().length() != 2)
        {
            mon = "0" + datestr[1];
        } else
        {
            mon = datestr[1];
        }
        if (datestr[2].trim().length() != 2)
        {
            day = "0" + datestr[2];
        } else
        {
            day = datestr[2];
        }

        return datestr[0] + mon + day;
    }

    /**
     * 将yyyyMMdd格式的字符串日期转化为日期格式
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
    	Date date = null;
    	try {
    		date = new SimpleDateFormat(getDateFormat(strDate)).parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return date;
    }
    
    /**
             * 常规自动日期格式识别
     * @param str 时间字符串
     * @return Date
     * @author dc
     */
    private static String getDateFormat(String str) {
        boolean year = false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        if(pattern.matcher(str.substring(0, 4)).matches()) {
            year = true;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if(!year) {
            if(str.contains("月") || str.contains("-") || str.contains("/")) {
                if(Character.isDigit(str.charAt(0))) {
                    index = 1;
                }
            }else {
                index = 3;
            }
        }
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            if(Character.isDigit(chr)) {
                if(index==0) {
                    sb.append("y");
                }
                if(index==1) {
                    sb.append("M");
                }
                if(index==2) {
                    sb.append("d");
                }
                if(index==3) {
                    sb.append("H");
                }
                if(index==4) {
                    sb.append("m");
                }
                if(index==5) {
                    sb.append("s");
                }
                if(index==6) {
                    sb.append("S");
                }
            }else {
                if(i>0) {
                    char lastChar = str.charAt(i-1);
                    if(Character.isDigit(lastChar)) {
                        index++;
                    }
                }
                sb.append(chr);
            }
        }
        return sb.toString();
    }
    

    
    
    /**
     * @函数名称：dateTo8 @功能描述：8位yyyyMMdd 转换为yyy/MM/dd,yyyy-MM-dd,yyyy:MM:dd
     * @param date 要格式化的日期字符串: 8位yyyyMMdd
     * @return 返回格式化后的日期
     */
    public static String dateTo10(String date)
    {
        if (date == null)
            return "";
        if (date.trim().length() != 8)
            return "";
        return date.substring(0, 4) + formatDateSign + date.substring(4, 6)
                + formatDateSign + date.substring(6, 8);
    }

    public static String dateTo19(String date)
    {
        int len = date.length();
        if (len != 14)
            return date;
        return date.substring(0, 4) + formatDateSign + date.substring(4, 6)
                + formatDateSign + date.substring(6, 8) + formatDandTSign
                + date.substring(8, 10) + formatTimeSign
                + date.substring(10, 12) + formatTimeSign
                + date.substring(12, 14);
    }

    /**
     * @函数名称：dateTo14 @功能描述：8位yyyyMMdd 转换为yyy/MM/dd,yyyy-MM-dd,yyyy:MM:dd
     * @param date 要格式化的日期字符串: 8位yyyyMMdd
     * @return 返回格式化后的日期
     */
    public static String dateTo14(String date)
    {
        if (date == null)
            return "";
        if (date.trim().length() != 19)
            return date;
        return date.substring(0, 4) + date.substring(5, 7)
                + date.substring(8, 10) + date.substring(11, 13)
                + date.substring(14, 16) + date.substring(17);
    }

    /**
     * @函数名称：oracleDateTo8 @功能描述：9位yy-mmm-dd 转换为yyyy-MM-dd
     * @param date 要格式化的日期字符串:9位yy-mmm-dd
     * @return 返回格式化后的日期
     * @write :wujs
     * @date :2007-08-30
     */
    public static String oracleDateTo8(String date)
    {
        if (date == null)
            return "";
        if (date.trim().length() != 9)
            return date;
        String month = "";
        String smonth = date.substring(3, 6);
        if (smonth.equals("JAN"))
            month = "01";
        else if (smonth.equals("FEB"))
            month = "02";
        else if (smonth.equals("MAR"))
            month = "03";
        else if (smonth.equals("APR"))
            month = "04";
        else if (smonth.equals("MAY"))
            month = "05";
        else if (smonth.equals("JUN"))
            month = "06";
        else if (smonth.equals("JUL"))
            month = "07";
        else if (smonth.equals("AUG"))
            month = "08";
        else if (smonth.equals("SEP"))
            month = "09";
        else if (smonth.equals("OCT"))
            month = "10";
        else if (smonth.equals("NOV"))
            month = "11";
        else if (smonth.equals("DEC"))
            month = "12";
        return "20" + date.substring(7, 9) + formatDateSign + month
                + formatDateSign + date.substring(0, 2);
    }

    /**
     * 时间格式化。 <br>
     * 8位(HH:mm:ss)或7位(H:mm:ss)的时间转换为6位(HHmmss)或5位(Hmmss) <br>
     * 时间的分隔字符可以是任意字符，一般为冒号(:)
     * 
     * @param time -要格式化的时间字符串:8位(HH:mm:ss)或7位(H:mm:ss)
     * @return String - 返回格式化后的时间 <br>
     * 若time长度不为8或7，即格式不为8位(HH:mm:ss)或7位(H:mm:ss)形式的时间，则直接返回date。 <br>
     * 若time为null, 则返回""
     */
    public static String timeTo6(String time)
    {
        int len = time.length();
        if (len < 7 || len > 8)
            return "";
        return time.substring(0, len - 6) + time.substring(len - 5, len - 3)
                + time.substring(len - 2, len);
    }

    /**
     * 时间格式化。 <br>
     * 6位(HHmmss)或5位(Hmmss)的时间转换为8位(HH:mm:ss)或7位(H:mm:ss)
     * @param date -要格式化的时间字符串: 6位(HHmmss)或5位(Hmmss)
     * @return String - 返回格式化后的时间 <br>
     * 若time长度不为5或6，即格式不为6位(HHmmss)或5位(Hmmss)形式的时间，则直接返回date。 <br>
     * 若time为null, 则返回""
     */
    public static String timeTo8(String time)
    {
        int len = time.length();
        if (len < 5 || len > 6)
            return "";
        return time.substring(0, len - 4) + formatTimeSign
                + time.substring(len - 4, len - 2) + formatTimeSign
                + time.substring(len - 2, len);
    }

    /**
     * @函数名称：stringToSqlDate 
     * @功能描述：将String型的日期格式转换为Sql型的日期格式 
     * @param str
     * @return
     */
    public static java.sql.Date stringToSqlDate(String str)
    {
        if (stringToUtilDate(str) == null || str.length() < 1)
            return null;
        else
            return new java.sql.Date(stringToUtilDate(str).getTime());
    }

    /**
     * @函数名称：stringToUtilDate 
     * @功能描述：将String型的日期格式转换为Util型的日期格式 
     * @param str
     * @return
     */
    public static java.util.Date stringToUtilDate(String str)
    {
        if (null != str && str.length() > 0)
        {
            try
            {
                // 对两种时间格式进行转化。
                if (str.length() <= simpleDateFormat.length())
                { // 只包含日期。
                    return (new SimpleDateFormat(simpleDateFormat)).parse(str);
                } else
                { // 日期和时间都有
                    return (new SimpleDateFormat(simpleTimeFormat)).parse(str);
                }
            } catch (ParseException ex)
            {
                ex.printStackTrace();
                return null;
            }
        } else
            return null;
    }

    /**
     * @函数名称：utilDateToSql
     * @功能描述：将Util型的日期格式转换为Sql型的日期格式
     * @param date
     * @return
     */
    public static java.sql.Date utilDateToSql(java.util.Date date)
    {
        return new java.sql.Date(date.getTime());
    }

    /**
     * @函数名称：sqlDateToUtil
     * @功能描述：将Sql型的日期格式转换为Util型的日期格式
     * @param date
     * @return
     */
    public static java.util.Date sqlDateToUtil(java.sql.Date date)
    {
        return new java.util.Date(date.getTime());
    }

    /**
     * @函数名称：toDateTimeString
     * @功能描述：将Sql型的带时间日期格式转换为String型的日期格式
     * @param d
     * @return
     */
    public static String toDateTimeString(java.sql.Date d)
    {
        if (d == null)
        {
            return null;
        } else
        {
            return (new SimpleDateFormat(simpleTimeFormat)).format(d);
        }
    }

    /**
     * @函数名称：toDateTimeString
     * @功能描述：将Util型的带时间日期格式转换为String型的日期格式
     * @param d
     * @return
     */
    public static String toDateTimeString(java.util.Date d)
    {
        if (d == null)
        {
            return null;
        } else
        {
            return (new SimpleDateFormat(simpleTimeFormat)).format(d);
        }
    }

    /**
     * @函数名称：toDateString
     * @功能描述：将Sql型的只带日期格式转换为String型的日期格式
     * @param d
     * @return
     */
    public static String toDateString(java.sql.Date d)
    {
        if (d == null)
        {
            return null;
        } else
        {
            return (new SimpleDateFormat(simpleDateFormat)).format(d);
        }
    }

    /**
     * @函数名称：toDateString
     * @功能描述：将Sql型的只带日期格式转换为String型的日期格式
     * @param d
     * @return
     */
    public static String toDateString(java.util.Date d)
    {
        if (d == null)
        {
            return null;
        } else
        {
            return (new SimpleDateFormat(simpleDateFormat)).format(d);
        }
    }

    /**
     * @函数名称：getCurrentDate 
     * @功能描述：获取当前日期和时间 
     * @return
     */
    public static java.sql.Date getCurrentDateTime()
    {
        Calendar newcalendar = null;
        newcalendar = Calendar.getInstance();

        String year = String.valueOf(newcalendar.get(Calendar.YEAR));
        String month = String.valueOf(newcalendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(newcalendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(newcalendar.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(newcalendar.get(Calendar.MINUTE));
        String sec = String.valueOf(newcalendar.get(Calendar.MINUTE));

        return stringToSqlDate(year + formatDateSign + month + formatDateSign
                + day + " " + hour + formatTimeSign + min + formatTimeSign
                + sec);
    }

    /**
     * @函数名称：getCurrentDate 
     * @功能描述：获取当前日期(只带日期) 
     * @return
     */
    public static java.sql.Date getCurrentDate()
    {
        Calendar newcalendar = null;
        newcalendar = Calendar.getInstance();

        String year = String.valueOf(newcalendar.get(Calendar.YEAR));
        String month = String.valueOf(newcalendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(newcalendar.get(Calendar.DAY_OF_MONTH));

        return stringToSqlDate(year + formatDateSign + month + formatDateSign
                + day);
    }

    /**
     * @函数名称：getCurrentTime 
     * @功能描述：获取当前时间(只带时间) 
     * @return
     */
    public static String getCurrentTime()
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        String timeString = dataFormat.format(date);
        return timeTo8(timeString);
    }

    /**
     * @函数名称：getCurrentTime 
     * @功能描述：获取当前时间(只带时间) 
     * @return
     */
    public static String getCurrentDateTimeStr()
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String timeString = dataFormat.format(date);
        return timeString;
    }

    public static String getCurrentDateTimeStr1()
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String timeString = dataFormat.format(date);
        return timeString;
    }

    /**
     * 功能描述：返回yyyyMMddHHmmss格式化的时间字符串
     * @param date
     * @return
     */
    public static String getCurrentDateTimeStr1(Date date)
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeString = dataFormat.format(date);
        return timeString;
    }

    public static String getCurrentDateStr()
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String timeString = dataFormat.format(date);
        return timeString;
    }

    public static String getCurrentDateStr1()
    {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String timeString = dataFormat.format(date);
        return timeString;
    }

    public static String getWeekStr()
    {
        String s = "";
        int week = getWeek();
        switch (week)
        {
            case 1:
                s = "星期一";
                break;
            case 2:
                s = "星期二";
                break;
            case 3:
                s = "星期三";
                break;
            case 4:
                s = "星期四";
                break;
            case 5:
                s = "星期五";
                break;
            case 6:
                s = "星期六";
                break;
            case 7:
                s = "星期天";
                break;
            default:
                break;
        }
        return s;
    }

    /**
     * 获取当前是星期几。 <br>
     * 0为星期天、1为星期一、以此类推。
     * @return String - 返回当前星期几
     */
    public static int getWeek()
    {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int posOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        posOfWeek--; // Calendar格式转化成普通格式 0星期天， 1 星期一...
        return posOfWeek;
    }

    /**
     * 
     * @函数名称：addYear
     * @功能描述：
     * @param beginTime
     * @return
     */
    public static java.sql.Date addYear(java.sql.Date beginTime, int i)
    {
        Calendar date = Calendar.getInstance();
        date.setTime(beginTime);
        date.add(Calendar.YEAR, i);
        return utilDateToSql(date.getTime());
    }

    /**
     * 
     * @函数名称：addMonth
     * @功能描述：
     * @param beginTime
     * @return
     */
    public static java.sql.Date addMonth(java.sql.Date beginTime, int i)
    {
        Calendar date = Calendar.getInstance();
        date.setTime(beginTime);
        date.add(Calendar.MONTH, i);
        return utilDateToSql(date.getTime());
    }

    /**
     * 
     * @函数名称：addMonth
     * @功能描述：
     * @param beginTime
     * @return
     */
    public static java.util.Date addMonth(java.util.Date beginTime, int i)
    {
        Calendar date = Calendar.getInstance();
        date.setTime(beginTime);
        date.add(Calendar.MONTH, i);
        return date.getTime();
    }

    /**
	 * 增加或减少天数
	 * @param date
	 * @param num
	 * @return
	 */
    public static Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
    
    /**
     * 
     * @函数名称：addDay
     * @功能描述：
     * @param beginTime
     * @return
     */
    public static java.sql.Date addDay1(java.sql.Date beginTime, int i)
    {
        Calendar date = Calendar.getInstance();
        date.setTime(beginTime);
        date.add(Calendar.DAY_OF_MONTH, i);
        return utilDateToSql(date.getTime());
    }

    /**
     * 
     * @函数名称：compareYear
     * @功能描述：比较年
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int compareYear(java.sql.Date beginTime, java.sql.Date endTime)
    {
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        begin.setTime(beginTime);
        end.setTime(endTime);
        int compareYear = end.get(Calendar.YEAR) - begin.get(Calendar.YEAR);
        return compareYear;
    }

    /**
     * 
     * @函数名称：compareMonth
     * @功能描述：比较月
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int compareMonth(java.sql.Date beginTime,
            java.sql.Date endTime)
    {
        int compareYear = compareYear(beginTime, endTime);
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        begin.setTime(beginTime);
        end.setTime(endTime);
        int compareMonth = compareYear * 12
                + (end.get(Calendar.MONTH) - begin.get(Calendar.MONTH));
        return compareMonth;
    }

    /**
     * 
     * @函数名称：compareDay
     * @功能描述：比较天
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int compareDay(java.sql.Date beginTime, java.sql.Date endTime)
    {
        long compare = (endTime.getTime() - beginTime.getTime())
                / (1000 * 3600 * 24);
        String compareStr = String.valueOf(compare);
        return Integer.parseInt(compareStr);
    }

    /**
     * 
     * @函数名称：compareHour
     * @功能描述：比较小时
     * @param date
     * @param date2
     * @return
     */
    public static int compareHour(Date date, Date date2)
    {
        long compare = (date2.getTime() - date.getTime()) / (1000 * 3600);
        String compareStr = String.valueOf(compare);
        return Integer.parseInt(compareStr);
    }

    /**
     * 
     * @函数名称：compareHour
     * @功能描述：比较小时
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int compareHourByTimestamp(String beginTime, String endTime)
    {
        Long beginTimestamp = Long.parseLong(beginTime);
        Long endTimestamp = Long.parseLong(endTime);
        return compareHour(new java.util.Date(beginTimestamp),
                new java.util.Date(endTimestamp));
    }

    /**
     * 获取一个月的最后一天。 <br>
     * 例如20030111,即一月份的最后一天是20030131
     * @param date -指定日期(yyyyMMdd)
     * @return String - 返回计算后的日期
     */
    public static String getLastDayOfMonth(String date)
    {
        if (date.length() != 8)
            return "";
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = 0;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                || month == 10 || month == 12)
        {
            day = 31;
        } else if (month == 2)
        {
            if (((year % 4) == 0) && ((year % 100) != 0))
            {
                day = 29;
            } else
            {
                day = 28;
            }
        } else
        {
            day = 30;
        }

        String newDate = "" + year;
        if (month < 10)
        {
            newDate += "0" + month + day;
        } else
        {
            newDate += "" + month + day;
        }
        return newDate;
    }

    /**
     * 获取一个日期的星期 <br>
     * 
     * @param date -指定日期(yyyy-MM-dd)
     * @return String - 返回算好的星期
     */
    public static String getWeek(String sDate)
    {
        final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
                "星期六" };

        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();

        try
        {
            date = sdfInput.parse(sDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayNames[dayOfWeek - 1];
    }

    /**
     * 获取一个日期的农历日期和星期 <br>
     * 
     * @param date -指定日期(yyyy-MM-dd)
     * @return String - 返回字符串：YYYY年MM月日 农历YYYY年十二月初二 星期三
     */
    /*public static String getSystemDateStr(String inDateStr)
            throws ParseException
    {
        Calendar today = Calendar.getInstance();
        today.setTime(chineseDateFormat.parse(inDateStr));
        lunar lunar = new Lunar(today);
        String outDateStr = inDateStr.substring(0, 4) + "年"
                + inDateStr.substring(5, 7) + "月" + inDateStr.substring(8, 10)
                + "日" + "　农历" + lunar + " " + getWeek(inDateStr);
        return outDateStr;

    }*/

    // date日期转换为GMT格式
    public static String StrToGMTDateString(String str)
    {
        if (null == str || str.equals("") || str.equals("NULL"))
        {
            return "";
        }
        Date date = stringToUtilDate(str);
        String[] array = FuncUtils.spiltStr(date.toGMTString(), " ");
        if (array[0].length() < 2)
        {
            array[0] = "0" + array[0];
        }
        array[2] = array[2].substring(2);
        return array[2] + "-" + array[1] + "-" + array[0];
    }

    /**
     * 日期处理
     * @param dateStr
     * @return
     */
    public static String dateProcess(String dateStr)
    {
        if (FuncUtils.isNull(dateStr))
            return "";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try
        {
            return format.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(dateStr));
        } catch (ParseException e)
        {
        }
        return null;
    }

    /**
     * 日期处理
     * @param dateStr
     * @return
     */
    public static String dateConv(String dateStr, String from_pattern,
            String to_pattern)
    {
        if (FuncUtils.isNull(dateStr))
            return "";
        SimpleDateFormat format = new SimpleDateFormat(to_pattern);
        try
        {
            return format.format(new SimpleDateFormat(from_pattern)
                    .parse(dateStr));
        } catch (ParseException e)
        {
        }
        return null;
    }

    public static boolean compareAWithB(Date a, Date b)
    {
        return a.before(b);
    }

    /**
     * 比较String类型的时间大小
     * @param a
     * @param b
     * @return true 表示a<b
     */
    public static boolean compareString(String a, String b)
    {
        java.util.Date x = stringToUtilDate(a);
        java.util.Date y = stringToUtilDate(b);
        return compareAWithB(x, y);
    }

    /**
     * 比较当前时间是否超出指定时间固定的毫秒数
     * @param a String类型且yyyy-MM-dd HH:mm:ss
     * @param time 比对的秒数
     * @return
     */
    public static boolean compareWithNow(String a, String time)
    {
        java.util.Date x = stringToUtilDate(a);
        int interval = Integer.parseInt(time);
        java.util.Date y = new Date(System.currentTimeMillis() - interval
                * 1000);
        return compareAWithB(x, y);
    }

    /**
     * @函数名称：getCurrentYearMonthStr 
     * @功能描述：获取当前日期(只带年月) 格式 yyMM
     * @return
     */
    public static String getCurrentYearMonthStr()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMM");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        return dateString;
    }
    /**
     * 当前时间加分钟 格式yyyyMMddHHmmss
     * @param minute
     * @return
     */
    public static String addMinute(String minute)
    {
        if(FuncUtils.isNull(minute)){
            return null;
        }

        try
        {
            int minuteInt=Integer.valueOf(minute);
            java.util.Date newDate = new java.util.Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(newDate);
            cal.add(Calendar.MINUTE, minuteInt);
            Date nextDate = cal.getTime();
            String next_dateStr = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(nextDate);
            return next_dateStr;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }
    
    
    //获取当月最后一天日期
  	public static String getLastonthsLastDay(){
  		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
  		 Calendar ca = Calendar.getInstance();    
         ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));  
         String last = format.format(ca.getTime());
  		return last;
  	}
  	
  	//获取上月第一天日期
  	public static String getLastonthsFirstDay(){
  		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        //获取前月的第一天
        Calendar   cal_1=Calendar.getInstance();//获取当前日期 
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
       String  firstDay = format.format(cal_1.getTime());
  		return firstDay;
  	  }
  	
}