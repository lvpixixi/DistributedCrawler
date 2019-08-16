package com.mss.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scripts.Language;
import us.codecraft.webmagic.scripts.ScriptEnginePool;


/**
 * JavaScript 工具类
 * @author wwq
 *
 */
public class ScriptUtils {

	private static Logger logger = LoggerFactory.getLogger(ScriptUtils.class);
	
	private static ScriptEnginePool enginePool;
	
	private static String script;
	private static String defines;
	private static ResourceBundle resource = ResourceBundle.getBundle("translateConfig");		 
	
	static {
		enginePool = new ScriptEnginePool(Language.JavaScript, 20);
        try {
        	InputStream definesStream = Spider.class.getClassLoader().
        			getResourceAsStream(Language.JavaScript.getDefineFile());
            defines = IOUtils.toString(definesStream);
            InputStream scriptStream = Spider.class.getClassLoader().
            		getResourceAsStream(resource.getString("SCRIPT_FILE_PATH"));
            script = IOUtils.toString(scriptStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
	}
	
	public static Object executeScript(String script) {
		ScriptEngine engine = enginePool.getEngine();
		ScriptContext context = engine.getContext();
		Object result = null;
		try {
			result = engine.eval(script, context);
		} catch (Exception e) {
			logger.error("",e);
		} finally {
            enginePool.release(engine);
        }
		return result;	
	}
	
	/**
	 * 执行JavaScript
	 * @param functionName
	 * @param site
	 * @param page
	 * @param args
	 * @return
	 */
	public static Object executeScript(String functionName, Site site,
			Page page, Object... args) {

		ScriptEngine engine = enginePool.getEngine();
		ScriptContext context = engine.getContext();
        context.setAttribute("page", page, ScriptContext.ENGINE_SCOPE);
        context.setAttribute("config", site, ScriptContext.ENGINE_SCOPE);
        Object result = null;
		try {
			engine.eval(defines + "\n" + script, context);
	        Invocable inv = (Invocable) engine;
	        result =inv.invokeFunction(functionName, args);
			//System.out.println(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            enginePool.release(engine);
        }
		return result;
    }
	
	/**
	 *  执行JS函数
	 * @param functionName
	 * @param args
	 * @return
	 */
	public static Object execScript(String functionName, Object... args) {

		ScriptEngine engine = enginePool.getEngine();
		ScriptContext context = engine.getContext();
        Object result = null;
		try {
			engine.eval(defines + "\n" + script, context);
	        Invocable inv = (Invocable) engine;
	        result =inv.invokeFunction(functionName, args);
			//System.out.println(result.toString());
		} catch (Exception e) {
			logger.error("",e);
		} finally {
            enginePool.release(engine);
        }
		return result;
    }
	
	/**
	 * 获取Check_browser 方法体
	 * @param str
	 * @return
	 */
	public static String getCheck_browser(String str) {
		String script = "function test(a){var json = "+str+"; return json.map(function(item) { return String.fromCharCode(item)}).join(\"\");} "; 
		Object result = null;
		try {
			result = executeScript(script,"test");
		} catch (NoSuchMethodException | ScriptException e) {
			logger.error("execute script error,", e);
			return null;
		}
		if(result != null) {
			return result.toString();
		} 
		return null;
	}
	
	public static String getToken(String data, String orgScript) {
		String script3 = "function check_browser(){ var data = "+data+"; "+orgScript+" return location_info+'';}"; 
		Object result = null;
		try {
			result = executeScript(script3,"check_browser");
		} catch (NoSuchMethodException | ScriptException e) {
			logger.error("execute script error,", e);
			return null;
		}
		if(result != null) {
			return result.toString();
		} 
		return null;
	}
	
	public static void main(String[] args) {
		//Object str = executeScript("test1", null, null);
		/*String str1 = "[102,117,110,99,116,105,111,110,32,99,104,101,99,107,95,98,114,111,119,115,101,114,40,100,97,116,97,41,123,32,10,32,32,32,32,32,108,111,99,97,116,105,111,110,95,105,110,102,111,32,61,32,49,51,52,50,49,55,55,50,55,32,45,32,100,97,116,97,46,118,97,108,117,101,59,10,125,32,10,108,111," +
				"99,97,116,105,111,110,95,105,110,102,111,32,61,32,54,54,54,53,57,48,51,56,49,59]";
		String script1 = "function test(data){var json = "+str1+"; return json.map(function(item) { return String.fromCharCode(item)}).join(\"\");} "; 
		//String s = "function check_browser(data){ var data = "+data+"; location_info = 8388607 ^ data.value; return location_info+'';}";
		//String script1 = "function test(item){return String.fromCharCode(item)}";
		String s2 = "function test() { return (8388607 ^ 77051441) +''}";
		
		String str2 = "function getUrls(){ var urls={}; var urlArr=['shareholderUrl','anCheYearInfo']; var shareholderUrl = '123123';"+
				" var anCheYearInfo ='123'; for(var i=0; i<urlArr.length; i++){ return urls.urlArr[i].replace(''',''); } return urls;}";
		Object str = null;
		try {
			str = executeScript(str2,"getUrls");
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
		System.out.println(str.toString());*/
		
		/*try {
			Object result = execScript("strEnc", "联想","a","b","c");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		String script1 = "eval('((function(){var a\\x3d1605932948;var b\\x3d2450142413;return 415780+\\x27.\\x27+(a+b)})())');";
		Object o;
		try {
			o = executeScript(script1);
			execScript("tk", "hello, world",o.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 传入script脚本并执行其中的js方法
	 * @param script
	 * @param funName
	 * @return
	 * @throws ScriptException 
	 * @throws NoSuchMethodException 
	 */
	public static Object executeScript(String script, String funName, String... args) throws ScriptException, NoSuchMethodException {
		ScriptEngine engine = enginePool.getEngine();
		ScriptContext context = engine.getContext();
		Object result = null;
		Invocable inv = (Invocable) engine;
	    try {
	    	engine.eval(script, context);
			result =inv.invokeFunction(funName, args);
		}  finally {
			enginePool.release(engine);
		}
		return result;
	}
	
}
