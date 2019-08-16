package io.renren.modules.spider.utils;

import com.alibaba.druid.util.StringUtils;
import io.renren.modules.spider.controller.DataExportController;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取token类
 */
public class BaiduAPI {

	private static Logger LOG = LoggerFactory.getLogger(DataExportController.class);
	
	private static final int HTTP_STATUS_OK = 200;
	
    /**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public static String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = "gcYKYxPNb1aL7jCVBQU1qDwn";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "uvzoVop4fLxmmU9jlgGbVDTrLXPx8fKN";
        return getAuth(clientId, clientSecret);
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            org.json.JSONObject jsonObject = new org.json.JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }
    
    /**
     * 抽取新闻的摘要字段
     * @param args
     */
    public static String extraceSummary(String content) {
    	if(StringUtils.isEmpty(content)) {
    		return "";
    	}
    	String token = getAuth();
    	if(org.springframework.util.StringUtils.isEmpty(token)) {
    		System.out.println("获取 token 失败, 请检查相关的设置");
    	}
    	String url = "https://aip.baidubce.com/rpc/2.0/nlp/v1/news_summary?access_token=" + token;
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("content", content);
    	params.put("max_summary_len", 150);
    	params.put("access_token", getAuth());
    	String summary = "";
    	try {
			summary = sendPostMethod(url, content, 150, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return summary;
    }
    
    /**
     * 通过post协议发送请求，并获取返回的响应结果
     * @param url
     *            请求url
     * @param params
     *            post传递的参数
     * @param encoding
     *            编码格式
     * @return 返回服务器响应结果
     * @throws HttpException
     */
    public static String sendPostMethod(String urlStr, String content, int maxLength, 
            String encoding) throws Exception {
    	try {
            //创建连接
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.connect();

            //POST请求
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            JSONObject obj = new JSONObject();
//            obj.element("app_name", "asdf");
//            obj.element("app_ip", "10.21.243.234");
//            obj.element("app_port", 8080);
//            obj.element("app_type", "001");
//            obj.element("app_area", "asd");
            obj.element("content", content);
            obj.element("max_summary_len", maxLength);

            out.writeBytes(obj.toString());
            out.flush();
            out.close();

            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println(sb);
            reader.close();
            // 断开连接
            connection.disconnect();
            return sb.toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	return "";
    }
    
    public static String load(String url,String query) throws Exception
	{
		URL restURL = new URL(url);
		
		/*
		 * 此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类 的子类HttpURLConnection
		 */
		HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
		//请求方式
		conn.setRequestMethod("POST");
		//设置是否从httpUrlConnection读入，默认情况下是true; httpUrlConnection.setDoInput(true);
		conn.setDoOutput(true);
		//allowUserInteraction 如果为 true，则在允许用户交互（例如弹出一个验证对话框）的上下文中对此 URL 进行检查。
		conn.setAllowUserInteraction(false);

		PrintStream ps = new PrintStream(conn.getOutputStream());
		ps.print(query);

		ps.close();

		BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line,resultStr="";

		while(null != (line=bReader.readLine()))
		{
			resultStr +=line;
		}
		System.out.println(resultStr);
		bReader.close();

		return resultStr;

	}
    
    public static void main(String[] args) {
    	getAuth();
//		System.out.println(extraceSummary("着名奖项旨在表彰沃伦华盛顿和迈克尔曼通过研究和公共政策推动气候变化知识的努力。气候科学家Warren Washington（左）和Michael Mann于2019年2月12日获得Tyler奖。图片来源：Tyler Prize奖;照片来自Joshua Yospyn"
//+"气候科学家迈克尔曼和是今年 ，他们致力于推动有关气候变化的知识，他们在处理这一问题上的“非凡勇气”，以及他们对公共政策的承诺，该奖项的执行者委员会今天宣布。沃伦华盛顿泰勒环境成就奖的核心成员"
//+"是国家大气研究中心的杰出学者，宾夕法尼亚州立大学大气科学教授是杰出的气候科学家，他们也是向政策制定者和公众传播问题的专家，泰勒奖执行委员会在选择今年的获奖者时说。华盛顿曼恩"
//+"该委员会引用了华盛顿在气候模拟方面的开创性努力，他在1960年开始研究这种模式，当时笨重的计算机比今天的计算机慢得多。该委员会表示，“华盛顿气候模式对提高我们对气候的认识所产生的影响是巨大的。”华盛顿，第二位获得博士学位的非洲裔美国人。在气象学方面，“不仅在他的领域，而且在更大的科学界，已经成为少数民族的灵感和领导者。”"
//+"泰勒委员会称赞曼恩开创性的统计技术，以重建过去的全球气温，并通过他的“曲棍球棒”图表“自20世纪以来的温度升高是异常的和历史上前所未有的。”该委员会还提到攻击Mann的政府，个人和化石燃料公司的科学，并指出Mann一般为他的科学和气候科学辩护，同时还因其在气候通信方面的技能而获得多个奖项。"
//+"“特别及时”的认可"));
	}
    

}
