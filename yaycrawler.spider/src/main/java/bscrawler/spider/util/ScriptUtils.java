package bscrawler.spider.util;

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
		String script1 = "eval('((function(){var a\\x3d1605932948;var b\\x3d2450142413;return 415780+\\x27.\\x27+(a+b)})())');";
		Object o;
		try {
			o = executeScript(script1);
			execScript("tk", "hello, world",o.toString());
		} catch (Exception e) {
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
