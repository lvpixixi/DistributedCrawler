package io.renren.modules.spider.utils;

import java.util.ArrayList;
import java.util.List;
 
public class Sublist {
	
	public static List getPage(List list,int pageNum,int pagesize){
	 
		//总记录数
        double totalCount = list.size();

        //分多少次处理
        Integer requestCount = (int)Math.ceil(totalCount / pagesize); 
        int page = pageNum<=0?1:pageNum;
        if(pageNum>requestCount){
        	return new ArrayList();
        }
        
       
        int fromIndex = (page-1)*pagesize;
        int toIndex = Math.min((int)totalCount, fromIndex+pagesize);
       
        List subList = list.subList(fromIndex, toIndex);
        
        return subList;
	}
	public static void main(String[] args) {  
	    
	  List<Integer> list=new ArrayList<Integer>();       
/*	  for(int i=1;i<=52;i++){  
		  list.add(i);     
	  }  */
	  
	  List<Integer> page = getPage(list,-1,10);
	  for(Integer v:page)
		  System.out.print(v+",");

	 }  

}