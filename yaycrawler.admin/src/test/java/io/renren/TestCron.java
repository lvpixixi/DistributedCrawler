package io.renren;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

public class TestCron {
	 private static String cron2QuartzCron(String cronExp){
	    	String[] crons = ("0 "+cronExp).split(" ");
	    	if(crons[3].equals("*")&&!crons[5].equals("*")){
	    		crons[3] = "?";
	    	}else if(crons[3].equals("*")&&crons[5].equals("*")){
	    		crons[5] = "?";
	    	}else{
	    		crons[5] = "?";
	    	}
	    	
	    	return crons[0]+" "+crons[1]+" "+crons[2]+" "+crons[3]+" "+crons[4]+" "+crons[5];
	    }
	public static void main(String[] args) {
		  String cron=cron2QuartzCron("0 0 1 1 *");
		  System.out.println(cron);
		  try {
	        	CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
				cronTriggerImpl.setCronExpression(cron);
				Calendar calendar = Calendar.getInstance();
		        Date now = calendar.getTime();
		        calendar.add(Calendar.MONTH, 1);// 把统计的区间段设置为从现在到1月后的今天（主要是为了方法通用考虑)

		        // 这里的时间是根据corn表达式算出来的值
		        List<Date> dates = TriggerUtils.computeFireTimesBetween(
		                cronTriggerImpl, null, now,
		                calendar.getTime());
		        
		        SimpleDateFormat dateFormat = new SimpleDateFormat(
	                    "yyyy-MM-dd HH:mm:ss");
		        int index = 0;
		        List<String> dateList = new ArrayList<>();
	            for (Date date : dates) {
	            	dateList.add(dateFormat.format(date));
	            	  System.out.println(dateFormat.format(date));
	            	if(index++>5){
	            		break;
	            	}
	            }
	          
	            
			} catch (ParseException e) {
				e.printStackTrace();
				
			}

	}

}
