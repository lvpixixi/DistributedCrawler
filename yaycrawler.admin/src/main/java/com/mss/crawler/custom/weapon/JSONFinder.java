package com.mss.crawler.custom.weapon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


  
/** 
 *  
 * @company  
 * @author superboo 
 * @version 3.0 
 * @date 2014-5-21 上午09:45:51 
 */  
public class JSONFinder {  
   
  
 
    @SuppressWarnings("rawtypes")  
    public static Map<String,String>  analysisJson(Object objJson,String parentKey){  
    	
    	Map<String,String> curKeys = new HashMap<String,String>();
    	
    	
          
        //如果obj为json数组  
        if(objJson instanceof JSONArray){  
            JSONArray objArray = (JSONArray)objJson;  
            for (int i = 0; i < objArray.size(); i++) {  
                Map<String,String> childKeys= analysisJson(objArray.get(i),parentKey+"["+i+"]");  
                curKeys.putAll(childKeys);
            }  
        }  
        //如果为json对象  
        else if(objJson instanceof JSONObject){  
            JSONObject jsonObject = (JSONObject)objJson;  
            Iterator it = jsonObject.keySet().iterator(); 
            while(it.hasNext()){  
                String key = it.next().toString();  
                Object object = jsonObject.get(key);  
                //如果得到的是数组  
                if(object instanceof JSONArray){  
                    JSONArray objArray = (JSONArray)object;  
                    Map<String,String> childKeys= analysisJson(objArray,parentKey+"["+key+"]");  
                    curKeys.putAll(childKeys);
                }  
                //如果key中是一个json对象  
                else if(object instanceof JSONObject){  
                    Map<String,String> childKeys= analysisJson((JSONObject)object,parentKey+"["+key+"]");  
                    curKeys.putAll(childKeys);
                }  
                //如果key中是其他  
                else{  
                	curKeys.put(key, parentKey+"["+key+"]");
                }  
            }  
        }  
        return curKeys;
    }  
    public static void main(String[] args) {  
        JSONFinder hw = new JSONFinder();  
        Map<String,String> result = JSONFinder.analysisJson("","");  
    }  
}  
