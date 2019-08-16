import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

import com.google.common.collect.Lists;

public class QueueTest {
	
	public static void removeDuplicateWithOrder(List list) {    
	    Set set = new HashSet();    
	     List newList = new ArrayList();    
	   for (Iterator iter = list.iterator(); iter.hasNext();) {    
	         Object element = iter.next();    
	         if (set.add(element))    
	            newList.add(element);    
	      }     
	     list.clear();    
	     list.addAll(newList);    
	} 

	public static void main(String[] args) {
		 //add()和remove()方法在失败的时候会抛出异常(不推荐)
		List<String> queue = Lists.newArrayList();
        //添加元素
        queue.add("a");
        queue.add("a");
        queue.add("b");
        queue.add("c");
        queue.add("d");
        queue.add("e");
        
        removeDuplicateWithOrder(queue);
        for(String q : queue){
            System.out.println(q);
        }
        System.out.println("===");
        System.out.println("poll="+queue.remove(0)); //返回第一个元素，并在队列中删除
        for(String q : queue){
            System.out.println(q);
        }
      

	}

}
