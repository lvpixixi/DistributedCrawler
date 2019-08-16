package bscrawler.spider.util;

import java.nio.ByteBuffer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

import com.alibaba.fastjson.JSONObject;

/**
 * 验证码Utils类
 * @author 伟其
 *
 */
public class YzmUtils {

	protected static Logger logger = LoggerFactory.getLogger(YzmUtils.class);//日志
	private static String userName = "123027921";
	private static String pwd = "19820302";
	private static String appId = "894369";
	
	/**
	 * 获取验证码
	 * @param imagePath
	 * @return
	 */
	public static JSONObject getAuthCode(String imagePath, String codeType) {
		logger.info("chaojiying entry ...");
		//String authCode = ChaoJiYing.PostPic("wwqlky", "130307", "891249", codeType, "0", "60", "test", imagePath);
		String authCode = ChaoJiYing.PostPic(userName, pwd, appId, codeType, "0", "60", "test", imagePath);
		logger.debug("chaojiying result is "+authCode);
		try {
			JSONObject json = JSONObject.parseObject(authCode);
			if(!"0".equals(json.getString("err_no")) || 
					!"OK".equals(json.getString("err_str"))) {
				reportError(json.getString("pic_id"));
				return null;
			} else {
				return json;
			}
		} catch (Exception e) {
			logger.error("fromObject error",e);
			return null;
		}
		
	}

	
	

	/**
	 * 验证码报错返分
	 * @param picId
	 */
	public static void reportError(String picId) {
		ChaoJiYing.ReportError(userName, pwd, appId, picId);
	}
	public static void main(String[] args) {
		
		JSONObject json = getAuthCode("D:\\data\\webmagic\\authcode\\633d2e2717652ef140cb9ad777322e36.jpg", "1004");
		System.out.println(json);
		/*int i=0;E:\mss\BG0013
		while(true) {
			System.out.println(++i);
			reportError("2342423424");
		}*/
		/*String authCode = YzmUtils.crackCaptchaByOCR("/mss/authcode/1478053279157.jpg","ningxia");
		System.out.println(authCode);*/
		/*String[] args1 = new String[]{"2615888293004","2615889519704","2615892546804"};
		for(String picId : args1) {
			reportError(picId);
		}
		String s = crackTJCaptchaByOCR(new File("E:\\magi\\authcode\\1466328674073.jpg"));
		System.out.println(s);*/
	}
}
